package com.nugget.phosanisnightmare;

import com.google.inject.Provides;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.GraphicsObject;
import net.runelite.api.NPC;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.DecorativeObjectDespawned;
import net.runelite.api.events.DecorativeObjectSpawned;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicChanged;
import net.runelite.api.events.GroundObjectDespawned;
import net.runelite.api.events.GroundObjectSpawned;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

/**
 * Reads Phosani's Nightmare game state (object names, animation IDs, graphic
 * IDs) and draws overlays. It never issues clicks, key presses, or any other
 * simulated input - every action, including prayer switches, stays with the
 * player. See README.md for why, and for how to calibrate the animation and
 * graphic IDs below (Jagex doesn't publish these, so they start unset).
 */
@Slf4j
@PluginDescriptor(
        name = "Phosani's Nightmare Helper",
        description = "Corpse Flowers / Grasping Claws tile overlay and a prayer reminder for Phosani's Nightmare. Read-only - never sends input.",
        tags = {"nightmare", "phosani", "boss", "pvm", "prayer", "overlay"}
)
public class PhosanisNightmareHelperPlugin extends Plugin
{
    private static final String BOSS_NAME_PHOSANI = "phosani's nightmare";
    private static final String BOSS_NAME_NIGHTMARE = "the nightmare";
    private static final String OBJECT_SAFE = "nightmare blossom";
    private static final String OBJECT_UNSAFE = "nightmare berries";
    private static final int CURSE_ATTACK_COUNT = 5;

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private FloorHazardOverlay floorHazardOverlay;

    @Inject
    private PrayerReminderOverlay prayerReminderOverlay;

    @Inject
    private PhosanisNightmareHelperConfig config;

    private final Set<WorldPoint> safeFlowerTiles = new HashSet<>();
    private final Set<WorldPoint> unsafeFlowerTiles = new HashSet<>();
    private final Set<WorldPoint> clawTiles = new HashSet<>();
    private final Set<Integer> loggedGraphicsObjectIds = new HashSet<>();
    private final Set<Integer> loggedPlayerGraphicIds = new HashSet<>();
    private Set<Integer> clawGraphicIds = new HashSet<>();

    private NPC target;
    private NightmareAttackStyle pendingAttackStyle = NightmareAttackStyle.NONE;
    private int reactionTicksRemaining;
    private int curseAttacksRemaining;

