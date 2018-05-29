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
package me.eccentric_nz.TARDIS.commands.handles;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import me.eccentric_nz.TARDIS.utility.TARDISMessage;
import me.eccentric_nz.TARDIS.utility.TARDISStaticUtils;
import org.bukkit.entity.Player;

/**
 *
 * @author eccentric_nz
 */
public class TARDISHandlesTimeCommand {

    public boolean sayTime(Player player, String[] args) {
        long minecraftTime = player.getWorld().getTime();
        String daynight = TARDISStaticUtils.getTime(minecraftTime);
        TARDISMessage.handlesSend(player, "HANDLES_TIME", minecraftTime, daynight, parseTime(minecraftTime));
        // get current server time (in a nice format)
        String utc = Instant.now().toString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        String localTimeToDisplay = LocalDateTime.parse(utc, formatter)
                .atOffset(ZoneOffset.UTC)
                .atZoneSameInstant(ZoneId.systemDefault())
                .format(formatter);
        // send message to player with current time
        TARDISMessage.handlesSend(player, "HANDLES_SERVER_TIME", localTimeToDisplay);
        return true;
    }

    private static String parseTime(long time) {
        long hours = time / 1000 + 6;
        long minutes = (time % 1000) * 60 / 1000;
        String ampm = "am";
        if (hours >= 12) {
            hours -= 12;
            ampm = "pm";
        }
        if (hours == 0) {
            hours = 12;
        }
        String mm = (minutes < 10) ? "0" + minutes : "" + minutes;
        return hours + ":" + mm + " " + ampm;
    }
}
