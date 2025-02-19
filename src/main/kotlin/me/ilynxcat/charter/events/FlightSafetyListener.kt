package me.ilynxcat.charter.events

import me.ilynxcat.charter.CharterPlugin
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerToggleFlightEvent

internal class FlightSafetyListener(private val plugin: CharterPlugin) : Listener, ICharterListener {
	override val shouldEnable: Boolean
		get() = plugin.config.flightSafeLandingEnabled

	@EventHandler
	fun onPlayerJoin(event: PlayerJoinEvent) {
		plugin.flightSafety.startSafeLanding(event.player)
	}

	@EventHandler
	fun onPlayerGameModeChange(event: PlayerGameModeChangeEvent) {
		if (event.newGameMode == GameMode.CREATIVE || event.newGameMode == GameMode.SPECTATOR)
			plugin.flightSafety.stopSafeLanding(event.player)
	}

	@EventHandler
	fun onPlayerToggleFlight(event: PlayerToggleFlightEvent) {
		if (event.isFlying) plugin.flightSafety.stopSafeLanding(event.player)
	}
}
