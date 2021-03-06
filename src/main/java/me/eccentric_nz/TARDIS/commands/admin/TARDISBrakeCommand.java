/*
 * Copyright (C) 2020 eccentric_nz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.eccentric_nz.TARDIS.commands.admin;

import me.eccentric_nz.TARDIS.TARDIS;
import me.eccentric_nz.TARDIS.commands.tardis.TARDISHandbrakeCommand;
import me.eccentric_nz.TARDIS.database.resultset.ResultSetTardisID;
import me.eccentric_nz.TARDIS.messaging.TARDISMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TARDISBrakeCommand {

    private final TARDIS plugin;

    public TARDISBrakeCommand(TARDIS plugin) {
        this.plugin = plugin;
    }

    public boolean toggle(CommandSender sender, String[] args) {
        if (args.length < 3) {
            TARDISMessage.send(sender, "TOO_FEW_ARGS");
            return true;
        }
        Player player = plugin.getServer().getPlayer(args[2]);
        if (player == null) {
            TARDISMessage.send(sender, "COULD_NOT_FIND_NAME");
            return true;
        }
        ResultSetTardisID rs = new ResultSetTardisID(plugin);
        if (rs.fromUUID(player.getUniqueId().toString())) {
            return new TARDISHandbrakeCommand(plugin).toggle(player, rs.getTardis_id(), args, true);
        }
        return true;
    }
}
