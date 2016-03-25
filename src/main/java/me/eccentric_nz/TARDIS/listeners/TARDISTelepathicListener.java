/*
 * Copyright (C) 2016 eccentric_nz
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
package me.eccentric_nz.TARDIS.listeners;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import me.eccentric_nz.TARDIS.TARDIS;
import me.eccentric_nz.TARDIS.database.ResultSetControls;
import me.eccentric_nz.TARDIS.database.ResultSetPlayerPrefs;
import me.eccentric_nz.TARDIS.database.ResultSetTardis;
import me.eccentric_nz.TARDIS.utility.TARDISMessage;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author eccentric_nz
 */
public class TARDISTelepathicListener implements Listener {

    private final TARDIS plugin;

    public TARDISTelepathicListener(TARDIS plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTelepathicCircuit(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (!block.getType().equals(Material.DAYLIGHT_DETECTOR)) {
            return;
        }
        String location = block.getLocation().toString();
        // get tardis from saved location
        HashMap<String, Object> where = new HashMap<String, Object>();
        where.put("type", 23);
        where.put("location", location);
        ResultSetControls rsc = new ResultSetControls(plugin, where, false);
        if (rsc.resultSet()) {
            int id = rsc.getTardis_id();
            // get the Time Lord of this TARDIS
            HashMap<String, Object> wheret = new HashMap<String, Object>();
            wheret.put("tardis_id", id);
            ResultSetTardis rs = new ResultSetTardis(plugin, wheret, "", false);
            if (rs.resultSet()) {
                UUID o_uuid = rs.getUuid();
                String owner = o_uuid.toString();
                // get Time Lord player prefs
                HashMap<String, Object> wherep = new HashMap<String, Object>();
                wherep.put("uuid", owner);
                ResultSetPlayerPrefs rsp = new ResultSetPlayerPrefs(plugin, wherep);
                if (rsp.resultSet()) {
                    final Player player = event.getPlayer();
                    UUID uuid = player.getUniqueId();
                    if (rsp.isTelepathyOn()) {
                        // track player
                        plugin.getTrackerKeeper().getTelepaths().put(uuid, o_uuid);
                        TARDISMessage.send(player, "TELEPATHIC_COMMAND");
                    } else {
                        TARDISMessage.send(player, "TELEPATHIC_OFF");
                    }
                }
            }
            block.setType(Material.DAYLIGHT_DETECTOR);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onTelepathicCircuitBreak(BlockBreakEvent event) {
        Block b = event.getBlock();
        if (!b.getType().equals(Material.DAYLIGHT_DETECTOR)) {
            return;
        }
        // check location
        HashMap<String, Object> where = new HashMap<String, Object>();
        where.put("type", 23);
        where.put("location", b.getLocation().toString());
        ResultSetControls rsc = new ResultSetControls(plugin, where, false);
        if (!rsc.resultSet()) {
            return;
        }
        event.setCancelled(true);
        // set block to AIR
        b.setType(Material.AIR);
        // drop a custom DAYLIGHT_DETECTOR
        ItemStack is = new ItemStack(Material.DAYLIGHT_DETECTOR, 1);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName("TARDIS Telepathic Circuit");
        im.setLore(Arrays.asList("Allow companions to", "use TARDIS commands"));
        is.setItemMeta(im);
        b.getWorld().dropItemNaturally(b.getLocation(), is);
    }

    @EventHandler(ignoreCancelled = true)
    public void onTelepathicCircuitPlace(BlockPlaceEvent event) {
        ItemStack is = event.getItemInHand();
        if (!is.getType().equals(Material.DAYLIGHT_DETECTOR) || !is.hasItemMeta()) {
            return;
        }
        ItemMeta im = is.getItemMeta();
        if (im.hasDisplayName() && im.getDisplayName().equals("TARDIS Telepathic Circuit")) {
            UUID uuid = event.getPlayer().getUniqueId();
            String l = event.getBlock().getLocation().toString();
            plugin.getTrackerKeeper().getTelepathicPlacements().put(uuid, l);
        }
    }
}
