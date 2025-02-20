package me.ilynxcat.charter

import org.bukkit.configuration.file.FileConfiguration

internal data class CharterConfiguration(private val plugin: CharterPlugin) {
	private val config = plugin.getConfig()

	fun saveDefault() {
		config.addDefault("welcome-new-players.enabled", true)
		config.addDefault("welcome-new-players.message", "<light_purple>Welcome <player> to the server!")
		config.addDefault("broadcast.enabled", true)
		config.addDefault("broadcast.format", "<gold>[<b>Broadcast</b>]</gold> <message>")
		config.addDefault("rules.enabled", true)
		config.addDefault("rules.send-on-first-join", false)
		config.addDefault(
			"rules.list", listOf(
				"<gray>[<num>]</gray> <red><bold>DO NOT</bold></red> Steal from or destroy other players' stuff",
				"<gray>[<num>]</gray> <green><bold>DO</bold></green> Be respectful to others",
				"<gray>[<num>]</gray> <green><bold>DO</bold></green> Enjoy your stay, <player>!",
			)
		)
		config.addDefault("flight.enabled", true)
		config.addDefault("flight.safe-landing.enabled", true)
		config.addDefault("flight.safe-landing.sounds", false)
		config.addDefault("player-time.enabled", true)
		config.addDefault("player-time.age.enabled", true)
		config.addDefault("player-time.seen.enabled", true)
		config.addDefault("moderation.enabled", true)
		config.addDefault("moderation.mute.enabled", true)
		config.options().copyDefaults(true)
		plugin.saveConfig()
	}

	fun reload() {
		plugin.reloadConfig()
		saveDefault()
		plugin.server.onlinePlayers.forEach { it.updateCommands() }
	}

	val welcomeNewPlayersEnabled: Boolean
		get() = config.getBoolean("welcome-new-players.enabled")
	val welcomeNewPlayersMessage: String
		get() = config.getString("welcome-new-players.message")!!

	val broadcastEnabled: Boolean
		get() = config.getBoolean("broadcast.enabled")
	val broadcastFormat: String
		get() = config.getString("broadcast.format")!!

	val rulesEnabled: Boolean
		get() = config.getBoolean("rules.enabled")
	val rulesList: List<String>
		get() = config.getStringList("rules.list")
	val rulesSendOnFirstJoin: Boolean
		get() = rulesEnabled && config.getBoolean("rules.send-on-first-join")

	val flightEnabled: Boolean
		get() = config.getBoolean("flight.enabled")
	val flightSafeLandingEnabled: Boolean
		get() = config.getBoolean("flight.safe-landing.enabled")
	val flightSafeLandSoundsEnabled: Boolean
		get() = config.getBoolean("flight.safe-landing.sounds")

	val playerTimeEnabled: Boolean
		get() = config.getBoolean("player-time.enabled")
	val playerTimeAgeEnabled: Boolean
		get() = playerTimeEnabled && config.getBoolean("player-time.age.enabled")
	val playerTimeSeenEnabled: Boolean
		get() = playerTimeEnabled && config.getBoolean("player-time.seen.enabled")

	val moderationEnabled: Boolean
		get() = config.getBoolean("moderation.enabled")
	val moderationMuteEnabled: Boolean
		get() = moderationEnabled && config.getBoolean("moderation.mute.enabled")
}
