package me.ilynxcat.charter.commands

import com.mojang.brigadier.Command
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
internal class BaseCommand(private val plugin: CharterPlugin) : ICharterCommand {
	override val shouldEnable: Boolean
		get() = true
	override val root: LiteralCommandNode<CommandSourceStack>
	override val aliases = setOf("ch")

	init {
		root = Commands.literal("charter")
			.requires { it.sender.hasPermission(CharterPermission.Version) }
			.executes { sendVersion(it) }
			.then(
				Commands.literal("version")
					.requires { it.sender.hasPermission(CharterPermission.Version) }
					.executes { sendVersion(it) })
			.then(
				Commands.literal("reload")
					.requires { it.sender.hasPermission(CharterPermission.Reload) }
					.executes { doReload(it) }
			)
			.build()
	}

	private fun sendVersion(ctx: CommandContext<CommandSourceStack>): Int {
		ctx.source.sender.sendRichMessage(
			"<gray><plugin> version <green><version></green>",
			Placeholder.unparsed("plugin", plugin.pluginMeta.name),
			Placeholder.unparsed("version", plugin.pluginMeta.version)
		)
		return Command.SINGLE_SUCCESS
	}

	private fun doReload(ctx: CommandContext<CommandSourceStack>): Int {
		plugin.config.reload()
		ctx.source.sender.sendRichMessage(
			"<gray>Reloaded <green><plugin></green> configuration",
			Placeholder.unparsed("plugin", plugin.pluginMeta.name)
		)
		return Command.SINGLE_SUCCESS
	}
}
