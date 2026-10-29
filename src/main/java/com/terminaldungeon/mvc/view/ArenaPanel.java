package com.terminaldungeon.mvc.view;

import javax.swing.JComponent;
import javax.swing.Timer;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

// The battle scene: torch-lit dungeon wall, hero on the left, enemy on the right.
// Only draws what the view tells it. Attacks lunge, slash and shake the screen; heals sparkle.
final class ArenaPanel extends JComponent {
    // time from the start of a lunge until the blow lands
    static final long IMPACT_MS = 150;

    private static final int BASE_PIXEL = 9;
    private static final long LUNGE_MS = 320;
    private static final int LUNGE_DISTANCE = 80;
    private static final long FLASH_MS = 380;
    private static final long SLASH_MS = 260;
    private static final long SHAKE_MS = 260;
    private static final long FLOAT_MS = 950;
    private static final long SPARKLE_MS = 900;
    private static final long GHOST_MS = 700;
    private static final long CARD_MS = 3200;

    private String[] enemySprite;
    private String[] ghostSprite;
    private long ghostStart;
    private int encounterTotal;
    private int encounterCurrent;
    private String bannerTitle;
    private String bannerSubtitle;
    private Color bannerColor;
    private String summaryRank;
    private List<String> summaryLines;
    private String cardTitle;
    private String cardText;
    private long cardStart;

    private final Fighter hero = new Fighter();
    private final Fighter enemy = new Fighter();
    private long shakeStart = -1;
    private final List<Slash> slashes = new ArrayList<>();
    private final List<Sparkle> sparkles = new ArrayList<>();
    private final List<FloatingText> floatingTexts = new ArrayList<>();
    private final Random random = new Random();
    private float torchFlicker = 1f;

    // per-fighter animation state
    private static final class Fighter {
        private long lungeStart = -1;
        private Color flashColor;
        private long flashStart;
        private boolean flashShake;
    }

    private static final class Slash {
        private final boolean onHero;
        private final long start;

        private Slash(boolean onHero, long start) {
            this.onHero = onHero;
            this.start = start;
        }
    }

    private static final class Sparkle {
        private final boolean onHero;
        private final long start;
        private final int offsetX;
        private final double speed;
        private final int size;
        private final Color color;

        private Sparkle(boolean onHero, long start, int offsetX, double speed, int size, Color color) {
            this.onHero = onHero;
            this.start = start;
            this.offsetX = offsetX;
            this.speed = speed;
            this.size = size;
            this.color = color;
        }
    }

    private static final class FloatingText {
        private final String text;
        private final Color color;
        private final boolean onHero;
        private final long start;

        private FloatingText(String text, Color color, boolean onHero, long start) {
            this.text = text;
            this.color = color;
            this.onHero = onHero;
            this.start = start;
        }
    }

    ArenaPanel() {
        setPreferredSize(new Dimension(940, 330));
        setOpaque(true);
        // repaints about 30 times a second so torches flicker and effects play
        new Timer(33, event -> {
            torchFlicker = 0.85f + random.nextFloat() * 0.15f;
            repaint();
        }).start();
    }

    void setEnemy(String[] sprite) {
        enemySprite = sprite;
    }

    void setEncounters(int total, int current) {
        encounterTotal = total;
        encounterCurrent = current;
    }

    void setBanner(String title, String subtitle, Color color) {
        bannerTitle = title;
        bannerSubtitle = subtitle;
        bannerColor = color;
    }

    // results shown inside the end banner: a big rank letter and a few lines of numbers
    void setSummary(String rank, List<String> lines) {
        summaryRank = rank;
        summaryLines = lines;
    }

    void clearBanner() {
        bannerTitle = null;
        summaryLines = null;
    }

    // story caption that fades in under the encounter dots when a new enemy steps in
    void showTitleCard(String title, String text, long delayMs) {
        cardTitle = title;
        cardText = text;
        cardStart = now() + delayMs;
    }

