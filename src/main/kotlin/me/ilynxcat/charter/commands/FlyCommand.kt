package me.ilynxcat.charter.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import me.ilynxcat.charter.CharterPermission
import me.ilynxcat.charter.CharterPlugin
import me.ilynxcat.charter.hasPermission
import me.ilynxcat.charter.utils.CustomArgumentSuggestions
import me.ilynxcat.charter.utils.miniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.GameMode
import org.bukkit.entity.Player

@Suppress("UnstableApiUsage")
internal class FlyCommand(private val plugin: CharterPlugin) : ICharterCommand {
	override val shouldEnable: Boolean
		get() = plugin.config.flightEnabled
	override val root: LiteralCommandNode<CommandSourceStack>
	override val aliases = setOf<String>()

	private val safety = plugin.flightSafety

	init {
		root = Commands.literal("fly")
			.requires { shouldEnable && it.sender.hasPermission(CharterPermission.Fly) }
			.executes { executeFly(it) }
			.then(
				Commands.argument("target", ArgumentTypes.player())
					.suggests(CustomArgumentSuggestions::suggestOnlinePlayers)
					.executes { executeFly(it) }
					.then(
						Commands.argument("enable", BoolArgumentType.bool())
							.executes { executeFly(it) })
			)
			.build()
	}

	private fun executeFly(ctx: CommandContext<CommandSourceStack>): Int {
		val sender = ctx.source.sender

		val target: Player = try {
			ctx.getArgument("target", PlayerSelectorArgumentResolver::class.java)
				.resolve(ctx.source).first()
		} catch (e: IllegalArgumentException) {
			if (sender is Player) sender
			else {
				sender.sendRichMessage("<red>No target player provided!")
				return 0
			}
		}

		val enableFlight: Boolean = try {
			ctx.getArgument("enable", Boolean::class.java)
		} catch (e: IllegalArgumentException) {
			!target.allowFlight
		}

		if (!target.canToggleFlight) {
			sender.sendRichMessage(
				"<red><player>'s flight cannot be toggled!",
				Placeholder.unparsed("player", target.name)
			)
			return 0
		}

		target.allowFlight = enableFlight

		if (enableFlight) safety.stopSafeLanding(target)
		else safety.startSafeLanding(target)

		sender.sendRichMessage(
			"<gray>Flight <state> for <player>.",
			Placeholder.component("player", target.displayName()),
			Placeholder.parsed("state", if (enableFlight) "<green>enabled</green>" else "<red>disabled</red>")
		)

		if (target != sender)
			target.sendActionBar(
				miniMessage.deserialize(
					if (enableFlight) "<green>You may now fly!" else "<red>You may no longer fly!",
				)
			)

		return Command.SINGLE_SUCCESS
	}

}

private val Player.canToggleFlight: Boolean
	get() {
		return this.gameMode == GameMode.SURVIVAL || this.gameMode == GameMode.ADVENTURE
	}
