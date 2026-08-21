package com.j3d.ui.theme;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public enum DefaultThemes {
    DEFAULT(
          new ThemeEntry(
                  "Default",
                  new Color(0xcad2c5),
                  new Color(0xc5e0c6),
                  new Color(0x84a98c),
                  new Color(0x52796f),
                  new Color(0x354f52),
                  new Color(0x2f3e46),
                  ThemeEntry.ThemeType.DARK
          )
    ),
    JOKE_MODE(
            new ThemeEntry(
                    "Joke Mode",
                    new Color(0xaaaaaa),
                    new Color(0xbbbbbb),
                    new Color(0xcccccc),
                    new Color(0xdddddd),
                    new Color(0xeeeeee),
                    new Color(0xffffff),
                    ThemeEntry.ThemeType.LIGHT
            )
    ),
    SAND(
            new ThemeEntry(
                    "Sand",
                    new Color(0xEBF38B),
                    new Color(0xF0EE84),
                    new Color(0xF4E87C),
                    new Color(0xCBBF7A),
                    new Color(0x9F956C),
                    new Color(0x584D3D),
                    ThemeEntry.ThemeType.DARK
            )
    ),
    BUBBLEGUM(
            new ThemeEntry(
                    "Bubblegum",
                    new Color(0xFFDDE2),
                    new Color(0xEFD6D2),
                    new Color(0xFF8CC6),
                    new Color(0xDE369D),
                    new Color(0xA74A8A),
                    new Color(0x6F5E76),
                    ThemeEntry.ThemeType.DARK
            )
    ),
    OCEAN(
            new ThemeEntry(
                    "Ocean",
                    new Color(0x78C0E0),
                    new Color(0x449DD1),
                    new Color(0x192BC2),
                    new Color(0x150578),
                    new Color(0x120A65),
                    new Color(0x0E0E52),
                    ThemeEntry.ThemeType.DARK
            )
    ),
    DISCORD_LIGHT(
            new ThemeEntry(
                    "What Discord Light Mode Feels Like",
                    new Color(0xBEBEE5),
                    new Color(0xCBCBEC),
                    new Color(0xD8D8F3),
                    new Color(0xE7E7F8),
                    new Color(0xF2F2FE),
                    new Color(0xFFFFFF),
                    ThemeEntry.ThemeType.LIGHT
            )
    ),
    MONDAYS(
            new ThemeEntry(
                    "Mondays",
                    new Color(0x939271),
                    new Color(0xabaa88),
                    new Color(0xc4c2a0),
                    new Color(0xdddbb8),
                    new Color(0xfefef2) ,
                    new Color(0xf7f5d1),
                    ThemeEntry.ThemeType.LIGHT
            )
    ),
    ESPRESSO(
            new ThemeEntry(
                    "Espresso",
                    new Color(0xffddc7),
                    new Color(0xd3a774),
                    new Color(0x341600),
                    new Color(0x4b4523),
                    new Color(0x64481f) ,
                    new Color(0x303a25),
            ThemeEntry.ThemeType.DARK
            )
    ),
    SKY_DIVER(
            new ThemeEntry(
                    "Sky Diver",
                    new Color(0xcee2cc),
                    new Color(0xb6cabe),
                    new Color(0x9fb2af),
                    new Color(0x889ba0),
                    new Color(0x728591) ,
                    new Color(0x5d6f83),
            ThemeEntry.ThemeType.SATURATED
            )
    ),
    BIG_GREENS(
            new ThemeEntry(
                    "Big Greens",
                    new Color(0x35bf58),
                    new Color(0x1fab48),
                    new Color(0x12b05d),
                    new Color(0x47c85f),
                    new Color(0x8cf641) ,
                    new Color(0x7efa74),
            ThemeEntry.ThemeType.SATURATED
            )
    ),
    BUBBLEGUM_2(
            new ThemeEntry(
                    "Bubblegum 2",
                    new Color(0xf9a790),
                    new Color(0xf5a087),
                    new Color(0xe58c6e),
                    new Color(0xe38b6c),
                    new Color(0xd3805f) ,
                    new Color(0xce7d5c),
            ThemeEntry.ThemeType.SATURATED
            )
    );

    DefaultThemes(ThemeEntry entry) {
        this.themeEntry = entry;
    }

    private ThemeEntry themeEntry;

    public ThemeEntry getThemeEntry() {
        return themeEntry;
    }
}
