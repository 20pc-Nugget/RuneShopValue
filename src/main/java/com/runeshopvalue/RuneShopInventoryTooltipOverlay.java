package com.runeshopvalue;

import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;

import javax.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class RuneShopInventoryTooltipOverlay extends Overlay
{
    private final Client client;
    private final RuneShopValueConfig config;
    private final TooltipManager tooltipManager;

    @Inject
    public RuneShopInventoryTooltipOverlay(Client client, RuneShopValueConfig config, TooltipManager tooltipManager)
    {
        this.client = client;
        this.config = config;
        this.tooltipManager = tooltipManager;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.showInventoryTooltip())
        {
            return null;
        }

        Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);
        if (inventoryWidget == null || inventoryWidget.isHidden())
        {
            return null;
        }

        Widget[] slots = inventoryWidget.getDynamicChildren();
        if (slots == null)
        {
            return null;
        }

        Point mouse = client.getMouseCanvasPosition();
        if (mouse == null)
        {
            return null;
        }

        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
        if (inventory == null)
        {
            return null;
        }

        Item[] items = inventory.getItems();

        for (Widget slot : slots)
        {
            Rectangle bounds = slot.getBounds();
            if (bounds == null || !bounds.contains(mouse.getX(), mouse.getY()))
            {
                continue;
            }

            int index = slot.getIndex();
            if (index < 0 || index >= items.length)
            {
                return null;
            }

            Item item = items[index];
            if (item == null)
            {
                return null;
            }

            Integer price = RunePrices.PRICES.get(item.getId());
            if (price == null)
            {
                return null;
            }

            long value = (long) item.getQuantity() * price;
            tooltipManager.add(new Tooltip(String.format("ALI: %,d gp (%,d ea)", value, price)));
            return null;
        }

        return null;
    }
}