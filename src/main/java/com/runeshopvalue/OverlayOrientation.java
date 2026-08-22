package com.runeshopvalue;

public enum OverlayOrientation
{
    VERTICAL("Vertical"),
    HORIZONTAL("Horizontal");

    private final String label;

    OverlayOrientation(String label)
    {
        this.label = label;
    }

    @Override
    public String toString()
    {
        return label;
    }
}