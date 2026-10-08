package com.nugget.phosanisnightmare;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

/**
 * Shows which protection prayer to click and a tick countdown. This only
 * renders a callout - it never activates the prayer itself, so clicking is
 * still entirely up to the player.
 */
public class PrayerReminderOverlay extends Overlay
{
    private final PhosanisNightmareHelperPlugin plugin;
    private final PhosanisNightmareHelperConfig config;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    private PrayerReminderOverlay(PhosanisNightmareHelperPlugin plugin, PhosanisNightmareHelperConfig config)
    {
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.TOP_CENTER);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.showPrayerReminder())
        {
            return null;
        }

        NightmareAttackStyle pending = plugin.getPendingAttackStyle();
        if (pending == NightmareAttackStyle.NONE)
        {
            return null;
        }

        NightmareAttackStyle toClick = plugin.isCurseActive() ? pending.iconToClickUnderCurse() : pending;
        int ticks = plugin.getReactionTicksRemaining();

        panelComponent.getChildren().clear();
        panelComponent.setBackgroundColor(new Color(20, 20, 20, 215));
        panelComponent.setPreferredSize(new Dimension(230, 0));

        panelComponent.getChildren().add(TitleComponent.builder()
                .text(ticks <= 0 ? "NOW" : String.valueOf(ticks))
                .color(toClick.getColor())
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left(toClick.getLabel())
                .leftColor(Color.WHITE)
                .build());

        if (plugin.isCurseActive())
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Curse active")
                    .leftColor(new Color(255, 120, 255))
                    .build());
        }

        return panelComponent.render(graphics);
    }
}