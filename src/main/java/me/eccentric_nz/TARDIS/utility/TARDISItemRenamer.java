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
package me.eccentric_nz.TARDIS.utility;

import me.eccentric_nz.TARDIS.TARDIS;
import me.eccentric_nz.TARDIS.enumeration.RECIPE_ITEM;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

/**
 * Soon after taking Amy Pond on board for the first time, the TARDIS console provided the Doctor with a new sonic
 * screwdriver, as the previous one had been destroyed.
 *
 * @author eccentric_nz
 */
public class TARDISItemRenamer {

    private final ItemStack itemStack;

    public TARDISItemRenamer(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    /**
     * Sets the name of the held item to the specified string. Also adds some lore.
     *
     * @param name    the name to give the item
     * @param setlore whether to set lore on the item
     */
    public void setName(String name, boolean setlore) {
        ItemMeta im = itemStack.getItemMeta();
        if (im == null) {
            TARDIS.plugin.debug("ItemMeta was null for ItemStack: " + itemStack.toString());
        } else {
            im.setDisplayName(name);
            if (setlore) {
                ArrayList<String> lore = new ArrayList<>();
                lore.add("Enter and exit your TARDIS");
                im.setLore(lore);
                im.setCustomModelData(1);
            }
            try {
                RECIPE_ITEM recipeItem = RECIPE_ITEM.valueOf(TARDISStringUtils.toScoredUppercase(name));
                im.setCustomModelData(recipeItem.getCustomModelData());
            } catch (IllegalArgumentException e) {
                // do nothing
            }
            itemStack.setItemMeta(im);
        }
    }
}
