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

@Suppress("UnstableApiUsage")
internal class BroadcastRawCommand(private val plugin: CharterPlugin) : ICharterCommand {
	override val shouldEnable: Boolean
		get() = plugin.config.broadcastEnabled
	override val root: LiteralCommandNode<CommandSourceStack>
	override val aliases = setOf("tellallraw", "serversayraw")

	init {
		root = Commands.literal("broadcastraw")
			.requires { shouldEnable && it.sender.hasPermission(CharterPermission.BroadcastRaw) }
			.then(
				Commands.argument("message", StringArgumentType.greedyString())
					.executes { executeBroadcast(it) })
			.build()
	}

	private fun executeBroadcast(ctx: CommandContext<CommandSourceStack>): Int {
		val message = StringArgumentType.getString(ctx, "message")
		val formattedMessage = formatRules(message)
		plugin.server.broadcast(formattedMessage)
		return Command.SINGLE_SUCCESS
	}
}

private fun formatRules(message: String): Component {
	return miniMessage.deserialize(message)
}
