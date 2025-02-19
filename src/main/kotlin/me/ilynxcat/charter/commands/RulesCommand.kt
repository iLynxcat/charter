package me.ilynxcat.charter.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import me.ilynxcat.charter.CharterPermission
import me.ilynxcat.charter.CharterPlugin
import me.ilynxcat.charter.hasPermission
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder

@Suppress("UnstableApiUsage")
internal class RulesCommand(private val plugin: CharterPlugin) : ICharterCommand {
	override val shouldEnable: Boolean
		get() = plugin.config.rulesEnabled
	override val root: LiteralCommandNode<CommandSourceStack>
	override val aliases = setOf<String>()

	init {
		root = Commands.literal("rules")
			.requires { shouldEnable && it.sender.hasPermission(CharterPermission.Rules) }
			.executes { sendRules(it) }
			.build()
	}

	private fun sendRules(ctx: CommandContext<CommandSourceStack>): Int {
		val playerName = ctx.source.sender.name
		plugin.config.rulesList.forEachIndexed { index, ruleLine ->
			ctx.source.sender.sendRichMessage(
				ruleLine,
				Placeholder.unparsed("player", playerName),
				Placeholder.unparsed("num", (index + 1).toString())
			)
		}
		return Command.SINGLE_SUCCESS
	}
}
