/*
 * Copyright (C) 2013 eccentric_nz
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import me.eccentric_nz.TARDIS.TARDIS;
import me.eccentric_nz.TARDIS.commands.admin.TARDISAdminMenuInventory;
import me.eccentric_nz.TARDIS.database.ResultSetDoors;
import me.eccentric_nz.TARDIS.database.ResultSetTravellers;
import me.eccentric_nz.TARDIS.utility.TARDISVector3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author eccentric_nz
 */
public class TARDISSonicListener implements Listener {

    private final TARDIS plugin;
    private final Material sonic;
    private final HashMap<String, Long> timeout = new HashMap<String, Long>();
    private final List<Material> doors = new ArrayList<Material>();
    private final List<String> frozenPlayers = new ArrayList<String>();

    public TARDISSonicListener(TARDIS plugin) {
        this.plugin = plugin;
        String[] split = plugin.getRecipesConfig().getString("shaped.Sonic Screwdriver.result").split(":");
        this.sonic = Material.valueOf(split[0]);
        doors.add(Material.WOODEN_DOOR);
        doors.add(Material.IRON_DOOR_BLOCK);
        doors.add(Material.TRAP_DOOR);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        long now = System.currentTimeMillis();
        final ItemStack is = player.getItemInHand();
        if (is.getType().equals(sonic) && is.hasItemMeta()) {
            ItemMeta im = player.getItemInHand().getItemMeta();
            if (im.getDisplayName().equals("Sonic Screwdriver")) {
                List<String> lore = im.getLore();
                Action action = event.getAction();
                if (action.equals(Action.RIGHT_CLICK_AIR)) {
                    playSonicSound(player, now);
                    if (player.hasPermission("tardis.admin") && lore != null && lore.contains("Admin Upgrade")) {
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                ItemStack[] items = new TARDISAdminMenuInventory(plugin).getMenu();
                                Inventory menu = plugin.getServer().createInventory(player, 54, "§4Admin Menu");
                                menu.setContents(items);
                                player.openInventory(menu);
                            }
                        }, 40L);
                    }
                    if (player.hasPermission("tardis.sonic.bio") && lore != null && lore.contains("Bio-scanner Upgrade")) {
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                Location observerPos = player.getEyeLocation();
                                TARDISVector3D observerDir = new TARDISVector3D(observerPos.getDirection());

                                TARDISVector3D observerStart = new TARDISVector3D(observerPos);
                                TARDISVector3D observerEnd = observerStart.add(observerDir.multiply(16));

                                Player hit = null;

                                // Get nearby entities
                                for (Player target : player.getWorld().getPlayers()) {
                                    // Bounding box of the given player
                                    TARDISVector3D targetPos = new TARDISVector3D(target.getLocation());
                                    TARDISVector3D minimum = targetPos.add(-0.5, 0, -0.5);
                                    TARDISVector3D maximum = targetPos.add(0.5, 1.67, 0.5);
                                    if (target != player && hasIntersection(observerStart, observerEnd, minimum, maximum)) {
                                        if (hit == null || hit.getLocation().distanceSquared(observerPos) > target.getLocation().distanceSquared(observerPos)) {
                                            hit = target;
                                        }
                                    }
                                }
                                // freeze the closest player
                                if (hit != null) {
                                    hit.sendMessage(plugin.pluginName + player.getName() + " froze you with their Sonic Screwdriver!");
                                    final String hitNme = hit.getName();
                                    frozenPlayers.add(hitNme);
                                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                        @Override
                                        public void run() {
                                            frozenPlayers.remove(hitNme);
                                        }
                                    }, 100L);
                                }
                            }
                        }, 20L);
                    }
                }
                if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
                    final Block b = event.getClickedBlock();
                    if (doors.contains(b.getType()) && player.hasPermission("tardis.admin") && lore != null && lore.contains("Admin Upgrade")) {
                        playSonicSound(player, now);
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                HashMap<String, Object> wheredoor = new HashMap<String, Object>();
                                Location loc = b.getLocation();
                                String bw = loc.getWorld().getName();
                                int bx = loc.getBlockX();
                                int by = loc.getBlockY();
                                int bz = loc.getBlockZ();
                                if (b.getData() >= 8 && !b.getType().equals(Material.TRAP_DOOR)) {
                                    by = (by - 1);
                                }
                                String doorloc = bw + ":" + bx + ":" + by + ":" + bz;
                                wheredoor.put("door_location", doorloc);
                                wheredoor.put("door_type", 0);
                                ResultSetDoors rsd = new ResultSetDoors(plugin, wheredoor, false);
                                if (rsd.resultSet()) {
                                    int id = rsd.getTardis_id();
                                    HashMap<String, Object> whereid = new HashMap<String, Object>();
                                    whereid.put("tardis_id", id);
                                    ResultSetTravellers rst = new ResultSetTravellers(plugin, whereid, true);
                                    if (rst.resultSet()) {
                                        List<String> data = rst.getData();
                                        player.sendMessage(plugin.pluginName + "The players inside this TARDIS are:");
                                        for (String s : data) {
                                            player.sendMessage(s);
                                        }
                                    } else {
                                        player.sendMessage(plugin.pluginName + "The TARDIS is unoccupied.");
                                    }
                                }
                            }
                        }, 60L);
                    }
                }
            }
        }
    }

    private void playSonicSound(final Player player, long now) {
        if ((!timeout.containsKey(player.getName()) || timeout.get(player.getName()) < now)) {
            ItemMeta im = player.getItemInHand().getItemMeta();
            im.addEnchant(Enchantment.DURABILITY, 1, true);
            player.getItemInHand().setItemMeta(im);
            timeout.put(player.getName(), now + 3050);
            plugin.utils.playTARDISSound(player.getLocation(), player, "sonic_screwdriver");
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    for (Enchantment e : player.getItemInHand().getEnchantments().keySet()) {
                        player.getItemInHand().removeEnchantment(e);
                    }
                }
            }, 60L);
        }
    }

    private boolean hasIntersection(TARDISVector3D p1, TARDISVector3D p2, TARDISVector3D min, TARDISVector3D max) {
        final double epsilon = 0.0001f;
        TARDISVector3D d = p2.subtract(p1).multiply(0.5);
        TARDISVector3D e = max.subtract(min).multiply(0.5);
        TARDISVector3D c = p1.add(d).subtract(min.add(max).multiply(0.5));
        TARDISVector3D ad = d.abs();
        if (Math.abs(c.x) > e.x + ad.x) {
            return false;
        }
        if (Math.abs(c.y) > e.y + ad.y) {
            return false;
        }
        if (Math.abs(c.z) > e.z + ad.z) {
            return false;
        }
        if (Math.abs(d.y * c.z - d.z * c.y) > e.y * ad.z + e.z * ad.y + epsilon) {
            return false;
        }
        if (Math.abs(d.z * c.x - d.x * c.z) > e.z * ad.x + e.x * ad.z + epsilon) {
            return false;
        }
        return Math.abs(d.x * c.y - d.y * c.x) <= e.x * ad.y + e.y * ad.x + epsilon;
    }

    @EventHandler
    public void onPlayerFrozenMove(PlayerMoveEvent event) {
        if (frozenPlayers.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }
}