    // the attacker lunges forward; when the blow lands the target flashes, a slash sweeps across it
    // and the screen shakes
    void attack(boolean heroAttacks, int damage, long delayMs) {
        long start = now() + delayMs;
        long impact = start + IMPACT_MS;
        boolean targetIsHero = !heroAttacks;
        Fighter attacker = heroAttacks ? hero : enemy;
        Fighter target = heroAttacks ? enemy : hero;

        attacker.lungeStart = start;
        target.flashColor = Theme.DANGER;
        target.flashStart = impact;
        target.flashShake = true;
        slashes.add(new Slash(targetIsHero, impact));
        floatingTexts.add(new FloatingText("-" + damage, Theme.DANGER, targetIsHero, impact));
        shakeStart = impact;
    }

    // green flash and rising sparkles around whoever heals
    void heal(boolean onHero, int amount, long delayMs) {
        long start = now() + delayMs;
        Fighter fighter = onHero ? hero : enemy;
        fighter.flashColor = Theme.HEAL;
        fighter.flashStart = start;
        fighter.flashShake = false;
        floatingTexts.add(new FloatingText("+" + amount, Theme.HEAL, onHero, start));
        for (int i = 0; i < 18; i++) {
            Color color = random.nextBoolean() ? Theme.HEAL : new Color(255, 230, 140);
            sparkles.add(new Sparkle(onHero, start + random.nextInt(250), random.nextInt(110) - 55,
                    0.7 + random.nextDouble() * 0.6, 3 + random.nextInt(3), color));
        }
    }

    // the defeated enemy floats up and fades out while the next one appears
    void defeatEnemy(String[] defeatedSprite, long delayMs) {
        ghostSprite = defeatedSprite;
        ghostStart = now() + delayMs;
    }

    private static long now() {
        return System.currentTimeMillis();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int floorY = (int) (height * 0.72);
        long time = now();

        g.setColor(Theme.BACKGROUND);
        g.fillRect(0, 0, width, height);

        Graphics2D scene = (Graphics2D) g.create();
        long shakeElapsed = time - shakeStart;
        if (shakeStart >= 0 && shakeElapsed >= 0 && shakeElapsed < SHAKE_MS) {
            float fade = 1f - (float) shakeElapsed / SHAKE_MS;
            scene.translate((random.nextInt(9) - 4) * fade, (random.nextInt(7) - 3) * fade);
        }

        paintWall(scene, width, floorY);
        paintTorch(scene, (int) (width * 0.12), 70);
        paintTorch(scene, (int) (width * 0.88), 70);
        paintFloor(scene, width, height, floorY);
        paintEncounterTrack(scene, width);

        int heroX = (int) (width * 0.24);
        int enemyX = (int) (width * 0.76);

        scene.setFont(Theme.title(40));
        scene.setColor(new Color(255, 255, 255, 28));
        drawCentered(scene, "VS", width / 2, floorY - 60);

        paintFighter(scene, SpriteArt.HERO, heroX, floorY, hero, 1, time);
        if (ghostSprite != null) {
            paintGhost(scene, enemyX, floorY, time);
        }
        if (enemySprite != null) {
            paintFighter(scene, enemySprite, enemyX, floorY, enemy, -1, time);
        }

        paintSlashes(scene, heroX, enemyX, floorY, time);
        paintSparkles(scene, heroX, enemyX, floorY, time);
        paintFloatingTexts(scene, heroX, enemyX, floorY, time);
        scene.dispose();

        if (cardTitle != null && bannerTitle == null) {
            paintTitleCard(g, width, time);
        }

        if (bannerTitle != null) {
            paintBanner(g, width, height);
        }
        g.dispose();
    }

    private void paintWall(Graphics2D g, int width, int floorY) {
        g.setPaint(new GradientPaint(0, 0, new Color(30, 26, 40), 0, floorY, new Color(18, 16, 24)));
        g.fillRect(-10, -10, width + 20, floorY + 10);

        g.setColor(new Color(255, 255, 255, 10));
        int brickHeight = 26;
        int brickWidth = 64;
        for (int row = 0; row * brickHeight < floorY; row++) {
            int y = row * brickHeight;
            g.drawLine(0, y, width, y);
            int offset = (row % 2) * brickWidth / 2;
            for (int x = -offset; x < width; x += brickWidth) {
                g.drawLine(x, y, x, Math.min(floorY, y + brickHeight));
            }
        }
    }

