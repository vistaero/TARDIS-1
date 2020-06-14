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
package me.eccentric_nz.TARDIS.commands.preferences;

import me.eccentric_nz.TARDIS.TARDIS;
import me.eccentric_nz.TARDIS.messaging.TARDISMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

/**
 * @author eccentric_nz
 */
class TARDISSetLampCommand {

    private final TARDIS plugin;

    TARDISSetLampCommand(TARDIS plugin) {
        this.plugin = plugin;
    }

    boolean setLampPref(Player player, String[] args) {
        if (args.length < 2) {
            TARDISMessage.send(player, "LAMP_NEED");
            return false;
        }
        Material lamp;
        try {
            lamp = Material.valueOf(args[1]);
        } catch (IllegalArgumentException e) {
            TARDISMessage.send(player, "LAMP_NOT_MATERIAL");
            return true;
        }
        // check it's an allowed block
        List<String> allowed_strs = plugin.getBlocksConfig().getStringList("lamp_blocks");
        if (!allowed_strs.contains(lamp.toString())) {
            TARDISMessage.send(player, "LAMP_NO_SET");
            return true;
        }
        HashMap<String, Object> setl = new HashMap<>();
        setl.put("lamp", lamp.toString());
        HashMap<String, Object> where = new HashMap<>();
        where.put("uuid", player.getUniqueId().toString());
        plugin.getQueryFactory().doUpdate("player_prefs", setl, where);
        TARDISMessage.send(player, "LAMP_SAVED");
        return true;
    }
}
