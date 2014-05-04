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
package me.eccentric_nz.TARDIS.move;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import me.eccentric_nz.TARDIS.TARDIS;
import me.eccentric_nz.TARDIS.database.QueryFactory;
import me.eccentric_nz.TARDIS.database.ResultSetCompanions;
import me.eccentric_nz.TARDIS.database.ResultSetPlayerPrefs;
import me.eccentric_nz.TARDIS.utility.TARDISMessage;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 *
 * @author eccentric_nz
 */
public class TARDISMoveListener implements Listener {

    private final TARDIS plugin;

    public TARDISMoveListener(TARDIS plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMoveToFromTARDIS(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (!plugin.getTrackerKeeper().getTrackMover().contains(p.getUniqueId())) {
            return;
        }
        Location l = new Location(event.getTo().getWorld(), event.getTo().getBlockX(), event.getTo().getBlockY(), event.getTo().getBlockZ(), 0.0f, 0.0f);
        Location loc = p.getLocation(); // Grab Location

        /**
         * Copyright (c) 2011, The Multiverse Team All rights reserved. Check
         * the Player has actually moved a block to prevent unneeded
         * calculations... This is to prevent huge performance drops on high
         * player count servers.
         */
        TARDISMoveSession tms = plugin.getTrackerKeeper().getTARDISMoveSession(p);
        tms.setStaleLocation(loc);

        // If the location is stale, ie: the player isn't actually moving xyz coords, they're looking around
        if (tms.isStaleLocation()) {
            return;
        }
        // check the block they're on
        if (plugin.getTrackerKeeper().getTrackPortals().containsKey(l)) {
            TARDISTeleportLocation tpl = plugin.getTrackerKeeper().getTrackPortals().get(l);
            UUID uuid = p.getUniqueId();
            int id = tpl.getTardisId();
            // are they a companion of this TARDIS?
            List<UUID> companions = new ResultSetCompanions(plugin, id).getCompanions();
            if (companions.contains(uuid)) {
                Location to = tpl.getLocation();
                HashMap<String, Object> wherepp = new HashMap<String, Object>();
                wherepp.put("uuid", uuid.toString());
                ResultSetPlayerPrefs rsp = new ResultSetPlayerPrefs(plugin, wherepp);
                boolean hasPrefs = rsp.resultSet();
                boolean minecart = (hasPrefs) ? rsp.isMinecartOn() : false;
                boolean userQuotes = (hasPrefs) ? rsp.isQuotesOn() : false;
                boolean exit = !(to.getWorld().getName().contains("TARDIS"));
                // set travelling status
                QueryFactory qf = new QueryFactory(plugin);
                if (exit) {
                    // unoccupied
                    plugin.getGeneralKeeper().getDoorListener().removeTraveller(uuid);
                } else {
                    // occupied
                    plugin.getGeneralKeeper().getDoorListener().removeTraveller(uuid);
                    HashMap<String, Object> set = new HashMap<String, Object>();
                    set.put("tardis_id", id);
                    set.put("uuid", uuid.toString());
                    qf.doSyncInsert("travellers", set);
                }
                // tp player
                plugin.getGeneralKeeper().getDoorListener().movePlayer(p, to, exit, l.getWorld(), userQuotes, 0, minecart);
                if (userQuotes) {
                    TARDISMessage.send(p, plugin.getPluginName() + "Don't forget to close the door!");
                }
            }
        }
    }
}