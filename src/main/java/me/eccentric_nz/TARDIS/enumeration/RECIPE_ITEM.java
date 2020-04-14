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
package me.eccentric_nz.TARDIS.enumeration;

import java.util.Locale;

public enum RECIPE_ITEM {
    // shaped recipes start here
    ACID_BATTERY(10000001),
    ARTRON_STORAGE_CELL(10000001),
    BIO_SCANNER_CIRCUIT(10001969),
    BLANK_STORAGE_DISK(10000001),
    BLASTER_BATTERY(10000002),
    CUSTARD_CREAM(10000002),
    DIAMOND_DISRUPTOR_CIRCUIT(10001971),
    EMERALD_ENVIRONMENT_CIRCUIT(10001972),
    FISH_FINGER(10000001),
    FOB_WATCH(10000001),
    HANDLES(10000001),
    HANDLES_DISK(10000001),
    IGNITE_CIRCUIT(10001982),
    JAMMY_DODGER(10000001),
    LANDING_PAD(10000002),
    PAINTER_CIRCUIT(10001979),
    PAPER_BAG(10000001),
    PERCEPTION_CIRCUIT(10001978),
    PERCEPTION_FILTER(14),
    PICKUP_ARROWS_CIRCUIT(10001984),
    REDSTONE_ACTIVATOR_CIRCUIT(10001970),
    RIFT_CIRCUIT(10001983),
    RIFT_MANIPULATOR(10000001),
    RUST_PLAGUE_SWORD(10000001),
    SERVER_ADMIN_CIRCUIT(10001968),
    SONIC_BLASTER(10000002),
    SONIC_GENERATOR(10000001),
    SONIC_OSCILLATOR(10001967),
    SONIC_SCREWDRIVER(10000011),
    STATTENHEIM_REMOTE(10000001),
    TARDIS_ARS_CIRCUIT(10001973),
    TARDIS_ARTRON_FURNACE(10000001),
    TARDIS_BIOME_READER(10000001),
    TARDIS_CHAMELEON_CIRCUIT(10001966),
    TARDIS_COMMUNICATOR(10000040),
    TARDIS_INPUT_CIRCUIT(10001976),
    TARDIS_INVISIBILITY_CIRCUIT(10001981),
    TARDIS_KEY(1),
    TARDIS_KEYBOARD_EDITOR(10000001),
    TARDIS_LOCATOR(10000001),
    TARDIS_LOCATOR_CIRCUIT(10001965),
    TARDIS_MATERIALISATION_CIRCUIT(10001964),
    TARDIS_MEMORY_CIRCUIT(10001975),
    TARDIS_RANDOMISER_CIRCUIT(10001980),
    TARDIS_REMOTE_KEY(15),
    TARDIS_SCANNER_CIRCUIT(10001977),
    TARDIS_STATTENHEIM_CIRCUIT(10001963),
    TARDIS_TELEPATHIC_CIRCUIT(10000001),
    TARDIS_TEMPORAL_CIRCUIT(10001974),
    WHITE_BOW_TIE(10000023),
    ORANGE_BOW_TIE(10000024),
    MAGENTA_BOW_TIE(10000025),
    LIGHT_BLUE_BOW_TIE(10000026),
    YELLOW_BOW_TIE(10000027),
    LIME_BOW_TIE(10000028),
    PINK_BOW_TIE(10000029),
    GREY_BOW_TIE(10000030),
    LIGHT_GREY_BOW_TIE(10000031),
    CYAN_BOW_TIE(10000032),
    PURPLE_BOW_TIE(10000033),
    BLUE_BOW_TIE(10000034),
    BROWN_BOW_TIE(10000035),
    GREEN_BOW_TIE(10000036),
    RED_BOW_TIE(10000037),
    BLACK_BOW_TIE(10000038),
    THREE_D_GLASSES(10000039),
    // unshaped recipes start here
    BIOME_STORAGE_DISK(10000001),
    BOWL_OF_CUSTARD(10000001),
    PLAYER_STORAGE_DISK(10000001),
    PRESET_STORAGE_DISK(10000001),
    SAVE_STORAGE_DISK(10000001),
    TARDIS_SCHEMATIC_WAND(10000001),
    // jelly babies
    VANILLA_JELLY_BABY(10000001),
    ORANGE_JELLY_BABY(10000002),
    WATERMELON_JELLY_BABY(10000003),
    BUBBLEGUM_JELLY_BABY(10000004),
    LEMON_JELLY_BABY(10000005),
    LIME_JELLY_BABY(10000006),
    STRAWBERRY_JELLY_BABY(10000007),
    EARL_GREY_JELLY_BABY(10000008),
    VODKA_JELLY_BABY(10000009),
    ISLAND_PUNCH_JELLY_BABY(10000010),
    GRAPE_JELLY_BABY(10000011),
    BLUEBERRY_JELLY_BABY(10000012),
    CAPPUCCINO_JELLY_BABY(10000013),
    APPLE_JELLY_BABY(10000014),
    RASPBERRY_JELLY_BABY(10000015),
    LICORICE_JELLY_BABY(10000016),
    // sonic upgrades
    ADMIN_UPGRADE(10001968),
    BIO_SCANNER_UPGRADE(10001969),
    REDSTONE_UPGRADE(10001970),
    DIAMOND_UPGRADE(10001971),
    EMERALD_UPGRADE(10001972),
    PAINTER_UPGRADE(10001979),
    IGNITE_UPGRADE(10001982),
    PICKUP_ARROWS_UPGRADE(10001984),
    // planet items
    ACID_BUCKET(1),
    RUST_BUCKET(1),
    // not fond
    NOT_FOUND(-1);

    private final int customModelData;

    RECIPE_ITEM(int customModelData) {
        this.customModelData = customModelData;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public static RECIPE_ITEM getByName(String name) {
        String processed = name.replace(" ", "_").replace("-", "_").replace("3", "THREE").toUpperCase(Locale.ENGLISH);
        try {
            return RECIPE_ITEM.valueOf(processed);
        } catch (IllegalArgumentException e) {
            return NOT_FOUND;
        }
    }
}
