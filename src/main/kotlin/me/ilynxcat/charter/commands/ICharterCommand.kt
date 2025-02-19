package me.ilynxcat.charter.commands

import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack

@Suppress("UnstableApiUsage")
internal interface ICharterCommand {
	val shouldEnable: Boolean
	val root: LiteralCommandNode<CommandSourceStack>
	val aliases: Set<String>
}
