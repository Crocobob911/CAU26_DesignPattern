package com.holub.life;

import com.holub.ui.Colors;
import java.awt.Color;

/** The original Holub colors. The default theme, so the board looks the same as before. */
final class ClassicTheme extends PaletteTheme {
    ClassicTheme() {
        super("Classic", Color.WHITE, Color.RED, Colors.LIGHT_YELLOW,
                Colors.DARK_YELLOW, Colors.LIGHT_ORANGE, Color.BLUE);
    }
}
