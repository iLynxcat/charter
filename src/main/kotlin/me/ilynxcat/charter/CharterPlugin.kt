package me.ilynxcat.charter

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import me.ilynxcat.charter.commands.*
import me.ilynxcat.charter.events.FlightSafetyListener
import me.ilynxcat.charter.events.ICharterListener
import me.ilynxcat.charter.events.PlayerWelcomeListener
import me.ilynxcat.charter.managers.ChatModerationManager
import me.ilynxcat.charter.managers.FlightSafetyManager
import org.bukkit.plugin.java.JavaPlugin

class CharterPlugin : JavaPlugin() {
	val chatModeration = ChatModerationManager(this)

	internal val config = CharterConfiguration(this)
	internal val flightSafety = FlightSafetyManager(this)

	private val events: Array<ICharterListener> = arrayOf(
		PlayerWelcomeListener(this),
		FlightSafetyListener(this)
	)
	private val commands: Array<ICharterCommand> = arrayOf(
		BaseCommand(this),
		BroadcastCommand(this),
		BroadcastRawCommand(this),
		RulesCommand(this),
		FlyCommand(this),
		GameModeCommand(this),
		AgeCommand(this),
		LastSeenCommand(this),
	)

	override fun onEnable() {
		config.saveDefault()

		@Suppress("UnstableApiUsage")
		lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
			commands
				.forEach { event.registrar().register(it.root, it.aliases) }
		}

		events.filter { it.shouldEnable }
			.forEach { server.pluginManager.registerEvents(it, this) }

		logger.info("${pluginMeta.displayName} is enabled!")
	}

	override fun onDisable() {}

	private fun setupDatabase() {
		TODO("Implement database setup")
	}
}
