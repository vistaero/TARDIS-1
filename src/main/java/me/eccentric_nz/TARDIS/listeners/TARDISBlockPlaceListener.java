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
package me.eccentric_nz.TARDIS.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.eccentric_nz.TARDIS.TARDIS;
import me.eccentric_nz.TARDIS.builders.TARDISMaterialisationData;
import me.eccentric_nz.TARDIS.builders.TARDISSpace;
import me.eccentric_nz.TARDIS.database.QueryFactory;
import me.eccentric_nz.TARDIS.database.ResultSetCount;
import me.eccentric_nz.TARDIS.database.ResultSetCurrentLocation;
import me.eccentric_nz.TARDIS.database.ResultSetPlayerPrefs;
import me.eccentric_nz.TARDIS.database.ResultSetTardis;
import me.eccentric_nz.TARDIS.enumeration.COMPASS;
import me.eccentric_nz.TARDIS.enumeration.MESSAGE;
import me.eccentric_nz.TARDIS.enumeration.SCHEMATIC;
import me.eccentric_nz.TARDIS.utility.TARDISMessage;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * TARDISes are bioships that are grown from a species of coral presumably
 * indigenous to Gallifrey.
 *
 * The TARDIS had a drawing room, which the Doctor claimed to be his "private
 * study". Inside it were momentos of his many incarnations' travels.
 *
 * @author eccentric_nz
 */
public class TARDISBlockPlaceListener implements Listener {

    private final TARDIS plugin;

    private final List<Material> blocks = new ArrayList<Material>();
    private Material custom;

    public TARDISBlockPlaceListener(TARDIS plugin) {
        this.plugin = plugin;
        blocks.add(Material.IRON_BLOCK); // budget
        blocks.add(Material.GOLD_BLOCK); // bigger
        blocks.add(Material.DIAMOND_BLOCK); // deluxe
        blocks.add(Material.LAPIS_BLOCK); // tom baker
        blocks.add(Material.BOOKSHELF); // wood plank
        blocks.add(Material.EMERALD_BLOCK); // eleventh
        blocks.add(Material.REDSTONE_BLOCK); // redstone
        blocks.add(Material.QUARTZ_BLOCK); // ARS
        blocks.add(Material.COAL_BLOCK); // steampunk
        if (plugin.getConfig().getBoolean("creation.custom_schematic")) {
            custom = Material.valueOf(plugin.getConfig().getString("creation.custom_schematic_seed"));
            blocks.add(custom); // custom
        }
    }

