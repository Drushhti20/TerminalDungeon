package com.terminaldungeon.mvc.view;

import com.terminaldungeon.prototype.Enemy;

import javax.swing.Icon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Map;

// Pixel-art sprites for the Swing view. Each letter is one pixel, '.' is transparent.
// The hero faces right and the enemies face left, so they look at each other in the arena.
final class SpriteArt {
    static final String[] HERO = {
            "....hHHHHh......",
            "...hHHHHHHh.....",
            "...hHYYYYHh.....",
            "...hSSSSSSh....W",
            "...SSESSESS...W.",
            "...SSSSSSSS..W..",
            "....SSSSSS..W...",
            "...aAAAAAAa.W...",
            "..aAAYAAYAAAw...",
            "..SaAAYYAAAaS...",
            "..S.aAAAAAa.....",
            "....aAAAAAa.....",
            "....LLL.LLL.....",
            "....LLL.LLL.....",
            "....LLL.LLL.....",
            "...BBBB.BBBB....",
    };

    static final String[] GOBLIN = {
            "................",
            "................",
            "G....gGGGGg....G",
            "GG..gGGGGGGg..GG",
            ".GGgGGGGGGGGgGG.",
            "..GGGRGGGGRGGG..",
            "...GGGGGGGGGG...",
            "....GTGTTGTG....",
            ".....gGGGGg.....",
            "..K.CCCCCCCC....",
            "..KGCCCCCCCCG...",
            "..KG.CCCCCC.G...",
            "..w..CCCCCC.....",
            ".....GG..GG.....",
            ".....GG..GG.....",
            "....ggg..ggg....",
    };

    static final String[] SKELETON = {
            "................",
            ".....zZZZZz.....",
            "....zZZZZZZz....",
            "....ZEEZZEEZ....",
            "....ZERZZERZ....",
            "....zZZEEZZz....",
            ".....ZEZEZE.....",
            "......zZZz......",
            "K....ZzZZzZ.....",
            "K..ZZ.ZzzZ.ZZ...",
            "K.Z...ZzzZ...Z..",
            "w.Z...zZZz...Z..",
            "..Z....ZZ....Z..",
            "......Z..Z......",
            "......Z..Z......",
            ".....zZ..Zz.....",
    };

    static final String[] ORC = {
            "................",
            ".....oOOOOOo....",
            "....oOOOOOOOo...",
            "....OORROORROO..",
            "....OOOOOOOOOO..",
            "....OTOOOOOOTO..",
            "....OTOooooOTO..",
            "X....OOOOOOOO...",
            "XX.MMMMMMMMMMM..",
            "XXOMMMYMMMYMMMO.",
            ".xOOMMMMMMMMMOO.",
            ".x..MMMMMMMMM...",
            ".x..ooo...ooo...",
            ".x..ooo...ooo...",
            ".x..OOO...OOO...",
            "...MMMM...MMMM..",
    };

    private static final Map<Character, Color> PALETTE = Map.ofEntries(
            Map.entry('H', new Color(154, 164, 181)),
            Map.entry('h', new Color(107, 115, 133)),
            Map.entry('S', new Color(241, 194, 125)),
            Map.entry('E', new Color(34, 34, 34)),
            Map.entry('A', new Color(63, 111, 209)),
            Map.entry('a', new Color(44, 79, 154)),
            Map.entry('Y', new Color(232, 184, 74)),
            Map.entry('W', new Color(223, 230, 238)),
            Map.entry('w', new Color(139, 90, 43)),
            Map.entry('L', new Color(58, 63, 85)),
            Map.entry('B', new Color(91, 58, 30)),
            Map.entry('G', new Color(108, 191, 58)),
            Map.entry('g', new Color(74, 138, 38)),
            Map.entry('R', new Color(255, 59, 59)),
            Map.entry('T', new Color(245, 240, 220)),
            Map.entry('C', new Color(139, 90, 43)),
            Map.entry('K', new Color(184, 192, 204)),
            Map.entry('O', new Color(95, 125, 74)),
            Map.entry('o', new Color(67, 89, 58)),
            Map.entry('M', new Color(107, 74, 58)),
            Map.entry('X', new Color(192, 198, 208)),
            Map.entry('x', new Color(107, 68, 35)),
            Map.entry('Z', new Color(232, 228, 216)),
            Map.entry('z', new Color(169, 163, 149)));

    private SpriteArt() {
    }

    // display choice only: which picture to draw for an enemy type
    static String[] forEnemy(Enemy enemy) {
        if ("Orc".equalsIgnoreCase(enemy.getType())) {
            return ORC;
        }
        if ("Skeleton".equalsIgnoreCase(enemy.getType())) {
            return SKELETON;
        }
        return GOBLIN;
    }

    // Orcs are drawn bigger than Goblins
    static int pixelSizeFor(String[] sprite, int basePixel) {
        return sprite == ORC ? basePixel + 2 : basePixel;
    }

    static int width(String[] sprite, int pixel) {
        return sprite[0].length() * pixel;
    }

    static int height(String[] sprite, int pixel) {
        return sprite.length * pixel;
    }

    static void draw(Graphics2D g, String[] sprite, int x, int y, int pixel) {
        drawTinted(g, sprite, x, y, pixel, null, 0f);
    }

    // tint blends every pixel toward one colour, used for hit and heal flashes
    static void drawTinted(Graphics2D g, String[] sprite, int x, int y, int pixel, Color tint, float amount) {
        for (int row = 0; row < sprite.length; row++) {
            String line = sprite[row];
            for (int col = 0; col < line.length(); col++) {
                Color color = PALETTE.get(line.charAt(col));
                if (color == null) {
                    continue;
                }
                g.setColor(tint == null ? color : Theme.blend(color, tint, amount));
                g.fillRect(x + col * pixel, y + row * pixel, pixel, pixel);
            }
        }
    }

    static Icon icon(String[] sprite, int pixel) {
        return new Icon() {
            @Override
            public void paintIcon(Component component, Graphics g, int x, int y) {
                draw((Graphics2D) g, sprite, x, y, pixel);
            }

            @Override
            public int getIconWidth() {
                return width(sprite, pixel);
            }

            @Override
            public int getIconHeight() {
                return height(sprite, pixel);
            }
        };
    }
}
