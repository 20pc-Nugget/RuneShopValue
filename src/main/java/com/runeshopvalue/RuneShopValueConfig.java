package com.runeshopvalue;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

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

    @ConfigItem(
            keyName = "showOverlay",
            name = "Show in-game overlay",
            description = "Display rune values as an in-game overlay"
    )
    default boolean showOverlay()
    {
        return false;
    }

    @ConfigItem(
            keyName = "overlayOrientation",
            name = "Overlay orientation",
            description = "Lay the overlay out horizontally or vertically"
    )
    default OverlayOrientation overlayOrientation()
    {
        return OverlayOrientation.VERTICAL;
    }

    @ConfigItem(
            keyName = "showOverlayTotal",
            name = "Show total in overlay",
            description = "Display the total gp coin stack in the overlay"
    )
    default boolean showOverlayTotal()
    {
        return true;
    }

    @ConfigItem(
            keyName = "overlayTotalOnly",
            name = "Overlay: total only",
            description = "Only show the total coin stack in the overlay, hiding individual rune rows"
    )
    default boolean overlayTotalOnly()
    {
        return false;
    }

    @ConfigItem(
            keyName = "showInventoryTooltip",
            name = "Show Ali price on hover",
            description = "Show the Ali Morrisane sell value when hovering a rune in your inventory"
    )
    default boolean showInventoryTooltip()
    {
        return true;
    }

    @ConfigSection(
            name = "Elemental Runes",
            description = "Toggle which elemental runes are tracked",
            position = 10
    )
    String elementalSection = "elementalSection";

    @ConfigItem(keyName = "showAir", name = "Air rune", description = "Track air runes", section = elementalSection)
    default boolean showAir() { return true; }

    @ConfigItem(keyName = "showWater", name = "Water rune", description = "Track water runes", section = elementalSection)
    default boolean showWater() { return true; }

    @ConfigItem(keyName = "showEarth", name = "Earth rune", description = "Track earth runes", section = elementalSection)
    default boolean showEarth() { return true; }

    @ConfigItem(keyName = "showFire", name = "Fire rune", description = "Track fire runes", section = elementalSection)
    default boolean showFire() { return true; }

    @ConfigSection(
            name = "Catalytic Runes",
            description = "Toggle which catalytic runes are tracked",
            position = 20
    )
    String catalyticSection = "catalyticSection";

    @ConfigItem(keyName = "showMind", name = "Mind rune", description = "Track mind runes", section = catalyticSection)
    default boolean showMind() { return true; }

    @ConfigItem(keyName = "showBody", name = "Body rune", description = "Track body runes", section = catalyticSection)
    default boolean showBody() { return true; }

    @ConfigItem(keyName = "showCosmic", name = "Cosmic rune", description = "Track cosmic runes", section = catalyticSection)
    default boolean showCosmic() { return true; }

    @ConfigItem(keyName = "showChaos", name = "Chaos rune", description = "Track chaos runes", section = catalyticSection)
    default boolean showChaos() { return true; }

    @ConfigItem(keyName = "showNature", name = "Nature rune", description = "Track nature runes", section = catalyticSection)
    default boolean showNature() { return true; }

    @ConfigItem(keyName = "showLaw", name = "Law rune", description = "Track law runes", section = catalyticSection)
    default boolean showLaw() { return true; }

    @ConfigItem(keyName = "showDeath", name = "Death rune", description = "Track death runes", section = catalyticSection)
    default boolean showDeath() { return true; }

    @ConfigItem(keyName = "showBlood", name = "Blood rune", description = "Track blood runes", section = catalyticSection)
    default boolean showBlood() { return true; }

    @ConfigItem(keyName = "showSoul", name = "Soul rune", description = "Track soul runes", section = catalyticSection)
    default boolean showSoul() { return true; }
}