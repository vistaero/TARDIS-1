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
import me.eccentric_nz.TARDIS.database.QueryFactory;
import me.eccentric_nz.TARDIS.database.ResultSetTardisID;
import me.eccentric_nz.TARDIS.database.data.Program;
import me.eccentric_nz.TARDIS.utility.TARDISMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * Programming is a process used by Cybermen to control humans. To program a human, the person has to be dead. A control
 * is installed in the person, powered by electricity, turning the person into an agent of the Cybermen. Control over
 * programmed humans can be shorted out by another signal, but that kills whatever might be left of the person.
 *
 * @author eccentric_nz
 */
public class TARDISHandlesProcessor {

    private final TARDIS plugin;
    private final Program program;
    private final Player player;
    private final int pid;

    public TARDISHandlesProcessor(TARDIS plugin, Program program, Player player, int pid) {
        this.plugin = plugin;
        this.program = program;
        this.player = player;
        this.pid = pid;
    }

    public void processDisk() {
        String event = "";
        int i = 0;
        for (ItemStack is : program.getInventory()) {
            if (is != null) {
                TARDISHandlesBlock thb = TARDISHandlesBlock.BY_NAME.get(is.getItemMeta().getDisplayName());
                TARDISHandlesCategory category = thb.getCategory();
                if (category == TARDISHandlesCategory.EVENT) {
                    event = thb.toString();
                    break;
                } else if (category == TARDISHandlesCategory.COMMAND) {
                    processCommand(i + 1);
                    TARDISMessage.handlesSend(player, "HANDLES_EXECUTE");
                }
            }
            i++;
        }
        if (!event.isEmpty()) {
            HashMap<String, Object> set = new HashMap<>();
            set.put("parsed", event);
            HashMap<String, Object> where = new HashMap<>();
            where.put("program_id", pid);
            new QueryFactory(plugin).doUpdate("programs", set, where);
            TARDISMessage.handlesSend(player, "HANDLES_RUNNING");
        }
    }

    public void processCommand(int pos) {
        ResultSetTardisID rs = new ResultSetTardisID(plugin);
        if (rs.fromUUID(program.getUuid())) {
            String command = "";
            int id = rs.getTardis_id();
            for (int i = pos; i < 36; i++) {
                ItemStack is = program.getInventory()[i];
                if (is != null) {
                    TARDISHandlesBlock thb = TARDISHandlesBlock.BY_NAME.get(is.getItemMeta().getDisplayName());
                    ItemStack next = program.getInventory()[i + 1];
                    if (is != null) {
                        TARDISHandlesBlock arg = TARDISHandlesBlock.BY_NAME.get(next.getItemMeta().getDisplayName());
                        switch (thb) {
                            case TARDIS:
                                switch (arg) {
                                    case COMEHERE:
                                        command = "handles call " + program.getUuid() + " " + id;
                                        break;
                                    case HIDE:
                                        command = "tardis hide";
                                        break;
                                    case REBUILD:
                                        command = "tardis rebuild";
                                        break;
                                    case TAKE_OFF:
                                        command = "handles takeoff " + program.getUuid() + " " + id;
                                        break;
                                    case LAND:
                                        command = "handles land " + program.getUuid() + " " + id;
                                        break;
                                }
                                break;
                            case DOOR:
                                switch (arg) {
                                    case OPEN:

                                        break;
                                    case CLOSE:

                                        break;
                                    case LOCK:

                                        break;
                                    case UNLOCK:

                                        break;
                                }
                                break;
                            case LIGHTS:
                                if (arg == TARDISHandlesBlock.ON) {

                                } else {

                                }
                                break;
                            case POWER:
                                if (arg == TARDISHandlesBlock.ON) {

                                } else {

                                }
                                break;
                            case SIEGE:
                                if (arg == TARDISHandlesBlock.ON) {

                                } else {

                                }
                                break;
                            case SCAN:
                                command = "handles scan " + program.getUuid() + " " + id;
                                break;
                            case TRAVEL:

                                break;
                            default:
                        }
                    }
                }
            }
            // execute command
            if (command.startsWith("handles")) {
                plugin.getServer().dispatchCommand(plugin.getConsole(), command);
            } else {
                player.performCommand(command);
            }
        }
    }
}
