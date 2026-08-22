package com.runeshopvalue;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

@PluginDescriptor(
        name = "Rune Shop Value",
        description = "Shows the sell value of your runes at Ali Morrisane's shop",
        tags = {"runes", "value", "ali morrisane", "shop"}
)
public class RuneShopValuePlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private RuneShopValueConfig config;

    @Inject
    private ItemManager itemManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private RuneShopValueOverlay overlay;

    @Inject
    private RuneShopInventoryTooltipOverlay inventoryTooltipOverlay;

    private RuneShopValuePanel panel;
    private NavigationButton navButton;

    @Provides
    RuneShopValueConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(RuneShopValueConfig.class);
    }

    @Override
    protected void startUp()
    {
        panel = new RuneShopValuePanel(itemManager);

        BufferedImage icon = itemManager.getImage(ItemID.LAW_RUNE);

        navButton = NavigationButton.builder()
                .tooltip("Rune Shop Value")
                .icon(icon)
                .priority(6)
                .panel(panel)
                .build();

        clientToolbar.addNavigation(navButton);
        overlayManager.add(overlay);
        overlayManager.add(inventoryTooltipOverlay);
    }

    @Override
    protected void shutDown()
    {
        clientToolbar.removeNavigation(navButton);
        overlayManager.remove(overlay);
        overlayManager.remove(inventoryTooltipOverlay);
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        Map<Integer, Integer> runeCounts = new HashMap<>();

        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
        if (inventory != null)
        {
            for (Item item : inventory.getItems())
            {
                if (RunePrices.PRICES.containsKey(item.getId()) && isEnabled(item.getId()))
                {
                    runeCounts.merge(item.getId(), item.getQuantity(), Integer::sum);
                }
            }
        }

        if (config.includeRunePouch())
        {
            RunePouchReader.addPouchContents(client, runeCounts);
            runeCounts.keySet().removeIf(id -> !isEnabled(id));
        }

        panel.update(runeCounts);
        overlay.updateRuneCounts(runeCounts);
    }

    private boolean isEnabled(int itemId)
    {
        if (itemId == ItemID.AIR_RUNE) return config.showAir();
        if (itemId == ItemID.WATER_RUNE) return config.showWater();
        if (itemId == ItemID.EARTH_RUNE) return config.showEarth();
        if (itemId == ItemID.FIRE_RUNE) return config.showFire();
        if (itemId == ItemID.MIND_RUNE) return config.showMind();
        if (itemId == ItemID.BODY_RUNE) return config.showBody();
        if (itemId == ItemID.COSMIC_RUNE) return config.showCosmic();
        if (itemId == ItemID.CHAOS_RUNE) return config.showChaos();
        if (itemId == ItemID.NATURE_RUNE) return config.showNature();
        if (itemId == ItemID.LAW_RUNE) return config.showLaw();
        if (itemId == ItemID.DEATH_RUNE) return config.showDeath();
        if (itemId == ItemID.BLOOD_RUNE) return config.showBlood();
        if (itemId == ItemID.SOUL_RUNE) return config.showSoul();
        return true;
    }
}