    @Provides
    PhosanisNightmareHelperConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(PhosanisNightmareHelperConfig.class);
    }

    @Override
    protected void startUp()
    {
        overlayManager.add(floorHazardOverlay);
        overlayManager.add(prayerReminderOverlay);
        parseClawGraphicIds();
        reset();
        log.debug("Phosani's Nightmare Helper started");
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(floorHazardOverlay);
        overlayManager.remove(prayerReminderOverlay);
        reset();
    }

    private void reset()
    {
        safeFlowerTiles.clear();
        unsafeFlowerTiles.clear();
        clawTiles.clear();
        loggedGraphicsObjectIds.clear();
        loggedPlayerGraphicIds.clear();
        target = null;
        pendingAttackStyle = NightmareAttackStyle.NONE;
        reactionTicksRemaining = 0;
        curseAttacksRemaining = 0;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() == GameState.LOADING || event.getGameState() == GameState.HOPPING)
        {
            reset();
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (event.getGroup().equals("phosanisnightmarehelper"))
        {
            parseClawGraphicIds();
        }
    }

    private void parseClawGraphicIds()
    {
        Set<Integer> ids = new HashSet<>();
        String raw = config.graspingClawGraphicIds();
        if (raw != null && !raw.trim().isEmpty())
        {
            for (String part : raw.split(","))
            {
                try
                {
                    ids.add(Integer.parseInt(part.trim()));
                }
                catch (NumberFormatException e)
                {
                    log.warn("Ignoring invalid Grasping Claw graphic ID '{}'", part);
                }
            }
        }
        clawGraphicIds = ids;
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event)
    {
        NPC npc = event.getNpc();
        String name = npc.getName();
        if (name == null)
        {
            return;
        }
        String lower = name.toLowerCase();
        if (lower.equals(BOSS_NAME_PHOSANI) || lower.equals(BOSS_NAME_NIGHTMARE))
        {
            target = npc;
            log.debug("Tracking {}", name);
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event)
    {
        if (event.getNpc() == target)
        {
            target = null;
            pendingAttackStyle = NightmareAttackStyle.NONE;
            reactionTicksRemaining = 0;
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
        handleObjectSpawn(event.getGameObject().getId(), event.getGameObject().getWorldLocation());
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event)
    {
        handleObjectDespawn(event.getGameObject().getWorldLocation());
    }

    @Subscribe
    public void onGroundObjectSpawned(GroundObjectSpawned event)
    {
        handleObjectSpawn(event.getGroundObject().getId(), event.getGroundObject().getWorldLocation());
    }

    @Subscribe
    public void onGroundObjectDespawned(GroundObjectDespawned event)
    {
        handleObjectDespawn(event.getGroundObject().getWorldLocation());
    }

    @Subscribe
    public void onDecorativeObjectSpawned(DecorativeObjectSpawned event)
    {
        handleObjectSpawn(event.getDecorativeObject().getId(), event.getDecorativeObject().getWorldLocation());
    }

    @Subscribe
    public void onDecorativeObjectDespawned(DecorativeObjectDespawned event)
    {
        handleObjectDespawn(event.getDecorativeObject().getWorldLocation());
    }

    private void handleObjectSpawn(int id, WorldPoint worldPoint)
    {
        ObjectComposition comp = client.getObjectDefinition(id);
        if (comp == null || comp.getName() == null)
        {
            return;
        }

        String name = comp.getName().toLowerCase();
        if (name.equals(OBJECT_SAFE))
        {
            safeFlowerTiles.add(worldPoint);
            unsafeFlowerTiles.remove(worldPoint);
        }
        else if (name.equals(OBJECT_UNSAFE))
        {
            unsafeFlowerTiles.add(worldPoint);
            safeFlowerTiles.remove(worldPoint);
        }
    }

    private void handleObjectDespawn(WorldPoint worldPoint)
    {
        safeFlowerTiles.remove(worldPoint);
        unsafeFlowerTiles.remove(worldPoint);
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event)
    {
        Actor actor = event.getActor();
        if (target == null || actor != target)
        {
            return;
        }

        int animId = actor.getAnimation();

        if (config.debugLogging())
        {
            debugMessage("Nightmare animation -> " + animId);
        }

        NightmareAttackStyle style = matchAttackStyle(animId);
        if (style != NightmareAttackStyle.NONE)
        {
            pendingAttackStyle = style;
            reactionTicksRemaining = style.getReactionTicks();
        }
    }

    private NightmareAttackStyle matchAttackStyle(int animId)
    {
        if (config.meleeAnimationId() != -1 && animId == config.meleeAnimationId())
        {
            return NightmareAttackStyle.MELEE;
        }
        if (config.rangedAnimationId() != -1 && animId == config.rangedAnimationId())
        {
            return NightmareAttackStyle.RANGED;
        }
        if (config.magicAnimationId() != -1 && animId == config.magicAnimationId())
        {
            return NightmareAttackStyle.MAGIC;
        }
        return NightmareAttackStyle.NONE;
    }

    @Subscribe
    public void onGraphicChanged(GraphicChanged event)
    {
        Actor actor = event.getActor();
        Player local = client.getLocalPlayer();
        if (local == null || actor != local)
        {
            return;
        }

        int graphicId = actor.getGraphic();
        if (graphicId == -1)
        {
            return;
        }

        if (config.debugLogging() && loggedPlayerGraphicIds.add(graphicId))
        {
            debugMessage("Your graphic -> " + graphicId);
        }

        if (config.curseGraphicId() != -1 && graphicId == config.curseGraphicId())
        {
            curseAttacksRemaining = CURSE_ATTACK_COUNT;
        }
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (pendingAttackStyle != NightmareAttackStyle.NONE)
        {
            if (reactionTicksRemaining > 0)
            {
                reactionTicksRemaining--;
            }
            else
            {
                if (curseAttacksRemaining > 0)
                {
                    curseAttacksRemaining--;
                }
                pendingAttackStyle = NightmareAttackStyle.NONE;
            }
        }

        clawTiles.clear();
        for (GraphicsObject go : client.getGraphicsObjects())
        {
            if (clawGraphicIds.contains(go.getId()))
            {
                LocalPoint lp = go.getLocation();
                if (lp != null)
                {
                    clawTiles.add(WorldPoint.fromLocal(client, lp));
                }
            }
            else if (config.debugLogging() && target != null && loggedGraphicsObjectIds.add(go.getId()))
            {
                debugMessage("Graphics object -> id " + go.getId());
            }
        }
    }

    private void debugMessage(String message)
    {
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "[Phosani Helper] " + message, null);
    }

    public Set<WorldPoint> getSafeFlowerTiles()
    {
        return safeFlowerTiles;
    }

    public Set<WorldPoint> getUnsafeFlowerTiles()
    {
        return unsafeFlowerTiles;
    }

    public Set<WorldPoint> getClawTiles()
    {
        return clawTiles;
    }

    public NightmareAttackStyle getPendingAttackStyle()
    {
        return pendingAttackStyle;
    }

    public int getReactionTicksRemaining()
    {
        return reactionTicksRemaining;
    }

    public boolean isCurseActive()
    {
        return curseAttacksRemaining > 0;
    }
}