package me.ilynxcat.charter.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import me.ilynxcat.charter.CharterPermission
import me.ilynxcat.charter.CharterPlugin
import me.ilynxcat.charter.hasPermission
import me.ilynxcat.charter.permission
import me.ilynxcat.charter.utils.CustomArgumentSuggestions
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.GameMode
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

@Suppress("UnstableApiUsage")
internal class GameModeCommand(private val plugin: CharterPlugin) : ICharterCommand {
	override val shouldEnable: Boolean
		get() = true
	override val root: LiteralCommandNode<CommandSourceStack>
	override val aliases = setOf("gm")

	init {
		root = Commands.literal("gamemode")
			.executes { readoutGameMode(it) }
			.then(
				Commands.argument("mode", ArgumentTypes.gameMode())
					.suggests(GameModeCommand::suggestGameModes)
					.executes { executeGameMode(it) }
					.then(
						Commands.argument("target", ArgumentTypes.player())
							.suggests(CustomArgumentSuggestions::suggestOnlinePlayers)
							.executes { executeGameMode(it) })
			)
			.requires { shouldEnable && it.sender.hasPermission(CharterPermission.GameMode) }
			.build()
	}

	private fun readoutGameMode(ctx: CommandContext<CommandSourceStack>): Int {
		val sender = ctx.source.sender
		if (sender !is Player) {
			sender.sendRichMessage("<red>You must be a player to read game mode!")
			return 0
		}
		sender.sendRichMessage(
			"<gray>You are in <yellow><mode></yellow> mode.",
			Placeholder.unparsed("mode", sender.gameMode.name.lowercase())
		)
		return Command.SINGLE_SUCCESS
	}

	private fun executeGameMode(ctx: CommandContext<CommandSourceStack>): Int {
		val sender = ctx.source.sender
		val mode = ctx.getArgument("mode", GameMode::class.java)
		val target: Player = try {
			ctx.getArgument("target", PlayerSelectorArgumentResolver::class.java)
				.resolve(ctx.source).first()
		} catch (e: IllegalArgumentException) {
			if (sender is Player) sender
			else {
				sender.sendRichMessage("<red>You must specify a target!")
				return 0
			}
		}

		updateGameMode(mode, target)
		if (target != sender) {
			sender.sendRichMessage(
				"<gray>Updated <player>'s game mode to <yellow><mode></yellow>.",
				Placeholder.component("player", target.displayName()),
				Placeholder.unparsed("mode", mode.name.lowercase())
			)
		}

		return Command.SINGLE_SUCCESS
	}

	private fun updateGameMode(mode: GameMode, target: Player) {
		target.gameMode = mode
		target.sendRichMessage(
			"<gray>Your game mode is now <yellow><mode></yellow>.",
			Placeholder.unparsed("mode", mode.name.lowercase())
		)
	}

	private companion object {
		fun suggestGameModes(
			ctx: CommandContext<CommandSourceStack>,
			builder: SuggestionsBuilder
		): CompletableFuture<Suggestions> {
			for (gameMode in GameMode.entries) {
				if (!ctx.source.sender.hasPermission(gameMode.permission)) continue
				builder.suggest(gameMode.name.lowercase())
				// TODO: implement aliases in the actual command, probably register a custom enum arg type
				// gameMode.aliases.forEach { builder.suggest(it) }
			}
			return builder.buildFuture()
		}
	}
}

internal val GameMode.aliases: List<String>
	get() = when (this) {
		GameMode.SURVIVAL -> listOf("survival", "s", "su")
		GameMode.CREATIVE -> listOf("creative", "c")
		GameMode.ADVENTURE -> listOf("adventure", "a")
		GameMode.SPECTATOR -> listOf("spectator", "sp")
	}
