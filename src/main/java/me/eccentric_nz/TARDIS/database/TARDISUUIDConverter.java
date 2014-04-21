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
package me.eccentric_nz.TARDIS.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import me.eccentric_nz.TARDIS.TARDIS;
import me.eccentric_nz.TARDIS.utility.TARDISUUIDFetcher;
import org.bukkit.ChatColor;
import org.bukkit.util.FileUtil;

/**
 *
 * @author eccentric_nz
 */
public class TARDISUUIDConverter {

    private final TARDIS plugin;
    private final TARDISDatabaseConnection service = TARDISDatabaseConnection.getInstance();
    private final Connection connection = service.getConnection();
    private final List<String> timelords = new ArrayList<String>();

    public TARDISUUIDConverter(TARDIS plugin) {
        this.plugin = plugin;
    }

    public boolean convert() {
        // get if server is online-mode=true (as required to retrieve correct player UUIDs)
        if (!getOnlineMode()) {
            plugin.getConsole().sendMessage(plugin.getPluginName() + ChatColor.RED + "UUID conversion requires the server online-mode to be TRUE!");
            return false;
        }
        // backup database
        plugin.getConsole().sendMessage(plugin.getPluginName() + "Backing up TARDIS database...");
        File oldFile = new File(plugin.getDataFolder() + File.separator + "TARDIS.db");
        File newFile = new File(plugin.getDataFolder() + File.separator + "TARDIS_" + System.currentTimeMillis() + ".db");
        FileUtil.copy(oldFile, newFile);
        // get all TARDIS owners from database
        Statement statement = null;
        PreparedStatement ps_ach = null;
        PreparedStatement ps_ars = null;
        PreparedStatement ps_pla = null;
        PreparedStatement ps_sto = null;
        PreparedStatement ps_tco = null;
        PreparedStatement ps_tar = null;
        PreparedStatement ps_tra = null;
        ResultSet rsa = null;
        ResultSet rsc = null;
        ResultSet rsp = null;
        ResultSet rsr = null;
        ResultSet rss = null;
        ResultSet rst = null;
        ResultSet rsv = null;
        String querya = "SELECT player FROM achievements";
        String queryc = "SELECT player FROM t_count";
        String queryp = "SELECT player FROM player_prefs";
        String queryr = "SELECT player FROM ars";
        String querys = "SELECT owner FROM storage";
        String queryt = "SELECT owner FROM tardis";
        String queryv = "SELECT player FROM travellers";
        String a_update = "UPDATE achievements SET uuid = ? WHERE player = ?";
        String c_update = "UPDATE t_count SET uuid = ? WHERE player = ?";
        String p_update = "UPDATE player_prefs SET uuid = ? WHERE player = ?";
        String r_update = "UPDATE ars SET uuid = ? WHERE player = ?";
        String s_update = "UPDATE storage SET uuid = ? WHERE owner = ?";
        String t_update = "UPDATE tardis SET uuid = ? WHERE owner = ?";
        String v_update = "UPDATE travellers SET uuid = ? WHERE player = ?";
        int count = 0;
        // we need to query all tables as there are potentially players in some table that aren't in others
        try {
            service.testConnection(connection);
            statement = connection.createStatement();
            rst = statement.executeQuery(queryt);
            if (rst.isBeforeFirst()) {
                while (rst.next()) {
                    timelords.add(rst.getString("owner"));
                }
            }
            rsa = statement.executeQuery(querya);
            if (rsa.isBeforeFirst()) {
                while (rsa.next()) {
                    // only add them if we haven't already
                    if (!timelords.contains(rsa.getString("player"))) {
                        timelords.add(rsa.getString("player"));
                    }
                }
            }
            rsc = statement.executeQuery(queryc);
            if (rsc.isBeforeFirst()) {
                while (rsc.next()) {
                    if (!timelords.contains(rsc.getString("player"))) {
                        timelords.add(rsc.getString("player"));
                    }
                }
            }
            rsp = statement.executeQuery(queryp);
            if (rsp.isBeforeFirst()) {
                while (rsp.next()) {
                    if (!timelords.contains(rsp.getString("player"))) {
                        timelords.add(rsp.getString("player"));
                    }
                }
            }
            rsr = statement.executeQuery(queryr);
            if (rsr.isBeforeFirst()) {
                while (rsr.next()) {
                    if (!timelords.contains(rsr.getString("player"))) {
                        timelords.add(rsr.getString("player"));
                    }
                }
            }
            rss = statement.executeQuery(querys);
            if (rss.isBeforeFirst()) {
                while (rss.next()) {
                    if (!timelords.contains(rss.getString("owner"))) {
                        timelords.add(rss.getString("owner"));
                    }
                }
            }
            rsv = statement.executeQuery(queryv);
            if (rsv.isBeforeFirst()) {
                while (rsv.next()) {
                    if (!timelords.contains(rsv.getString("player"))) {
                        timelords.add(rsv.getString("player"));
                    }
                }
            }
            // don't bother if the player list is empty
            if (timelords.size() > 0) {
                TARDISUUIDFetcher fetcher = new TARDISUUIDFetcher(timelords);
                // get UUIDs
                Map<String, UUID> response = null;
                try {
                    response = fetcher.call();
                } catch (Exception e) {
                    plugin.debug("Exception while running TARDISUUIDFetcher: " + e.getMessage());
                    return false;
                }
                if (response != null) {
                    // update all TARDIS owners to UUIDs
                    ps_ach = connection.prepareStatement(a_update);
                    ps_ars = connection.prepareStatement(r_update);
                    ps_sto = connection.prepareStatement(p_update);
                    ps_pla = connection.prepareStatement(s_update);
                    ps_tco = connection.prepareStatement(c_update);
                    ps_tar = connection.prepareStatement(t_update);
                    ps_tra = connection.prepareStatement(v_update);
                    for (Map.Entry<String, UUID> map : response.entrySet()) {
                        ps_ach.setString(1, map.getValue().toString());
                        ps_ach.setString(2, map.getKey());
                        count += ps_ach.executeUpdate();
                        ps_ars.setString(1, map.getValue().toString());
                        ps_ars.setString(2, map.getKey());
                        count += ps_ars.executeUpdate();
                        ps_pla.setString(1, map.getValue().toString());
                        ps_pla.setString(2, map.getKey());
                        count += ps_pla.executeUpdate();
                        ps_sto.setString(1, map.getValue().toString());
                        ps_sto.setString(2, map.getKey());
                        count += ps_sto.executeUpdate();
                        ps_tco.setString(1, map.getValue().toString());
                        ps_tco.setString(2, map.getKey());
                        count += ps_tco.executeUpdate();
                        ps_tar.setString(1, map.getValue().toString());
                        ps_tar.setString(2, map.getKey());
                        count += ps_tar.executeUpdate();
                        ps_tra.setString(1, map.getValue().toString());
                        ps_tra.setString(2, map.getKey());
                        count += ps_tra.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            plugin.debug("ResultSet error for UUID updating! " + e.getMessage());
            return false;
        } finally {
            try {
                if (rsa != null) {
                    rsa.close();
                }
                if (rsc != null) {
                    rsc.close();
                }
                if (rsp != null) {
                    rsp.close();
                }
                if (rsr != null) {
                    rsr.close();
                }
                if (rss != null) {
                    rss.close();
                }
                if (rst != null) {
                    rst.close();
                }
                if (rsv != null) {
                    rsv.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (ps_ach != null) {
                    ps_ach.close();
                }
                if (ps_ars != null) {
                    ps_ars.close();
                }
                if (ps_pla != null) {
                    ps_pla.close();
                }
                if (ps_sto != null) {
                    ps_sto.close();
                }
                if (ps_tco != null) {
                    ps_tco.close();
                }
                if (ps_tar != null) {
                    ps_tar.close();
                }
                if (ps_tra != null) {
                    ps_tra.close();
                }
            } catch (SQLException e) {
                plugin.debug("Error closing tardis table! " + e.getMessage());
            }
        }
        plugin.getConsole().sendMessage(plugin.getPluginName() + "Converted " + count + " Time Lord names to UUIDs.");
        return true;
    }

    /**
     * Gets the server default resource pack. Will use the Minecraft default
     * pack if none is specified. Until Minecraft/Bukkit lets us set the RP back
     * to Default, we'll have to host it on DropBox
     *
     * @return The server specified texture pack.
     */
    public boolean getOnlineMode() {
        FileInputStream in = null;
        try {
            Properties properties = new Properties();
            String path = "server.properties";
            in = new FileInputStream(path);
            properties.load(in);
            String online = properties.getProperty("online-mode");
            return (online != null && online.equalsIgnoreCase("true"));
        } catch (FileNotFoundException ex) {
            plugin.debug("Could not find server.properties!");
            return false;
        } catch (IOException ex) {
            plugin.debug("Could not read server.properties!");
            return false;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                plugin.debug("Could not close server.properties!");
            }
        }
    }
}