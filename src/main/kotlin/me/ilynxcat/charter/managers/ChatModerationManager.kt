package me.ilynxcat.charter.managers

import me.ilynxcat.charter.CharterPlugin
import java.util.UUID

class ChatModerationManager(private val plugin: CharterPlugin) {
	// TODO: save to sqlite db

	fun mute(playerId: UUID) {}
	fun unmute(playerId: UUID) {}
	fun ignore(source: UUID, target: UUID) {}
	fun unignore(source: UUID, target: UUID) {}
	fun ignorePhrase(source: UUID, phrase: String) {}
	fun unignorePhrase(source: UUID, phrase: String) {}

	internal fun registerMute(playerId: UUID) {}
	internal fun deregisterMute(playerId: UUID) {}
	internal fun registerIgnore(source: UUID, target: UUID) {}
	internal fun deregisterIgnore(source: UUID, target: UUID) {}
	internal fun registerIgnoredPhrase(source: UUID, phrase: String) {}
	internal fun deregisterIgnoredPhrase(source: UUID, phrase: String) {}
}
