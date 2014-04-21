/*
 * Copyright (C) 2014 eccentric_nz
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
package me.eccentric_nz.TARDIS.commands.tardis;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import me.eccentric_nz.TARDIS.TARDIS;
import me.eccentric_nz.TARDIS.database.ResultSetTardis;
import me.eccentric_nz.TARDIS.database.ResultSetTravellers;
import me.eccentric_nz.TARDIS.enumeration.MESSAGE;
import me.eccentric_nz.TARDIS.utility.TARDISMessage;
import org.bukkit.entity.Player;

/**
 *
 * @author eccentric_nz
 */
public class TARDISInsideCommand {

    private final TARDIS plugin;

    public TARDISInsideCommand(TARDIS plugin) {
        this.plugin = plugin;
    }

    public boolean whosInside(Player player, String[] args) {
        // check they are a timelord
        HashMap<String, Object> where = new HashMap<String, Object>();
        where.put("uuid", player.getUniqueId().toString());
        ResultSetTardis rs = new ResultSetTardis(plugin, where, "", false);
        if (!rs.resultSet()) {
            TARDISMessage.send(player, plugin.getPluginName() + MESSAGE.NOT_A_TIMELORD.getText());
            return true;
        }
        int id = rs.getTardis_id();
        HashMap<String, Object> wheret = new HashMap<String, Object>();
        wheret.put("tardis_id", id);
        ResultSetTravellers rst = new ResultSetTravellers(plugin, wheret, true);
        if (rst.resultSet()) {
            List<UUID> data = rst.getData();
            TARDISMessage.send(player, plugin.getPluginName() + "The players inside your TARDIS are:");
            for (UUID s : data) {
                TARDISMessage.send(player, plugin.getServer().getPlayer(s).getDisplayName());
            }
        } else {
            TARDISMessage.send(player, plugin.getPluginName() + "Nobody is inside your TARDIS.");
        }
        return true;
    }
}