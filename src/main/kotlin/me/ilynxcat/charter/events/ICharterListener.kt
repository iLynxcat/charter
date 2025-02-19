package me.ilynxcat.charter.events

import org.bukkit.event.Listener

internal interface ICharterListener : Listener {
	val shouldEnable: Boolean
}
