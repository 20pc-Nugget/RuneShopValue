package com.runeshopvalue;

import net.runelite.api.ItemID;
import java.util.HashMap;
import java.util.Map;

public class RunePrices
{
    // Ali Morrisane's Discount Wares — static "buys at" price per rune.
    // Anything not in this map isn't purchased by this shop.
    public static final Map<Integer, Integer> PRICES = new HashMap<>();

    static
    {
        // Elemental runes
        PRICES.put(ItemID.AIR_RUNE, 2);
        PRICES.put(ItemID.WATER_RUNE, 2);
        PRICES.put(ItemID.EARTH_RUNE, 2);
        PRICES.put(ItemID.FIRE_RUNE, 2);

        // Catalytic runes
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