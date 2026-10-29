package com.terminaldungeon.mvc.view;

import javax.swing.JToggleButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

// Selectable card for an equipment preset on the setup screen; gold border when chosen
final class PresetCard extends JToggleButton {
    private final String title;
    private final String description;

    PresetCard(String title, String description) {
        this.title = title;
        this.description = description;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(230, 120));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        boolean selected = isSelected();

        g.setColor(selected ? new Color(52, 43, 30) : Theme.PANEL_DARK);
        g.fillRoundRect(0, 0, width - 1, height - 1, 16, 16);
        g.setColor(selected ? Theme.GOLD : getModel().isRollover() ? Theme.MUTED : Theme.PANEL_BORDER);
        g.setStroke(new java.awt.BasicStroke(selected ? 3f : 1.5f));
        g.drawRoundRect(1, 1, width - 3, height - 3, 16, 16);

        SpriteArt.draw(g, SpriteArt.HERO, 14, (height - SpriteArt.height(SpriteArt.HERO, 4)) / 2, 4);

        int textX = 14 + SpriteArt.width(SpriteArt.HERO, 4) + 14;
        g.setFont(Theme.ui(Font.BOLD, 17));
        g.setColor(selected ? Theme.GOLD : Theme.TEXT);
        g.drawString(title, textX, 44);

        g.setFont(Theme.ui(Font.PLAIN, 13));
        g.setColor(Theme.MUTED);
        int lineY = 68;
        for (String line : description.split("\n")) {
            g.drawString(line, textX, lineY);
            lineY += 18;
        }

        if (selected) {
            g.setFont(Theme.ui(Font.BOLD, 11));
            g.setColor(Theme.GOLD);
            g.drawString("SELECTED", width - 70, 22);
        }
        g.dispose();
    }
}
