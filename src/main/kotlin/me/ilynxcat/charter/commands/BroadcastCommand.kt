package me.ilynxcat.charter.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import me.ilynxcat.charter.CharterPermission
import me.ilynxcat.charter.CharterPlugin
import me.ilynxcat.charter.hasPermission
import me.ilynxcat.charter.utils.miniMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder

@Suppress("UnstableApiUsage")
internal class BroadcastCommand(private val plugin: CharterPlugin) : ICharterCommand {
	override val shouldEnable: Boolean
		get() = plugin.config.broadcastEnabled
	override val root: LiteralCommandNode<CommandSourceStack>
	override val aliases = setOf("tellall", "serversay")

	init {
		root = Commands.literal("broadcast")
			.requires { shouldEnable && it.sender.hasPermission(CharterPermission.Broadcast) }
			.then(
				Commands.argument("message", StringArgumentType.greedyString())
					.executes { executeBroadcast(it) })
			.build()
	}

	private fun executeBroadcast(ctx: CommandContext<CommandSourceStack>): Int {
		val message = StringArgumentType.getString(ctx, "message")
		val formattedMessage = formatRules(plugin, message)
		plugin.server.broadcast(formattedMessage)
		return Command.SINGLE_SUCCESS
	}
}

private fun formatRules(plugin: CharterPlugin, message: String): Component {
	return miniMessage.deserialize(
		plugin.config.broadcastFormat,
		Placeholder.component("message", Component.text(message))
	)
}
