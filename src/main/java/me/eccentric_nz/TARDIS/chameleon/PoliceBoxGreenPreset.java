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
package me.eccentric_nz.TARDIS.chameleon;

import me.eccentric_nz.TARDIS.chameleon.coloured.PoliceBoxGreenEastPreset;
import me.eccentric_nz.TARDIS.chameleon.coloured.PoliceBoxGreenNorthPreset;
import me.eccentric_nz.TARDIS.chameleon.coloured.PoliceBoxGreenSouthPreset;
import me.eccentric_nz.TARDIS.chameleon.coloured.PoliceBoxGreenWestPreset;

public class PoliceBoxGreenPreset extends TARDISColoured {

    public PoliceBoxGreenPreset() {
        PoliceBoxGreenEastPreset east = new PoliceBoxGreenEastPreset();
        setBlueprintDataEast(east.getBlueprintData());
        setStainedDataEast(east.getStainedData());
        setGlassDataEast(east.getGlassData());
        PoliceBoxGreenNorthPreset north = new PoliceBoxGreenNorthPreset();
        setBlueprintDataNorth(north.getBlueprintData());
        setStainedDataNorth(north.getStainedData());
        setGlassDataNorth(north.getGlassData());
        PoliceBoxGreenWestPreset west = new PoliceBoxGreenWestPreset();
        setBlueprintDataWest(west.getBlueprintData());
        setStainedDataWest(west.getStainedData());
        setGlassDataWest(west.getGlassData());
        PoliceBoxGreenSouthPreset south = new PoliceBoxGreenSouthPreset();
        setBlueprintDataSouth(south.getBlueprintData());
        setStainedDataSouth(south.getStainedData());
        setGlassDataSouth(south.getGlassData());
    }
}
