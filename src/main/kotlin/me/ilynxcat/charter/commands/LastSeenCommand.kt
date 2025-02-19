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
import me.ilynxcat.charter.utils.CustomArgumentSuggestions
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Suppress("UnstableApiUsage")
internal class LastSeenCommand(private val plugin: CharterPlugin) : ICharterCommand {
	override val shouldEnable: Boolean
		get() = plugin.config.playerTimeSeenEnabled
	override val root: LiteralCommandNode<CommandSourceStack>
	override val aliases = setOf<String>("lastseen", "lastjoin")

	init {
		root = Commands.literal("seen")
			.requires { shouldEnable && it.sender.hasPermission(CharterPermission.Seen) }
			.executes { readoutLastJoin(it) }
			.then(
				Commands.argument("target", StringArgumentType.word())
					.suggests(CustomArgumentSuggestions.Companion::suggestOnlinePlayers)
					.executes { readoutLastJoin(it) }
			)
			.build()
	}

	private fun readoutLastJoin(ctx: CommandContext<CommandSourceStack>): Int {
		val sender = ctx.source.sender
		val targetName: String = try {
			ctx.getArgument("target", String::class.java)
		} catch (e: IllegalArgumentException) {
			if (sender is Player) sender.name
			else {
				sender.sendRichMessage("<red>No target player provided!")
				return 0
			}
		}
		val target: OfflinePlayer? =
			plugin.server.getPlayer(targetName) ?: plugin.server.getOfflinePlayerIfCached(targetName)

		if (target == null) {
			sender.sendRichMessage(
				"<red>Player <yellow><player></yellow> has never been online or cannot be found!",
				Placeholder.unparsed("player", targetName)
			)
			return 0
		}

		val isOnline = target.isOnline
		val lastJoin = Instant.ofEpochMilli(target.lastLogin)
		val lastOnline = Instant.ofEpochMilli(target.lastSeen)

		sender.sendRichMessage(
			"<gray>Player <yellow><player></yellow> has been <status> since <yellow><since> ago</yellow>.",
			Placeholder.parsed(
				"status",
				if (isOnline) "<green>online</green>"
				else "<red>offline</red>"
			),
			Placeholder.component(
				"player",
				if (target is Player)
					target.displayName()
				else
					Component.text(target.name ?: targetName)
			),
			Placeholder.unparsed(
				"since", formatDuration(
					(if (isOnline) lastJoin else lastOnline).atZone(ZoneId.systemDefault()).toLocalDateTime(),
					LocalDateTime.now()
				)
			)
		)

		return Command.SINGLE_SUCCESS
	}

	companion object {
		fun formatDuration(joinDate: LocalDateTime, now: LocalDateTime): String {
			val ageDuration = Duration.between(joinDate, now)

			return String.format(
				"%dd %dh %dm %ds",
				ageDuration.toDaysPart(),
				ageDuration.toHoursPart(),
				ageDuration.toMinutesPart(),
				ageDuration.toSecondsPart(),
			)
		}
	}
}
