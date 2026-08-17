package com.runeshopvalue;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("runeshopvalue")
public interface RuneShopValueConfig extends Config
{
    @ConfigItem(
            keyName = "includeRunePouch",
            name = "Include rune pouch",
            description = "Add runes stored in a rune pouch (any variant) to the total"
    )
    default boolean includeRunePouch()
    {
        return true;
    }
}