    private void paintTorch(Graphics2D g, int x, int y) {
        float radius = 150 * torchFlicker;
        g.setPaint(new RadialGradientPaint(x, y, radius, new float[] {0f, 1f},
                new Color[] {new Color(255, 160, 60, (int) (70 * torchFlicker)), new Color(255, 160, 60, 0)}));
        g.fillOval((int) (x - radius), (int) (y - radius), (int) (radius * 2), (int) (radius * 2));

        g.setColor(new Color(90, 60, 35));
        g.fillRect(x - 4, y + 6, 8, 34);
        int flameHeight = (int) (22 * torchFlicker);
        g.setColor(new Color(255, 120, 30));
        g.fillOval(x - 9, y + 8 - flameHeight, 18, flameHeight + 6);
        g.setColor(new Color(255, 220, 90));
        g.fillOval(x - 5, y + 12 - flameHeight / 2, 10, flameHeight / 2 + 4);
    }

    private void paintFloor(Graphics2D g, int width, int height, int floorY) {
        g.setPaint(new GradientPaint(0, floorY, new Color(46, 40, 56), 0, height, new Color(16, 14, 20)));
        g.fillRect(-10, floorY, width + 20, height - floorY + 10);
        g.setColor(new Color(0, 0, 0, 90));
        g.drawLine(0, floorY, width, floorY);
    }

    private void paintEncounterTrack(Graphics2D g, int width) {
        if (encounterTotal == 0) {
            return;
        }
        int spacing = 34;
        int radius = 9;
        int startX = width / 2 - (encounterTotal - 1) * spacing / 2;
        int y = 26;
        for (int i = 0; i < encounterTotal; i++) {
            int x = startX + i * spacing;
            if (i < encounterCurrent) {
                g.setColor(new Color(90, 85, 105));
                g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
                g.setColor(new Color(40, 36, 50));
                g.drawLine(x - 4, y - 4, x + 4, y + 4);
                g.drawLine(x - 4, y + 4, x + 4, y - 4);
            } else if (i == encounterCurrent) {
                g.setColor(new Color(217, 83, 79, 70));
                g.fillOval(x - radius - 5, y - radius - 5, radius * 2 + 10, radius * 2 + 10);
                g.setColor(Theme.DANGER);
                g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
            } else {
                g.setColor(Theme.MUTED);
                g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
            }
        }
    }

    // facing is 1 for the hero (lunges right) and -1 for the enemy (lunges left)
    private void paintFighter(Graphics2D g, String[] sprite, int centerX, int floorY,
                              Fighter fighter, int facing, long time) {
        int pixel = SpriteArt.pixelSizeFor(sprite, BASE_PIXEL);
        int spriteWidth = SpriteArt.width(sprite, pixel);
        int spriteHeight = SpriteArt.height(sprite, pixel);

        int lunge = 0;
        long lungeElapsed = time - fighter.lungeStart;
        if (fighter.lungeStart >= 0 && lungeElapsed >= 0 && lungeElapsed < LUNGE_MS) {
            lunge = (int) (Math.sin(Math.PI * lungeElapsed / LUNGE_MS) * LUNGE_DISTANCE) * facing;
        }

        int shake = 0;
        Color tint = null;
        float amount = 0f;
        long flashElapsed = time - fighter.flashStart;
        if (fighter.flashColor != null && flashElapsed >= 0 && flashElapsed < FLASH_MS) {
            float fade = 1f - (float) flashElapsed / FLASH_MS;
            tint = fighter.flashColor;
            amount = 0.65f * fade;
            if (fighter.flashShake) {
                shake = (int) (Math.sin(flashElapsed / 18.0) * 8 * fade);
            }
        }

        int x = centerX + lunge + shake;
        g.setColor(new Color(0, 0, 0, 110));
        g.fillOval(x - spriteWidth / 2, floorY - 10, spriteWidth, 22);

        // gentle idle bob so the fighters feel alive
        int bob = (int) (Math.sin(time / 400.0 + centerX) * 2);
        SpriteArt.drawTinted(g, sprite, x - spriteWidth / 2, floorY - spriteHeight + 4 + bob, pixel, tint, amount);
    }

