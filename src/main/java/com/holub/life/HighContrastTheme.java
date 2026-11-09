package com.holub.life;

import java.awt.Color;

/** Black and white cells with bright borders, for low-vision users and projectors. */
final class HighContrastTheme extends PaletteTheme {
    HighContrastTheme() {
        super("High Contrast", Color.BLACK, Color.WHITE, Color.BLACK,
                new Color(0x40, 0x40, 0x40), Color.YELLOW, Color.CYAN);
    }
}