    /**
     * Listens for player block placing. If the player place a stack of blocks
     * in a certain pattern for example (but not limited to): IRON_BLOCK,
     * LAPIS_BLOCK, RESTONE_TORCH the pattern of blocks is turned into a TARDIS.
     *
     * @param event a player placing a block
     */
    @SuppressWarnings("deprecation")
    @EventHandler(ignoreCancelled = true)
    public void onPlayerBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (plugin.getTrackerKeeper().getTrackZeroRoomOccupants().contains(player.getUniqueId())) {
            event.setCancelled(true);
            TARDISMessage.send(player, plugin.getPluginName() + MESSAGE.NOT_IN_ZERO.getText());
            return;
        }
        Block block = event.getBlockPlaced();
        // only listen for redstone torches
        if (block.getType() == Material.REDSTONE_TORCH_ON) {
            Block blockBelow = block.getRelative(BlockFace.DOWN);
            int middle_id = blockBelow.getTypeId();
            byte middle_data = blockBelow.getData();
            Block blockBottom = blockBelow.getRelative(BlockFace.DOWN);
            // only continue if the redstone torch is placed on top of [JUST ABOUT ANY] BLOCK on top of an IRON/GOLD/DIAMOND_BLOCK
            if (plugin.getBlocksConfig().getStringList("tardis_blocks").contains(blockBelow.getType().toString()) && blocks.contains(blockBottom.getType())) {
                if (!plugin.getConfig().getBoolean("worlds." + block.getLocation().getWorld().getName())) {
                    TARDISMessage.send(player, plugin.getPluginName() + "You cannot create a TARDIS in this world!");
                    return;
                }
                SCHEMATIC schm;
                int max_count = plugin.getConfig().getInt("creation.count");
                int player_count = 0;
                if (max_count > 0) {
                    HashMap<String, Object> wherec = new HashMap<String, Object>();
                    wherec.put("uuid", player.getUniqueId().toString());
                    ResultSetCount rsc = new ResultSetCount(plugin, wherec, false);
                    if (rsc.resultSet()) {
                        player_count = rsc.getCount();
                        if (player_count == max_count) {
                            TARDISMessage.send(player, plugin.getPluginName() + "You have used up your quota of TARDISes!");
                            return;
                        }
                    }
                }
                switch (blockBottom.getType()) {
                    case IRON_BLOCK:
                        schm = SCHEMATIC.BUDGET;
                        break;
                    case GOLD_BLOCK:
                        if (player.hasPermission("tardis.bigger")) {
                            schm = SCHEMATIC.BIGGER;
                        } else {
                            TARDISMessage.send(player, plugin.getPluginName() + "You don't have permission to create a 'bigger' TARDIS!");
                            return;
                        }
                        break;
                    case DIAMOND_BLOCK:
                        if (player.hasPermission("tardis.deluxe")) {
                            schm = SCHEMATIC.DELUXE;
                        } else {
                            TARDISMessage.send(player, plugin.getPluginName() + "You don't have permission to create a 'deluxe' TARDIS!");
                            return;
                        }
                        break;
                    case EMERALD_BLOCK:
                        if (player.hasPermission("tardis.eleventh")) {
                            schm = SCHEMATIC.ELEVENTH;
                        } else {
                            TARDISMessage.send(player, plugin.getPluginName() + "You don't have permission to create an 'eleventh Doctor's' TARDIS!");
                            return;
                        }
                        break;
                    case REDSTONE_BLOCK:
                        if (player.hasPermission("tardis.redstone")) {
                            schm = SCHEMATIC.REDSTONE;
                        } else {
                            TARDISMessage.send(player, plugin.getPluginName() + "You don't have permission to create a 'redstone' TARDIS!");
                            return;
                        }
                        break;
                    case COAL_BLOCK:
                        if (player.hasPermission("tardis.steampunk")) {
                            schm = SCHEMATIC.STEAMPUNK;
                        } else {
                            TARDISMessage.send(player, plugin.getPluginName() + "You don't have permission to create a 'steampunk' TARDIS!");
                            return;
                        }
                        break;
                    case LAPIS_BLOCK:
                        if (player.hasPermission("tardis.tom")) {
                            schm = SCHEMATIC.TOM;
                        } else {
                            TARDISMessage.send(player, plugin.getPluginName() + "You don't have permission to create a '4th Doctor's' TARDIS!");
                            return;
                        }
                        break;
                    case BOOKSHELF:
                        if (player.hasPermission("tardis.plank")) {
                            schm = SCHEMATIC.PLANK;
                        } else {
                            TARDISMessage.send(player, plugin.getPluginName() + "You don't have permission to create a 'wood' TARDIS!");
                            return;
                        }
                        break;
                    case QUARTZ_BLOCK:
                        if (player.hasPermission("tardis.ars")) {
                            schm = SCHEMATIC.ARS;
                        } else {
                            TARDISMessage.send(player, plugin.getPluginName() + "You don't have permission to create an 'ARS' TARDIS!");
                            return;
                        }
                        break;
                    default:
                        if (plugin.getConfig().getBoolean("custom_schematic")) {
                            if (player.hasPermission("tardis.custom") && blockBottom.getType().equals(custom)) {
                                schm = SCHEMATIC.CUSTOM;
                            } else {
                                TARDISMessage.send(player, plugin.getPluginName() + "You don't have permission to create the server's custom' TARDIS!");
                                return;
                            }
                        } else {
                            schm = SCHEMATIC.BUDGET;
                        }
                        break;
                }
                if (player.hasPermission("tardis.create")) {
                    String playerNameStr = player.getName();
                    // check to see if they already have a TARDIS
                    HashMap<String, Object> where = new HashMap<String, Object>();
                    where.put("uuid", player.getUniqueId().toString());
                    ResultSetTardis rs = new ResultSetTardis(plugin, where, "", false);
                    if (!rs.resultSet()) {
                        Chunk chunk = blockBottom.getChunk();
                        // get this chunk co-ords
                        int cx;
                        int cz;
                        String cw;
                        World chunkworld;
                        boolean tips = false;
                        // TODO name worlds without player name
                        if (plugin.getConfig().getBoolean("creation.create_worlds") && !plugin.getConfig().getBoolean("creation.default_world")) {
                            // create a new world to store this TARDIS
                            cw = "TARDIS_WORLD_" + playerNameStr;
                            TARDISSpace space = new TARDISSpace(plugin);
                            chunkworld = space.getTardisWorld(cw);
                            cx = 0;
                            cz = 0;
                        } else if (plugin.getConfig().getBoolean("creation.default_world") && plugin.getConfig().getBoolean("creation.create_worlds_with_perms") && player.hasPermission("tardis.create_world")) {
                            // create a new world to store this TARDIS
                            cw = "TARDIS_WORLD_" + playerNameStr;
                            TARDISSpace space = new TARDISSpace(plugin);
                            chunkworld = space.getTardisWorld(cw);
                            cx = 0;
                            cz = 0;
                        } else {
                            // check config to see whether we are using a default world to store TARDISes
                            if (plugin.getConfig().getBoolean("creation.default_world")) {
                                cw = plugin.getConfig().getString("creation.default_world_name");
                                chunkworld = plugin.getServer().getWorld(cw);
                                tips = true;
                            } else {
                                chunkworld = chunk.getWorld();
                                cw = chunkworld.getName();
                            }
                            cx = chunk.getX();
                            cz = chunk.getZ();
                            if (!plugin.getConfig().getBoolean("creation.default_world") && plugin.getUtils().checkChunk(cw, cx, cz, schm)) {
                                TARDISMessage.send(player, plugin.getPluginName() + "A TARDIS already exists at this location, please try another chunk!");
                                return;
                            }
                        }
                        // get player direction
                        String d = plugin.getUtils().getPlayersDirection(player, false);
                        // save data to database (tardis table)
                        Location block_loc = blockBottom.getLocation();
                        String chun = cw + ":" + cx + ":" + cz;
                        QueryFactory qf = new QueryFactory(plugin);
                        HashMap<String, Object> set = new HashMap<String, Object>();
                        set.put("uuid", player.getUniqueId().toString());
                        set.put("owner", playerNameStr);
                        set.put("chunk", chun);
                        set.put("size", schm.name());
                        Long now;
                        if (player.hasPermission("tardis.prune.bypass")) {
                            now = Long.MAX_VALUE;
                        } else {
                            now = System.currentTimeMillis();
                        }
                        set.put("lastuse", now);
                        HashMap<String, Object> setpp = new HashMap<String, Object>();
                        if (middle_id == 22) {
                            set.put("middle_id", 35);
                            if (blockBottom.getType().equals(Material.EMERALD_BLOCK)) {
                                set.put("middle_data", 8);
                                setpp.put("wall", "LIGHT_GREY_WOOL");
                            } else {
                                set.put("middle_data", 1);
                                setpp.put("wall", "ORANGE_WOOL");
                            }
                        } else {
                            set.put("middle_id", middle_id);
                            set.put("middle_data", middle_data);
                            // determine wall block material from HashMap
                            setpp.put("wall", getWallKey(middle_id, (int) middle_data));
                        }
                        int lastInsertId = qf.doSyncInsert("tardis", set);
                        // insert/update  player prefs
                        HashMap<String, Object> wherep = new HashMap<String, Object>();
                        wherep.put("uuid", player.getUniqueId().toString());
                        ResultSetPlayerPrefs rsp = new ResultSetPlayerPrefs(plugin, wherep);
                        if (!rsp.resultSet()) {
                            setpp.put("uuid", player.getUniqueId().toString());
                            qf.doInsert("player_prefs", setpp);
                        } else {
                            HashMap<String, Object> wherepp = new HashMap<String, Object>();
                            wherepp.put("uuid", player.getUniqueId().toString());
                            qf.doUpdate("player_prefs", setpp, wherepp);
                        }
                        // populate home, current, next and back tables
                        HashMap<String, Object> setlocs = new HashMap<String, Object>();
                        setlocs.put("tardis_id", lastInsertId);
                        setlocs.put("world", block_loc.getWorld().getName());
                        setlocs.put("x", block_loc.getBlockX());
                        setlocs.put("y", block_loc.getBlockY());
                        setlocs.put("z", block_loc.getBlockZ());
                        setlocs.put("direction", d);
                        qf.insertLocations(setlocs);
                        // remove redstone torch/lapis and iron blocks
                        block.setType(Material.AIR);
                        blockBelow.setType(Material.AIR);
                        blockBottom.setType(Material.AIR);
                        // turn the block stack into a TARDIS
                        final TARDISMaterialisationData pbd = new TARDISMaterialisationData();
                        pbd.setChameleon(false);
                        pbd.setDirection(COMPASS.valueOf(d));
                        pbd.setLocation(block_loc);
                        pbd.setMalfunction(false);
                        pbd.setOutside(true);
                        pbd.setPlayer(player);
                        pbd.setRebuild(false);
                        pbd.setSubmarine(isSub(blockBottom));
                        pbd.setTardisID(lastInsertId);
                        plugin.getPresetBuilder().buildPreset(pbd);
                        plugin.getInteriorBuilder().buildInner(schm, chunkworld, lastInsertId, player, middle_id, middle_data, 35, (byte) 8, tips);
                        // set achievement completed
                        if (player.hasPermission("tardis.book")) {
                            HashMap<String, Object> seta = new HashMap<String, Object>();
                            seta.put("completed", 1);
                            HashMap<String, Object> wherea = new HashMap<String, Object>();
                            wherea.put("uuid", player.getUniqueId().toString());
                            wherea.put("name", "tardis");
                            qf.doUpdate("achievements", seta, wherea);
                            TARDISMessage.send(player, ChatColor.YELLOW + "Achievement Get!");
                            TARDISMessage.send(player, ChatColor.WHITE + plugin.getAchievementConfig().getString("tardis.message"));
                        }
                        if (max_count > 0) {
                            TARDISMessage.send(player, plugin.getPluginName() + "You have used up " + (player_count + 1) + " of " + max_count + " TARDIS builds!");
                            HashMap<String, Object> setc = new HashMap<String, Object>();
                            setc.put("count", player_count + 1);
                            if (player_count > 0) {
                                // update the player's TARDIS count
                                HashMap<String, Object> wheretc = new HashMap<String, Object>();
                                wheretc.put("uuid", player.getUniqueId().toString());
                                qf.doUpdate("t_count", setc, wheretc);
                            } else {
                                // insert new TARDIS count record
                                setc.put("uuid", player.getUniqueId().toString());
                                qf.doInsert("t_count", setc);
                            }
                        }
                    } else {
                        HashMap<String, Object> wherecl = new HashMap<String, Object>();
                        wherecl.put("tardis_id", rs.getTardis_id());
                        ResultSetCurrentLocation rsc = new ResultSetCurrentLocation(plugin, wherecl);
                        if (rsc.resultSet()) {
                            TARDISMessage.send(player, plugin.getPluginName() + "You already have a TARDIS, you left it in " + rsc.getWorld().getName() + " at x:" + rsc.getX() + " y:" + rsc.getY() + " z:" + rsc.getZ());
                        } else {
                            TARDISMessage.send(player, plugin.getPluginName() + "You already have a TARDIS, but we couldn't find it! Try calling it to you.");
                        }
                    }
                } else {
                    TARDISMessage.send(player, plugin.getPluginName() + "You don't have permission to build a TARDIS!");
                }
            }
        }
    }

    private String getWallKey(int i, int d) {
        for (Map.Entry<String, int[]> entry : plugin.getTardisWalls().blocks.entrySet()) {
            int[] value = entry.getValue();
            if (value[0] == i && value[1] == d) {
                return entry.getKey();
            }
        }
        return "ORANGE_WOOL";
    }

    private boolean isSub(Block b) {
        switch (b.getRelative(BlockFace.EAST).getType()) {
            case STATIONARY_WATER:
            case WATER:
                return true;
            default:
                return false;
        }
    }
}