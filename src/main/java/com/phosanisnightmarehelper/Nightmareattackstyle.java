package com.nugget.phosanisnightmare;

import java.awt.Color;

/**
 * The three protectable auto-attack styles Phosani's Nightmare uses, plus
 * NONE for "nothing incoming right now". Reaction windows come from the
 * OSRS Wiki's Phosani's Nightmare strategy page - re-check against a live
 * kill if Jagex ever rebalances the fight.
 */
public enum NightmareAttackStyle
{
    NONE(0, "", Color.WHITE),
    MELEE(2, "PROTECT FROM MELEE", new Color(170, 170, 170)),
    RANGED(3, "PROTECT FROM MISSILES", new Color(80, 170, 80)),
    MAGIC(3, "PROTECT FROM MAGIC", new Color(70, 120, 210));

    private final int reactionTicks;
    private final String label;
    private final Color color;

    NightmareAttackStyle(int reactionTicks, String label, Color color)
    {
        this.reactionTicks = reactionTicks;
        this.label = label;
        this.color = color;
    }

    public int getReactionTicks()
    {
        return reactionTicks;
    }

    public String getLabel()
    {
        return label;
    }

    public Color getColor()
    {
        return color;
    }

    /**
     * Phosani's Curse shuffles the protection prayers: clicking Melee
     * activates Magic, clicking Magic activates Missiles, and clicking
     * Missiles activates Melee. Given the style you actually need active,
     * this returns the icon you need to click to get it.
     */
    public NightmareAttackStyle iconToClickUnderCurse()
    {
        switch (this)
        {
            case MAGIC:
                return MELEE;
            case RANGED:
                return MAGIC;
            case MELEE:
                return RANGED;
            default:
                return this;
        }
    }
}