package com.runeshopvalue;

import net.runelite.api.Client;
import net.runelite.api.EnumComposition;
import net.runelite.api.EnumID;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemID;
import net.runelite.api.gameval.VarbitID;

import java.util.Map;

public class RunePouchReader
{
    private static final int NUM_SLOTS = 6;

    private static final int[] AMOUNT_VARBITS = {
            VarbitID.RUNE_POUCH_QUANTITY_1, VarbitID.RUNE_POUCH_QUANTITY_2, VarbitID.RUNE_POUCH_QUANTITY_3,
            VarbitID.RUNE_POUCH_QUANTITY_4, VarbitID.RUNE_POUCH_QUANTITY_5, VarbitID.RUNE_POUCH_QUANTITY_6
    };

    private static final int[] RUNE_VARBITS = {
            VarbitID.RUNE_POUCH_TYPE_1, VarbitID.RUNE_POUCH_TYPE_2, VarbitID.RUNE_POUCH_TYPE_3,
            VarbitID.RUNE_POUCH_TYPE_4, VarbitID.RUNE_POUCH_TYPE_5, VarbitID.RUNE_POUCH_TYPE_6
    };

    private static final int[] POUCH_ITEM_IDS = {
            ItemID.RUNE_POUCH,
            ItemID.RUNE_POUCH_L,
            ItemID.DIVINE_RUNE_POUCH,
            ItemID.DIVINE_RUNE_POUCH_L
    };

    public static void addPouchContents(Client client, Map<Integer, Integer> runeCounts)
    {
        var inventory = client.getItemContainer(InventoryID.INVENTORY);
        if (inventory == null)
        {
            return;
        }

        boolean hasPouch = false;
        for (int id : POUCH_ITEM_IDS)
        {
            if (inventory.contains(id))
            {
                hasPouch = true;
                break;
            }
        }
        if (!hasPouch)
        {
            return;
        }

        EnumComposition runepouchEnum = client.getEnum(EnumID.RUNEPOUCH_RUNE);

        for (int i = 0; i < NUM_SLOTS; i++)
        {
            int amount = client.getVarbitValue(AMOUNT_VARBITS[i]);
            int runeIndex = client.getVarbitValue(RUNE_VARBITS[i]);

            if (amount <= 0 || runeIndex == 0)
            {
                continue;
            }

            int runeItemId = runepouchEnum.getIntValue(runeIndex);
            if (RunePrices.PRICES.containsKey(runeItemId))
            {
                runeCounts.merge(runeItemId, amount, Integer::sum);
            }
        }
    }
}