package com.pelley.indexgestorum.client;

import net.minecraft.client.gui.GuiGraphics;

// Colors are the rarity hex values from design/data/03-Rarity-and-Scarcity.csv.
// Each rarity also gets its own pip shape so it can be told apart without color.
public enum Rarity
{
    COMMON("Common", 0xAAAAAA,
            ".###.",
            "#####",
            "#####",
            "#####",
            ".###."),
    UNCOMMON("Uncommon", 0x55FF55,
            "#####",
            "#####",
            "#####",
            "#####",
            "#####"),
    RARE("Rare", 0x55FFFF,
            "..#..",
            ".###.",
            ".###.",
            "#####",
            "#####"),
    EPIC("Epic", 0xFF55FF,
            "..#..",
            ".###.",
            "#####",
            ".###.",
            "..#.."),
    LEGENDARY("Legendary", 0xFFAA00,
            "..#..",
            "#####",
            ".###.",
            ".###.",
            "#...#"),
    MYTHIC("Mythic", 0xFF5555,
            "#...#",
            ".#.#.",
            "..#..",
            ".#.#.",
            "#...#"),
    UNIQUE("Unique", 0x00AAAA,
            "#####",
            "#...#",
            "#.#.#",
            "#...#",
            "#####");

    private final String displayName;
    private final int color;
    private final String[] pip;

    Rarity(String displayName, int color, String... pip)
    {
        this.displayName = displayName;
        this.color = color;
        this.pip = pip;
    }

    public String displayName()
    {
        return displayName;
    }

    public int color()
    {
        return color;
    }

    public void drawPip(GuiGraphics graphics, int x, int y)
    {
        int argb = 0xFF000000 | color;
        for (int row = 0; row < pip.length; row++)
        {
            String line = pip[row];
            for (int col = 0; col < line.length(); col++)
            {
                if (line.charAt(col) == '#') graphics.fill(x + col, y + row, x + col + 1, y + row + 1, argb);
            }
        }
    }
}
