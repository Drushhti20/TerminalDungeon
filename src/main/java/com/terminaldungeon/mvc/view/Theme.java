package com.terminaldungeon.mvc.view;

import java.awt.Color;
import java.awt.Font;

// Colours and fonts shared by the Swing view classes
final class Theme {
    static final Color BACKGROUND = new Color(20, 18, 26);
    static final Color PANEL = new Color(33, 29, 43);
    static final Color PANEL_DARK = new Color(24, 21, 32);
    static final Color PANEL_BORDER = new Color(70, 62, 92);
    static final Color GOLD = new Color(232, 184, 74);
    static final Color TEXT = new Color(230, 225, 240);
    static final Color MUTED = new Color(141, 134, 163);
    static final Color DANGER = new Color(217, 83, 79);
    static final Color HEAL = new Color(92, 184, 92);
    static final Color WARNING = new Color(240, 160, 50);
    static final Color EVENT = new Color(186, 140, 255);
    static final Color SYSTEM = new Color(120, 170, 230);
    static final Color SLATE = new Color(74, 85, 120);

    private Theme() {
    }

    static Font title(float size) {
        return new Font("Georgia", Font.BOLD, Math.round(size));
    }

    static Font ui(int style, float size) {
        return new Font(Font.SANS_SERIF, style, Math.round(size));
    }

    static Font mono(float size) {
        return new Font(Font.MONOSPACED, Font.PLAIN, Math.round(size));
    }

    // green above half health, orange at half, red at a quarter
    static Color healthColor(int health, int maxHealth) {
        if (health * 4 <= maxHealth) {
            return DANGER;
        }
        if (health * 2 <= maxHealth) {
            return WARNING;
        }
        return HEAL;
    }

    static Color blend(Color from, Color to, float amount) {
        float keep = 1f - amount;
        return new Color(
                Math.round(from.getRed() * keep + to.getRed() * amount),
                Math.round(from.getGreen() * keep + to.getGreen() * amount),
                Math.round(from.getBlue() * keep + to.getBlue() * amount));
    }
}
