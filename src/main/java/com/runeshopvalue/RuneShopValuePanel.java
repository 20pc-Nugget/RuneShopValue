package com.runeshopvalue;

import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class RuneShopValuePanel extends PluginPanel
{
    private final JPanel listPanel = new JPanel();
    private final JLabel totalLabel = new JLabel("Total: 0 gp");
    private final ItemManager itemManager;

    public RuneShopValuePanel(ItemManager itemManager)
    {
        this.itemManager = itemManager;

        setLayout(new BorderLayout());
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(listPanel), BorderLayout.CENTER);
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD));
        add(totalLabel, BorderLayout.SOUTH);
    }

    public void update(Map<Integer, Integer> runeCounts)
    {
        listPanel.removeAll();
        long total = 0;

        for (Map.Entry<Integer, Integer> entry : runeCounts.entrySet())
        {
            int itemId = entry.getKey();
            int quantity = entry.getValue();
            int price = RunePrices.PRICES.getOrDefault(itemId, 0);
            long value = (long) quantity * price;
            total += value;

            listPanel.add(buildRow(itemId, quantity, value));
        }

        totalLabel.setText("Total: " + String.format("%,d", total) + " gp");
        listPanel.revalidate();
        listPanel.repaint();
        revalidate();
        repaint();
    }

    private JPanel buildRow(int itemId, int quantity, long value)
    {
        JPanel row = new JPanel();
        row.setLayout(new FlowLayout(FlowLayout.LEFT, 6, 4));

        JLabel runeLabel = new JLabel();
        net.runelite.client.util.AsyncBufferedImage runeIcon = itemManager.getImage(itemId, quantity, true);
        runeIcon.addTo(runeLabel);
        row.add(runeLabel);

        JLabel coinLabel = new JLabel();
        int coinQuantity = (int) Math.min(value, Integer.MAX_VALUE);
        net.runelite.client.util.AsyncBufferedImage coinIcon = itemManager.getImage(ItemID.COINS, coinQuantity, true);
        coinIcon.addTo(coinLabel);
        row.add(coinLabel);

        JLabel valueLabel = new JLabel(String.format("%,d gp", value));
        row.add(valueLabel);

        return row;
    }
}