package com.runeshopvalue;

import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.Point;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuneShopValueOverlay extends Overlay
{
    private static final int ICON_SIZE = 32;
    private static final int PADDING = 4;

    private final Client client;
    private final ItemManager itemManager;
    private final RuneShopValueConfig config;
    private final TooltipManager tooltipManager;

    private Map<Integer, Integer> runeCounts = new HashMap<>();

    @Inject
    public RuneShopValueOverlay(Client client, ItemManager itemManager, RuneShopValueConfig config, TooltipManager tooltipManager)
    {
        this.client = client;
        this.itemManager = itemManager;
        this.config = config;
        this.tooltipManager = tooltipManager;
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

        boolean horizontal = config.overlayOrientation() == OverlayOrientation.HORIZONTAL;
        boolean totalOnly = config.overlayTotalOnly();

        List<Integer> orderedIds = new ArrayList<>();
        orderedIds.addAll(RunePrices.ELEMENTAL_ORDER);
        orderedIds.addAll(RunePrices.CATALYTIC_ORDER);

        Rectangle overlayBounds = getBounds();
        Point mouse = client.getMouseCanvasPosition();

        int x = 0;
        int y = 0;
        long total = 0;
        int maxX = 0;
        int maxY = 0;

        StringBuilder breakdown = new StringBuilder();

        for (int itemId : orderedIds)
        {
            Integer quantity = runeCounts.get(itemId);
            if (quantity == null || quantity <= 0)
            {
                continue;
            }

            int price = RunePrices.PRICES.getOrDefault(itemId, 0);
            long value = (long) quantity * price;
            total += value;

            if (totalOnly)
            {
                String runeName = itemManager.getItemComposition(itemId).getName();
                if (breakdown.length() > 0)
                {
                    breakdown.append("</br>");
                }
                breakdown.append(String.format("%,d x %s: %,d gp", quantity, runeName, value));
                continue;
            }

            BufferedImage runeImg = itemManager.getImage(itemId, quantity, true);
            if (runeImg != null)
            {
                graphics.drawImage(runeImg, x, y, null);
            }
            String runeName = itemManager.getItemComposition(itemId).getName();
            checkTooltip(overlayBounds, mouse, x, y, String.format("%,d x %s", quantity, runeName));
            x += ICON_SIZE + PADDING;

            int coinQty = (int) Math.min(value, Integer.MAX_VALUE);
            BufferedImage coinImg = itemManager.getImage(ItemID.COINS_995, coinQty, true);
            if (coinImg != null)
            {
                graphics.drawImage(coinImg, x, y, null);
            }
            checkTooltip(overlayBounds, mouse, x, y, String.format("%,d gp", value));

            if (horizontal)
            {
                x += ICON_SIZE + PADDING * 3;
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y + ICON_SIZE);
            }
            else
            {
                x = 0;
                y += ICON_SIZE + PADDING;
                maxX = ICON_SIZE * 2 + PADDING;
                maxY = y;
            }
        }

        if (totalOnly || config.showOverlayTotal())
        {
            int totalCoins = (int) Math.min(total, Integer.MAX_VALUE);
            BufferedImage totalCoinImg = itemManager.getImage(ItemID.COINS_995, totalCoins, true);
            if (totalCoinImg != null)
            {
                graphics.drawImage(totalCoinImg, x, y, null);
            }

            String tooltipText = totalOnly
                    ? String.format("Total: %,d gp</br>%s", total, breakdown)
                    : String.format("Total: %,d gp", total);
            checkTooltip(overlayBounds, mouse, x, y, tooltipText);

            maxX = Math.max(maxX, x + ICON_SIZE);
            maxY = Math.max(maxY, y + ICON_SIZE + (horizontal ? 0 : PADDING));
        }

        return new Dimension(maxX, maxY);
    }

    private void checkTooltip(Rectangle overlayBounds, Point mouse, int x, int y, String text)
    {
        if (overlayBounds == null || mouse == null)
        {
            return;
        }

        Rectangle iconBounds = new Rectangle(overlayBounds.x + x, overlayBounds.y + y, ICON_SIZE, ICON_SIZE);
        if (iconBounds.contains(mouse.getX(), mouse.getY()))
        {
            tooltipManager.add(new Tooltip(text));
        }
    }
}