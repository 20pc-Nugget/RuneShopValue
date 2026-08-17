package com.runeshopvalue;

import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

public class RuneShopValuePanel extends PluginPanel
{
    private final JPanel listPanel = new JPanel();
    private final JLabel totalCoinLabel = new JLabel();
    private final JLabel totalTextLabel = new JLabel("Total: 0 gp");
    private final ItemManager itemManager;

    public RuneShopValuePanel(ItemManager itemManager)
    {
        this.itemManager = itemManager;

        setLayout(new BorderLayout());
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(listPanel), BorderLayout.CENTER);

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        totalTextLabel.setFont(totalTextLabel.getFont().deriveFont(Font.BOLD));
        totalPanel.add(totalCoinLabel);
        totalPanel.add(totalTextLabel);
        add(totalPanel, BorderLayout.SOUTH);
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

        int totalCoins = (int) Math.min(total, Integer.MAX_VALUE);
        BufferedImage coinImg = itemManager.getImage(ItemID.COINS_995, totalCoins, true);
        totalCoinLabel.setIcon(new ImageIcon(coinImg));
        totalTextLabel.setText("Total: " + String.format("%,d gp", total));

        listPanel.revalidate();
        listPanel.repaint();
        revalidate();
        repaint();
    }

    private JPanel buildRow(int itemId, int quantity, long value)
    {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));

        BufferedImage runeImg = itemManager.getImage(itemId, quantity, true);
        JLabel runeLabel = new JLabel(new ImageIcon(runeImg));
        row.add(runeLabel);

        int coinQuantity = (int) Math.min(value, Integer.MAX_VALUE);
        BufferedImage coinImg = itemManager.getImage(ItemID.COINS_995, coinQuantity, true);
        JLabel coinLabel = new JLabel(new ImageIcon(coinImg));
        row.add(coinLabel);

        JLabel valueLabel = new JLabel(String.format("%,d gp", value));
        row.add(valueLabel);

        return row;
    }
}