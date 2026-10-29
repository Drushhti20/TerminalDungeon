package com.terminaldungeon.mvc.view;

import javax.swing.JComponent;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

// Animated health bar: the fill slides to the new value and lost health leaves a fading red trail
final class HealthBar extends JComponent {
    private int health;
    private int maxHealth = 1;
    private double shown;
    private double trail;
    private boolean empty;
    private final Timer timer = new Timer(16, event -> step());

    HealthBar() {
        setPreferredSize(new Dimension(280, 24));
        setOpaque(false);
    }

    // an empty bar with no numbers, used when there is no enemy left
    void showEmpty() {
        empty = true;
        timer.stop();
        repaint();
    }

    void setHealth(int health, int maxHealth, boolean animate) {
        empty = false;
        this.health = health;
        this.maxHealth = Math.max(1, maxHealth);
        if (!animate) {
            shown = health;
            trail = health;
            repaint();
            return;
        }
        timer.start();
    }

    private void step() {
        shown += (health - shown) * 0.18;
        if (Math.abs(health - shown) < 0.05) {
            shown = health;
        }
        trail = trail > shown ? Math.max(shown, trail - maxHealth * 0.005) : shown;
        repaint();
        if (shown == health && trail == shown) {
            timer.stop();
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int arc = height;

        g.setColor(Theme.PANEL_DARK);
        g.fillRoundRect(0, 0, width, height, arc, arc);
        if (empty) {
            g.setColor(Theme.PANEL_BORDER);
            g.drawRoundRect(0, 0, width - 1, height - 1, arc, arc);
            g.dispose();
            return;
        }

        int trailWidth = (int) Math.round(width * Math.max(0, trail) / maxHealth);
        int fillWidth = (int) Math.round(width * Math.max(0, shown) / maxHealth);

        if (trailWidth > fillWidth) {
            g.setColor(new Color(255, 120, 110, 170));
            g.fillRoundRect(0, 0, trailWidth, height, arc, arc);
        }
        if (fillWidth > 0) {
            Color color = Theme.healthColor((int) Math.round(shown), maxHealth);
            g.setPaint(new GradientPaint(0, 0, color.brighter(), 0, height, color.darker()));
            g.fillRoundRect(0, 0, fillWidth, height, arc, arc);
            g.setColor(new Color(255, 255, 255, 50));
            g.fillRoundRect(2, 2, Math.max(0, fillWidth - 4), height / 2 - 2, arc, arc);
        }

        g.setColor(Theme.PANEL_BORDER);
        g.drawRoundRect(0, 0, width - 1, height - 1, arc, arc);

        String text = health + " / " + maxHealth + " HP";
        g.setFont(Theme.ui(java.awt.Font.BOLD, 13));
        FontMetrics metrics = g.getFontMetrics();
        int textX = (width - metrics.stringWidth(text)) / 2;
        int textY = (height - metrics.getHeight()) / 2 + metrics.getAscent();
        g.setColor(new Color(0, 0, 0, 150));
        g.drawString(text, textX + 1, textY + 1);
        g.setColor(Color.WHITE);
        g.drawString(text, textX, textY);
        g.dispose();
    }
}
