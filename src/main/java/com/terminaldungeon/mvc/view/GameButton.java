package com.terminaldungeon.mvc.view;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

// Flat game-style button with a keyboard hint under the label; grey when disabled
final class GameButton extends JButton {
    private final Color base;
    private final String hint;

    GameButton(String label, String hint, Color base) {
        super(label);
        this.base = base;
        this.hint = hint;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(140, hint == null ? 48 : 56));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        ButtonModel model = getModel();
        Color fill;
        if (!isEnabled()) {
            fill = new Color(52, 48, 62);
        } else if (model.isPressed()) {
            fill = base.darker();
        } else if (model.isRollover()) {
            fill = Theme.blend(base, Color.WHITE, 0.18f);
        } else {
            fill = base;
        }

        int width = getWidth();
        int height = getHeight();
        g.setPaint(new GradientPaint(0, 0, Theme.blend(fill, Color.WHITE, 0.12f), 0, height, fill.darker()));
        g.fillRoundRect(0, 0, width - 1, height - 1, 14, 14);
        g.setColor(isEnabled() ? fill.darker().darker() : new Color(40, 37, 48));
        g.drawRoundRect(0, 0, width - 1, height - 1, 14, 14);

        Color textColor = isEnabled() ? Color.WHITE : new Color(110, 104, 125);
        g.setFont(Theme.ui(Font.BOLD, 16));
        FontMetrics metrics = g.getFontMetrics();
        int labelY = hint == null
                ? (height - metrics.getHeight()) / 2 + metrics.getAscent()
                : height / 2 - 2;
        g.setColor(textColor);
        g.drawString(getText(), (width - metrics.stringWidth(getText())) / 2, labelY);

        if (hint != null) {
            g.setFont(Theme.ui(Font.PLAIN, 11));
            FontMetrics hintMetrics = g.getFontMetrics();
            g.setColor(isEnabled() ? new Color(255, 255, 255, 160) : new Color(90, 85, 105));
            g.drawString(hint, (width - hintMetrics.stringWidth(hint)) / 2, labelY + hintMetrics.getHeight() + 2);
        }
        g.dispose();
    }
}
