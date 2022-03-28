/*
 *   Catalyst - AnvilPowered
 *   Copyright (C) 2021
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package org.anvilpowered.catalyst.common.command

import com.google.inject.Inject
import com.mojang.brigadier.context.CommandContext
import org.anvilpowered.anvil.api.misc.sendTo
import org.anvilpowered.anvil.api.util.KickService
import org.anvilpowered.anvil.api.util.PermissionService
import org.anvilpowered.anvil.api.util.UserService
import org.anvilpowered.catalyst.api.plugin.PluginMessages
import org.anvilpowered.catalyst.api.registry.CatalystKeys
import org.anvilpowered.anvil.api.registry.Registry

class KickCommand<TPlayer : TCommandSource, TCommandSource> @Inject constructor(
    private val pluginMessages: PluginMessages,
    private val registry: Registry,
    private val kickService: KickService,
    private val permissionService: PermissionService,
    private val userService: UserService<TPlayer, TPlayer>
) {

    fun execute(context: CommandContext<TCommandSource>): Int {
        val player = userService.getPlayer(context.getArgument<String>("target"))
        if (player != null) {
            if (permissionService.hasPermission(player, registry.getOrDefault(CatalystKeys.KICK_EXEMPT_PERMISSION))) {
                pluginMessages.kickExempt.sendTo(context.source)
                return 0
            }
            kickService.kick(context.getArgument<String>("target"), context.getArgument<String>("reason"))
        } else {
            pluginMessages.offlineOrInvalidPlayer().sendTo(context.source)
        }
        return 1
    }

    fun withoutReason(context: CommandContext<TCommandSource>): Int {
        val reason = "You have been kicked!"
        val player = userService.getPlayer(context.getArgument<String>("target"))
        if (player != null) {
            if (permissionService.hasPermission(player, registry.getOrDefault(CatalystKeys.KICK_EXEMPT_PERMISSION))) {
                pluginMessages.kickExempt.sendTo(context.source)
                return 0
            }
            kickService.kick(userService.getUUID(player)!!, reason)
        } else {
            pluginMessages.offlineOrInvalidPlayer().sendTo(context.source)
        }
        return 1
    }
}