    private void paintGhost(Graphics2D g, int centerX, int floorY, long time) {
        long elapsed = time - ghostStart;
        if (elapsed < 0) {
            return;
        }
        if (elapsed >= GHOST_MS) {
            ghostSprite = null;
            return;
        }
        float progress = (float) elapsed / GHOST_MS;
        int pixel = SpriteArt.pixelSizeFor(ghostSprite, BASE_PIXEL);
        int spriteWidth = SpriteArt.width(ghostSprite, pixel);
        int spriteHeight = SpriteArt.height(ghostSprite, pixel);
        Graphics2D ghost = (Graphics2D) g.create();
        ghost.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f - progress));
        SpriteArt.drawTinted(ghost, ghostSprite, centerX - spriteWidth / 2,
                floorY - spriteHeight + 4 - (int) (progress * 60), pixel, Color.WHITE, 0.6f);
        ghost.dispose();
    }

    // a bright arc and a diagonal cut across the target
    private void paintSlashes(Graphics2D g, int heroX, int enemyX, int floorY, long time) {
        Iterator<Slash> iterator = slashes.iterator();
        while (iterator.hasNext()) {
            Slash slash = iterator.next();
            long elapsed = time - slash.start;
            if (elapsed < 0) {
                continue;
            }
            if (elapsed >= SLASH_MS) {
                iterator.remove();
                continue;
            }
            float progress = (float) elapsed / SLASH_MS;
            int centerX = slash.onHero ? heroX : enemyX;
            int centerY = floorY - 85;
            int alpha = (int) (255 * (1f - progress));
            int sweep = (int) (170 * Math.min(1f, progress * 2.5f));

            Graphics2D stroke = (Graphics2D) g.create();
            stroke.setStroke(new BasicStroke(7f * (1f - progress) + 1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            stroke.setColor(new Color(255, 255, 255, alpha));
            int direction = slash.onHero ? -1 : 1;
            stroke.drawArc(centerX - 70, centerY - 70, 140, 140, slash.onHero ? 40 : 140, sweep * direction);
            stroke.setColor(new Color(255, 230, 160, alpha));
            int reach = (int) (60 * Math.min(1f, progress * 3f));
            stroke.drawLine(centerX - reach, centerY - reach, centerX + reach, centerY + reach);
            stroke.dispose();
        }
    }

    private void paintSparkles(Graphics2D g, int heroX, int enemyX, int floorY, long time) {
        Iterator<Sparkle> iterator = sparkles.iterator();
        while (iterator.hasNext()) {
            Sparkle sparkle = iterator.next();
            long elapsed = time - sparkle.start;
            if (elapsed < 0) {
                continue;
            }
            if (elapsed >= SPARKLE_MS) {
                iterator.remove();
                continue;
            }
            float progress = (float) elapsed / SPARKLE_MS;
            int x = (sparkle.onHero ? heroX : enemyX) + sparkle.offsetX
                    + (int) (Math.sin(progress * 6 + sparkle.offsetX) * 6);
            int y = floorY - 20 - (int) (progress * 150 * sparkle.speed);
            int alpha = (int) (255 * (1f - progress));
            int size = sparkle.size;
            g.setColor(new Color(sparkle.color.getRed(), sparkle.color.getGreen(), sparkle.color.getBlue(), alpha));
            g.fillRect(x - size, y - size / 3, size * 2, Math.max(2, size * 2 / 3));
            g.fillRect(x - size / 3, y - size, Math.max(2, size * 2 / 3), size * 2);
        }
    }

    private void paintFloatingTexts(Graphics2D g, int heroX, int enemyX, int floorY, long time) {
        g.setFont(Theme.ui(Font.BOLD, 26));
        Iterator<FloatingText> iterator = floatingTexts.iterator();
        while (iterator.hasNext()) {
            FloatingText text = iterator.next();
            long elapsed = time - text.start;
            if (elapsed < 0) {
                continue;
            }
            if (elapsed >= FLOAT_MS) {
                iterator.remove();
                continue;
            }
            float progress = (float) elapsed / FLOAT_MS;
            int x = text.onHero ? heroX : enemyX;
            int y = floorY - 170 - (int) (progress * 50);
            int alpha = (int) (255 * (1f - progress * progress));
            g.setColor(new Color(0, 0, 0, alpha / 2));
            drawCentered(g, text.text, x + 2, y + 2);
            g.setColor(new Color(text.color.getRed(), text.color.getGreen(), text.color.getBlue(), alpha));
            drawCentered(g, text.text, x, y);
        }
    }

    private void paintTitleCard(Graphics2D g, int width, long time) {
        long elapsed = time - cardStart;
        if (elapsed < 0) {
            return;
        }
        if (elapsed >= CARD_MS) {
            cardTitle = null;
            return;
        }
        float alpha = Math.min(1f, elapsed / 300f) * Math.min(1f, (CARD_MS - elapsed) / 600f);

        g.setFont(Theme.ui(Font.ITALIC, 15));
        int textWidth = g.getFontMetrics().stringWidth(cardText);
        int boxWidth = Math.max(textWidth, 300) + 48;
        Graphics2D card = (Graphics2D) g.create();
        card.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        card.setColor(new Color(10, 8, 14, 190));
        card.fillRoundRect(width / 2 - boxWidth / 2, 46, boxWidth, 66, 16, 16);
        card.setFont(Theme.title(22));
        card.setColor(Theme.GOLD);
        drawCentered(card, cardTitle, width / 2, 74);
        card.setFont(Theme.ui(Font.ITALIC, 15));
        card.setColor(Theme.TEXT);
        drawCentered(card, cardText, width / 2, 98);
        card.dispose();
    }

    private void paintBanner(Graphics2D g, int width, int height) {
        boolean hasSummary = summaryLines != null;
        int bandHeight = hasSummary ? 250 : 120;
        int bandY = (height - bandHeight) / 2;
        g.setColor(new Color(10, 8, 14, hasSummary ? 225 : 205));
        g.fillRect(0, bandY, width, bandHeight);
        g.setColor(bannerColor);
        g.fillRect(0, bandY, width, 3);
        g.fillRect(0, bandY + bandHeight - 3, width, 3);

        g.setFont(Theme.title(hasSummary ? 40 : 46));
        g.setColor(bannerColor);
        drawCentered(g, bannerTitle, width / 2, bandY + (hasSummary ? 48 : 62));

        if (hasSummary) {
            paintSummary(g, width, bandY);
        }

        g.setFont(Theme.ui(Font.PLAIN, 16));
        g.setColor(Theme.TEXT);
        drawCentered(g, bannerSubtitle, width / 2, bandY + bandHeight - (hasSummary ? 16 : 25));
    }

    private void paintSummary(Graphics2D g, int width, int bandY) {
        int rankX = width / 2 - 190;
        int rankY = bandY + 128;
        g.setColor(new Color(255, 255, 255, 18));
        g.fillOval(rankX - 52, rankY - 52, 104, 104);
        g.setColor(bannerColor);
        g.setStroke(new BasicStroke(3f));
        g.drawOval(rankX - 52, rankY - 52, 104, 104);
        g.setFont(Theme.title(62));
        drawCentered(g, summaryRank, rankX, rankY + 22);
        g.setFont(Theme.ui(Font.BOLD, 11));
        g.setColor(Theme.MUTED);
        drawCentered(g, "RANK", rankX, rankY + 70);

        g.setFont(Theme.mono(15));
        g.setColor(Theme.TEXT);
        int lineY = bandY + 78;
        for (String line : summaryLines) {
            g.drawString(line, width / 2 - 100, lineY);
            lineY += 23;
        }
    }

    private static void drawCentered(Graphics2D g, String text, int centerX, int baselineY) {
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(text, centerX - metrics.stringWidth(text) / 2, baselineY);
    }
}
