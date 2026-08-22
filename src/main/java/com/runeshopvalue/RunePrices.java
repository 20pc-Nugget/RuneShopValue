package com.runeshopvalue;

import net.runelite.api.ItemID;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunePrices
{
    // Ali Morrisane's Discount Wares — static "buys at" price per rune.
    // Anything not in this map isn't purchased by this shop.
    public static final Map<Integer, Integer> PRICES = new HashMap<>();

    // Ordered lowest to highest Runecraft level required to craft.
    public static final List<Integer> ELEMENTAL_ORDER = Arrays.asList(
            ItemID.AIR_RUNE,   // lvl 1
            ItemID.WATER_RUNE, // lvl 5
            ItemID.EARTH_RUNE, // lvl 9
            ItemID.FIRE_RUNE   // lvl 14
    );

    public static final List<Integer> CATALYTIC_ORDER = Arrays.asList(
            ItemID.MIND_RUNE,   // lvl 1
            ItemID.BODY_RUNE,   // lvl 20
            ItemID.COSMIC_RUNE, // lvl 27
            ItemID.CHAOS_RUNE,  // lvl 35
            ItemID.NATURE_RUNE, // lvl 44
            ItemID.LAW_RUNE,    // lvl 54
            ItemID.DEATH_RUNE,  // lvl 65
            ItemID.BLOOD_RUNE,  // lvl 77
            ItemID.SOUL_RUNE    // lvl 90
    );

    static
    {
        PRICES.put(ItemID.AIR_RUNE, 2);
        PRICES.put(ItemID.WATER_RUNE, 2);
        PRICES.put(ItemID.EARTH_RUNE, 2);
        PRICES.put(ItemID.FIRE_RUNE, 2);

        PRICES.put(ItemID.MIND_RUNE, 1);
        PRICES.put(ItemID.BODY_RUNE, 1);
        PRICES.put(ItemID.COSMIC_RUNE, 25);
        PRICES.put(ItemID.CHAOS_RUNE, 45);
        PRICES.put(ItemID.NATURE_RUNE, 90);
        PRICES.put(ItemID.DEATH_RUNE, 90);
        PRICES.put(ItemID.LAW_RUNE, 120);
        PRICES.put(ItemID.SOUL_RUNE, 150);
        PRICES.put(ItemID.BLOOD_RUNE, 200);
    }
}