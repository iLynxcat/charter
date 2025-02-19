package me.ilynxcat.charter.utils

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import java.util.concurrent.CompletableFuture

@Suppress("UnstableApiUsage")
internal class CustomArgumentSuggestions {
	companion object {
		fun suggestOnlinePlayers(
			ctx: CommandContext<CommandSourceStack>,
			builder: SuggestionsBuilder
		): CompletableFuture<Suggestions> {
			for (player in ctx.source.sender.server.onlinePlayers)
				builder.suggest(player.name)
			return builder.buildFuture()
		}
	}
}