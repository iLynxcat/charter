package me.ilynxcat.charter

import org.bukkit.GameMode
import org.bukkit.command.CommandSender

internal enum class CharterPermission(val node: String) {
	Version("charter.version"),
	Reload("charter.reload"),
	Rules("charter.rules"),
	Broadcast("charter.broadcast"),
	BroadcastRaw("charter.broadcast.raw"),
	Fly("charter.fly"),
	GameMode("charter.gamemode"),
	GameModeSurvival("charter.gamemode.survival"),
	GameModeCreative("charter.gamemode.creative"),
	GameModeAdventure("charter.gamemode.adventure"),
	GameModeSpectator("charter.gamemode.spectator"),
	Age("charter.age"),
	Seen("charter.seen"),
}

internal fun CommandSender.hasPermission(permission: CharterPermission): Boolean {
	return this.hasPermission(permission.node)
}

internal val GameMode.permission: CharterPermission
	get() =
		when (this) {
			GameMode.SURVIVAL -> CharterPermission.GameModeSurvival
			GameMode.CREATIVE -> CharterPermission.GameModeCreative
			GameMode.ADVENTURE -> CharterPermission.GameModeAdventure
			GameMode.SPECTATOR -> CharterPermission.GameModeSpectator
		}
