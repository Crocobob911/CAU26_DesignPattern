package com.holub.life;

import java.awt.Color;

/** Dark background with green live cells. */
final class DarkTheme extends PaletteTheme {
    DarkTheme() {
        super("Dark",
                new Color(0x12, 0x12, 0x12),   // background
                new Color(0x00, 0xe6, 0x76),   // live cell
                new Color(0x1e, 0x1e, 0x1e),   // dead cell
                new Color(0x33, 0x33, 0x33),   // grid line
                new Color(0x5a, 0x5a, 0x5a),   // block border
                new Color(0x29, 0x79, 0xff),   // active block
                new Color(0xb2, 0xff, 0x59),   // young cell
                new Color(0x00, 0x96, 0x88));  // old cell
    }
}
