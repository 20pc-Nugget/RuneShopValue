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
    }

    @Override
    protected void shutDown()
    {
        clientToolbar.removeNavigation(navButton);
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
                if (RunePrices.PRICES.containsKey(item.getId()))
                {
                    runeCounts.merge(item.getId(), item.getQuantity(), Integer::sum);
                }
            }
        }

        if (config.includeRunePouch())
        {
            RunePouchReader.addPouchContents(client, runeCounts);
        }

        panel.update(runeCounts);
    }
}