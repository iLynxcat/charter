package me.ilynxcat.charter.managers

import me.ilynxcat.charter.CharterPlugin
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.GameMode
import org.bukkit.block.Block
import org.bukkit.entity.Player
import me.ilynxcat.charter.utils.miniMessage
import java.util.*

internal class FlightSafetyManager(private val plugin: CharterPlugin) {
	private val inProgressSafeLandings: MutableSet<UUID> = mutableSetOf()

	internal fun has(player: Player): Boolean {
		return this.has(player.uniqueId)
	}

	internal fun has(uuid: UUID): Boolean {
		return inProgressSafeLandings.contains(uuid)
	}

	internal fun startSafeLanding(player: Player) {
		if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) return
		if (player.blockBeneath.isSafeForLanding) return

		inProgressSafeLandings.add(player.uniqueId)

		val playSounds = plugin.config.flightSafeLandSoundsEnabled
		var ticksFalling: Int = 0

		plugin.server.scheduler.runTaskTimer(plugin, { task ->
			if (!inProgressSafeLandings.contains(player.uniqueId)) {
				task.cancel()
				return@runTaskTimer
			}

			// TODO: make max safe fall seconds configurable
			// max 15 seconds falling
			if (ticksFalling >= 20 * 15) inProgressSafeLandings.remove(player.uniqueId)

			val block = player.blockBeneath
			if (!block.isSafeForLanding) {
				player.fallDistance = 0.7f
			} else {
				inProgressSafeLandings.remove(player.uniqueId)
				// disable the landing chime for liquids because they have their own sound effect
				if (playSounds && ticksFalling > 20 && !block.isLiquid) {
					// low chime
					player.playSound(hasLandedPingLow)
					// wait 0.15sec
					// then high chime
					plugin.server.scheduler.runTaskLater(plugin, { _ -> player.playSound(hasLandedPingHigh) }, 3)
				}
				task.cancel()
				return@runTaskTimer
			}

			// every second play a tick
			if (ticksFalling == 0 || ticksFalling % 20 == 0) {
				player.sendActionBar(miniMessage.deserialize("<gold>Landing safely"))
				if (playSounds) player.playSound(landingTick)
			}

			ticksFalling++
		}, 0, 1)
		return
	}

	internal fun stopSafeLanding(player: Player) {
		inProgressSafeLandings.remove(player.uniqueId)
	}

	companion object {
		val landingTick = Sound.sound()
			.type(Key.key("block.note_block.hat"))
			.pitch(1.5f)
			.volume(0.5f)
			.source(Sound.Source.RECORD)
			.build()
		val hasLandedPingLow = Sound.sound()
			.type(Key.key("block.note_block.bit"))
			.pitch(1.5f)
			.volume(0.5f)
			.source(Sound.Source.RECORD)
			.build()
		val hasLandedPingHigh = Sound.sound()
			.type(Key.key("block.note_block.bit"))
			.pitch(2.0f)
			.volume(0.55f)
			.source(Sound.Source.RECORD)
			.build()
	}
}

internal val Block.isSafeForLanding: Boolean
	get() = (!this.isEmpty && !this.isPassable) || this.isLiquid

internal val Player.blockBeneath: Block
	get() = this.location.subtract(0.0, 0.6, 0.0).block
