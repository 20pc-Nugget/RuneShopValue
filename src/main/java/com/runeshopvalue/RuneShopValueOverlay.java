package com.runeshopvalue;

import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class RuneShopValueOverlay extends Overlay
{
    private final ItemManager itemManager;
    private final RuneShopValueConfig config;

    private Map<Integer, Integer> runeCounts = new HashMap<>();

    @Inject
    public RuneShopValueOverlay(ItemManager itemManager, RuneShopValueConfig config)
    {
        this.itemManager = itemManager;
        this.config = config;
        setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    public void updateRuneCounts(Map<Integer, Integer> counts)
    {
        this.runeCounts = new HashMap<>(counts);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.showOverlay() || runeCounts.isEmpty())
        {
            return null;
        }

        int x = 0;
        int y = 0;
        int iconSize = 32;
        int padding = 4;
        long total = 0;

        for (Map.Entry<Integer, Integer> entry : runeCounts.entrySet())
        {
            int itemId = entry.getKey();
            int quantity = entry.getValue();
            int price = RunePrices.PRICES.getOrDefault(itemId, 0);
            long value = (long) quantity * price;
            total += value;

            BufferedImage runeImg = itemManager.getImage(itemId, quantity, true);
            if (runeImg != null)
            {
                graphics.drawImage(runeImg, x, y, null);
            }
            x += iconSize + padding;

            int coinQty = (int) Math.min(value, Integer.MAX_VALUE);
            BufferedImage coinImg = itemManager.getImage(ItemID.COINS_995, coinQty, true);
            if (coinImg != null)
            {
                graphics.drawImage(coinImg, x, y, null);
            }

            x = 0;
            y += iconSize + padding;
        }

        // Total row — coin stack + raw gp number only
        int totalCoins = (int) Math.min(total, Integer.MAX_VALUE);
        BufferedImage totalCoinImg = itemManager.getImage(ItemID.COINS_995, totalCoins, true);
        if (totalCoinImg != null)
        {
            graphics.drawImage(totalCoinImg, x, y, null);
        }
        x += iconSize + padding;

        graphics.setColor(Color.WHITE);
        graphics.setFont(graphics.getFont().deriveFont(Font.BOLD, 12f));
        String totalStr = String.format("%,d", total);
        graphics.drawString(totalStr, x, y + iconSize / 2 + 4);

        int maxWidth = x + graphics.getFontMetrics().stringWidth(totalStr);
        y += iconSize + padding;

        return new Dimension(maxWidth, y);
    }
}