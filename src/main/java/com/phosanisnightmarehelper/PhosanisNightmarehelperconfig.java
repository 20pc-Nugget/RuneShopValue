package com.nugget.phosanisnightmare;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("phosanisnightmarehelper")
public interface PhosanisNightmareHelperConfig extends Config
{
    @ConfigSection(
            name = "Floor attack",
            description = "Corpse Flowers safe-quadrant and Grasping Claws tile highlighting",
            position = 0
    )
    String floorSection = "floorSection";

    @ConfigItem(
            keyName = "showCorpseFlowers",
            name = "Highlight Corpse Flowers",
            description = "Colours the safe (white) and unsafe (red) flower quadrant tiles",
            section = floorSection,
            position = 0
    )
    default boolean showCorpseFlowers()
    {
        return true;
    }

    @ConfigItem(
            keyName = "showGraspingClaws",
            name = "Highlight Grasping Claws",
            description = "Colours tiles with an active Grasping Claws portal",
            section = floorSection,
            position = 1
    )
    default boolean showGraspingClaws()
    {
        return true;
    }

    @ConfigItem(
            keyName = "graspingClawGraphicIds",
            name = "Grasping Claw graphic ID(s)",
            description = "Comma-separated graphic/spotanim IDs for the claw portals. Jagex doesn't publish these - use Debug logging to find them, see README.",
            section = floorSection,
            position = 2
    )
    default String graspingClawGraphicIds()
    {
        return "";
    }

    @ConfigSection(
            name = "Prayer reminder",
            description = "On-screen callout for which prayer to click - this never clicks it for you",
            position = 1
    )
    String prayerSection = "prayerSection";

    @ConfigItem(
            keyName = "showPrayerReminder",
            name = "Show prayer reminder",
            description = "Displays a callout for which prayer to click and a tick countdown",
            section = prayerSection,
            position = 0
    )
    default boolean showPrayerReminder()
    {
        return true;
    }

    @ConfigItem(
            keyName = "meleeAnimationId",
            name = "Melee attack animation ID",
            description = "Animation ID for her melee claw swipe. Unset (-1) until you calibrate it - see README.",
            section = prayerSection,
            position = 1
    )
    default int meleeAnimationId()
    {
        return -1;
    }

    @ConfigItem(
            keyName = "rangedAnimationId",
            name = "Ranged attack animation ID",
            description = "Animation ID for her ranged contort attack.",
            section = prayerSection,
            position = 2
    )
    default int rangedAnimationId()
    {
        return -1;
    }

    @ConfigItem(
            keyName = "magicAnimationId",
            name = "Magic attack animation ID",
            description = "Animation ID for her magic petal-flail attack.",
            section = prayerSection,
            position = 3
    )
    default int magicAnimationId()
    {
        return -1;
    }

    @ConfigItem(
            keyName = "curseGraphicId",
            name = "Curse graphic ID",
            description = "Graphic ID applied to you while Curse is active. Optional - leave -1 to ignore Curse.",
            section = prayerSection,
            position = 4
    )
    default int curseGraphicId()
    {
        return -1;
    }

    @ConfigSection(
            name = "Debug",
            description = "Tools for finding the IDs above from a live kill",
            position = 2
    )
    String debugSection = "debugSection";

    @ConfigItem(
            keyName = "debugLogging",
            name = "Log unidentified animation/graphic IDs",
            description = "Prints animation and graphic ID changes to the chatbox so you can fill in the fields above from one kill",
            section = debugSection,
            position = 0
    )
    default boolean debugLogging()
    {
        return false;
    }
}