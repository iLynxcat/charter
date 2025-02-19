package me.ilynxcat.charter.events

import me.ilynxcat.charter.CharterPlugin
import me.ilynxcat.charter.utils.miniMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

internal class PlayerWelcomeListener(private val plugin: CharterPlugin) : Listener, ICharterListener {
	override val shouldEnable: Boolean
		get() = plugin.config.welcomeNewPlayersEnabled

	@EventHandler
	fun onPlayerFirstJoin(event: PlayerJoinEvent) {
		val player = event.player
		if (player.hasPlayedBefore()) return

		if (plugin.config.rulesSendOnFirstJoin) TODO("Implement rules on first join")

		event.joinMessage(welcomeMessage(plugin, player.displayName()))
	}
}

private fun welcomeMessage(plugin: CharterPlugin, player: ComponentLike): Component {
	return miniMessage.deserialize(
		plugin.config.welcomeNewPlayersMessage,
		Placeholder.component("player", player)
	)
}
