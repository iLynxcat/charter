package me.ilynxcat.charter.events

import io.papermc.paper.event.player.AsyncChatEvent
import me.ilynxcat.charter.CharterPlugin
import org.bukkit.event.EventHandler

internal class ChatModerationListener(private val plugin: CharterPlugin): ICharterListener {
	override val shouldEnable: Boolean
		get() = plugin.config.moderationEnabled

	private val mutes = plugin.chatModeration

	@EventHandler
	fun onPlayerChat(event: AsyncChatEvent) {

	}
}