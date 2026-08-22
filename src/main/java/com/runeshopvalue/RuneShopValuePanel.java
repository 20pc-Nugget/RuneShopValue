package com.runeshopvalue;

import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

public class RuneShopValuePanel extends PluginPanel
{
    private final JPanel listPanel = new JPanel();
    private final JLabel totalCoinLabel = new JLabel();
    private final ItemManager itemManager;

    public RuneShopValuePanel(ItemManager itemManager)
    {
        this.itemManager = itemManager;

        setLayout(new BorderLayout());
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(listPanel), BorderLayout.CENTER);

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        totalPanel.add(totalCoinLabel);
        add(totalPanel, BorderLayout.SOUTH);
    }

    public void update(Map<Integer, Integer> runeCounts)
    {
        listPanel.removeAll();
        long total = 0;

        total += addSection("Elemental", RunePrices.ELEMENTAL_ORDER, runeCounts);
        total += addSection("Catalytic", RunePrices.CATALYTIC_ORDER, runeCounts);

        int totalCoins = (int) Math.min(total, Integer.MAX_VALUE);
        BufferedImage totalCoinImg = itemManager.getImage(ItemID.COINS_995, totalCoins, true);
        totalCoinLabel.setIcon(new ImageIcon(totalCoinImg));
        totalCoinLabel.setToolTipText(String.format("Total: %,d gp", total));

        listPanel.revalidate();
        listPanel.repaint();
        revalidate();
        repaint();
    }

    private long addSection(String title, List<Integer> order, Map<Integer, Integer> runeCounts)
    {
        long sectionTotal = 0;
        boolean headerAdded = false;

        for (int itemId : order)
        {
            Integer quantity = runeCounts.get(itemId);
            if (quantity == null || quantity <= 0)
            {
                continue;
            }

            if (!headerAdded)
            {
                JLabel header = new JLabel(title);
                header.setFont(header.getFont().deriveFont(Font.BOLD, 13f));
                header.setBorder(BorderFactory.createEmptyBorder(6, 4, 2, 0));
                listPanel.add(header);
                headerAdded = true;
            }

            int price = RunePrices.PRICES.getOrDefault(itemId, 0);
            long value = (long) quantity * price;
            sectionTotal += value;

            listPanel.add(buildRow(itemId, quantity, value));
        }

        return sectionTotal;
    }

    private JPanel buildRow(int itemId, int quantity, long value)
    {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));

        BufferedImage runeImg = itemManager.getImage(itemId, quantity, true);
        row.add(new JLabel(new ImageIcon(runeImg)));

        int coinQuantity = (int) Math.min(value, Integer.MAX_VALUE);
        BufferedImage coinImg = itemManager.getImage(ItemID.COINS_995, coinQuantity, true);
        row.add(new JLabel(new ImageIcon(coinImg)));

        row.add(new JLabel(String.format("%,d gp", value)));

        return row;
    }
}