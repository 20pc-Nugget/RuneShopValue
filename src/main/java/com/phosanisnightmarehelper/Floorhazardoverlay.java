package com.phosanisnightmarehelper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

/**
 * Colours in the Corpse Flowers safe/unsafe quadrant tiles and any active
 * Grasping Claws portal tiles. Purely a rendering pass over game state the
 * plugin already collected - no input is sent.
 */
public class FloorHazardOverlay extends Overlay
{
    private static final Color SAFE_FILL = new Color(0, 200, 0, 90);
    private static final Color SAFE_BORDER = new Color(0, 255, 0, 200);
    private static final Color UNSAFE_FILL = new Color(200, 0, 0, 90);
    private static final Color UNSAFE_BORDER = new Color(255, 0, 0, 200);
    private static final Color CLAW_FILL = new Color(255, 60, 0, 130);
    private static final Color CLAW_BORDER = new Color(255, 140, 0, 220);

    private final Client client;
    private final PhosanisNightmareHelperPlugin plugin;
    private final PhosanisNightmareHelperConfig config;

    @Inject
    private FloorHazardOverlay(Client client, PhosanisNightmareHelperPlugin plugin, PhosanisNightmareHelperConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (config.showCorpseFlowers())
        {
            for (WorldPoint wp : plugin.getSafeFlowerTiles())
            {
                drawTile(graphics, wp, SAFE_FILL, SAFE_BORDER);
            }
            for (WorldPoint wp : plugin.getUnsafeFlowerTiles())
            {
                drawTile(graphics, wp, UNSAFE_FILL, UNSAFE_BORDER);
            }
        }

        if (config.showGraspingClaws())
        {
            for (WorldPoint wp : plugin.getClawTiles())
            {
                drawTile(graphics, wp, CLAW_FILL, CLAW_BORDER);
            }
        }

        return null;
    }

    private void drawTile(Graphics2D graphics, WorldPoint worldPoint, Color fill, Color border)
    {
        if (worldPoint.getPlane() != client.getPlane())
        {
            return;
        }

        LocalPoint lp = LocalPoint.fromWorld(client, worldPoint);
        if (lp == null)
        {
            return;
        }

        Polygon poly = Perspective.getCanvasTilePoly(client, lp);
        if (poly == null)
        {
            return;
        }

        graphics.setColor(fill);
        graphics.fillPolygon(poly);
        graphics.setColor(border);
        graphics.drawPolygon(poly);
    }
}