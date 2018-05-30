/*
 * Copyright (C) 2018 eccentric_nz
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
package me.eccentric_nz.TARDIS.handles;

import me.eccentric_nz.TARDIS.TARDIS;
import me.eccentric_nz.TARDIS.database.ResultSetAreas;
import me.eccentric_nz.TARDIS.database.ResultSetDestinations;
import me.eccentric_nz.TARDIS.database.ResultSetTardisID;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TARDISHandlesRequest {

    private final TARDIS plugin;

    public TARDISHandlesRequest(TARDIS plugin) {
        this.plugin = plugin;
    }

    public void process(UUID uuid, String chat) {
        List<String> split = Arrays.asList(chat.toLowerCase().split(" "));
        if (split.contains("craft")) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null && player.isOnline()) {
                player.performCommand("tardisrecipe " + " ");
            }
            return;
        }
        if (split.contains("remind")) {

        }
        if (split.contains("say")) {
            plugin.getServer().dispatchCommand(plugin.getConsole(), "handles say " + uuid.toString() + " " + chat);
        }
        if (split.contains("name")) {
            plugin.getServer().dispatchCommand(plugin.getConsole(), "handles name " + uuid.toString());
        }
        if (split.contains("time")) {
            plugin.getServer().dispatchCommand(plugin.getConsole(), "handles time " + uuid.toString());
        }
        HashMap<String, Object> where = new HashMap<>();
        where.put("uuid", uuid.toString());
        ResultSetTardisID rs = new ResultSetTardisID(plugin);
        if (rs.fromUUID(uuid.toString())) {
            int id = rs.getTardis_id();
            if (split.contains("call")) {
                plugin.getServer().dispatchCommand(plugin.getConsole(), "handles call " + uuid.toString() + " " + id);
            }
            if (split.contains("takeoff")) {
                plugin.getServer().dispatchCommand(plugin.getConsole(), "handles takeoff " + uuid.toString() + " " + id);
            }
            if (split.contains("land")) {
                plugin.getServer().dispatchCommand(plugin.getConsole(), "handles land " + uuid.toString() + " " + id);
            }
            if (split.contains("lock")) {

            }
            if (split.contains("unlock")) {

            }
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null && player.isOnline()) {
                if (split.contains("hide")) {
                    player.performCommand("tardis hide");
                }
                if (split.contains("rebuild")) {
                    player.performCommand("tardis rebuild");
                }
                if (split.contains("teleport")) {

                }
                if (split.contains("travel")) {
                    if (split.contains("save")) {
                        HashMap<String, Object> wheres = new HashMap<>();
                        wheres.put("tardis_id", id);
                        ResultSetDestinations rsd = new ResultSetDestinations(plugin, wheres, true);
                        if (rsd.resultSet()) {
                            for (HashMap<String, String> map : rsd.getData()) {
                                String dest = map.get("dest_name");
                                if (split.contains(dest) && player.hasPermission("tardis.timetravel")) {
                                    player.performCommand("tardistravel " + "");
                                    return;
                                }
                            }
                        }
                    }
                    if (split.contains("player")) {
                        for (Player p : plugin.getServer().getOnlinePlayers()) {
                            String name = p.getName();
                            if (split.contains(name) && player.hasPermission("tardis.timetravel.player")) {
                                player.performCommand("tardistravel " + name);
                                return;
                            }
                        }
                    }
                    if (split.contains("area")) {
                        ResultSetAreas rsa = new ResultSetAreas(plugin, null, false, true);
                        if (rsa.resultSet()) {
                            // cycle through areas
                            rsa.getNames().forEach((name) -> {
                                if (split.contains(name) && (player.hasPermission("tardis.area." + name) || player.hasPermission("tardis.area.*"))) {
                                    player.performCommand("tardistravel " + name);
                                    return;
                                }
                            });
                        }
                    }
                    if (split.contains("scan")) {
                        plugin.getServer().dispatchCommand(plugin.getConsole(), "handles scan " + uuid.toString());
                    }
                }
            }
        }
    }
}
