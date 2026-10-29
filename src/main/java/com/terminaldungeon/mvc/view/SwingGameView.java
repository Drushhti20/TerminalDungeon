package com.terminaldungeon.mvc.view;

import com.terminaldungeon.builder.Hero;
import com.terminaldungeon.mvc.model.GameModel;
import com.terminaldungeon.mvc.model.GameResult;
import com.terminaldungeon.prototype.Enemy;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// View (Swing)
// Window version of the game screen. Like GameView it only shows data and reads input,
// so the model, battle coordinator and history work unchanged behind it.
// It remembers the last health values it drew only to play hit/heal animations.
public class SwingGameView extends JFrame {
    private static final String SETUP_SCREEN = "setup";
    private static final String BATTLE_SCREEN = "battle";
    // the enemy's response starts after the hero's lunge has finished
    private static final long ENEMY_RESPONSE_DELAY_MS = 550;
    private static final Pattern AMOUNT = Pattern.compile(" for (\\d+) ");

    private final CardLayout screens = new CardLayout();
    private final JPanel screenPanel = new JPanel(screens);

    // setup screen
    private final JTextField nameField = new JTextField(16);
    private final PresetCard balancedCard = new PresetCard("Balanced", "Steady attack,\nsolid defence");
    private final PresetCard strongAttackCard = new PresetCard("Strong attack", "Hits hard,\nguards less");
    private final GameButton startButton = new GameButton("ENTER THE DUNGEON", null, new Color(176, 120, 30));
    private final JLabel setupErrorLabel = label(" ", Theme.ui(Font.BOLD, 14), Theme.DANGER);
    private final JPanel lineupPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));

    // battle screen
    private final ArenaPanel arena = new ArenaPanel();
    private final JLabel heroNameLabel = label(" ", Theme.ui(Font.BOLD, 20), Theme.GOLD);
    private final JLabel heroStatsLabel = label(" ", Theme.ui(Font.PLAIN, 14), Theme.MUTED);
    private final HealthBar heroHealthBar = new HealthBar();
    private final JLabel enemyNameLabel = label(" ", Theme.ui(Font.BOLD, 20), new Color(240, 130, 120));
    private final JLabel enemyStatsLabel = label(" ", Theme.ui(Font.PLAIN, 14), Theme.MUTED);
    private final HealthBar enemyHealthBar = new HealthBar();
    private final JLabel roundLabel = label(" ", Theme.ui(Font.BOLD, 16), Theme.TEXT);
    private final JTextPane logPane = new JTextPane();
    private final JLabel errorLabel = label(" ", Theme.ui(Font.BOLD, 14), Theme.DANGER);
    private final GameButton attackButton = new GameButton("Attack", "A", new Color(178, 52, 48));
    private final GameButton healButton = new GameButton("Heal", "H", new Color(52, 140, 72));
    private final GameButton saveButton = new GameButton("Save", "S", Theme.SLATE);
    private final GameButton loadButton = new GameButton("Load", "L", Theme.SLATE);
    private final GameButton undoButton = new GameButton("Undo", "U", Theme.SLATE);
    private final GameButton helpButton = new GameButton("Help", "F1", new Color(60, 56, 78));
    private final GameButton soundButton = new GameButton("Sound: ON", null, new Color(60, 56, 78));
    private final SoundEffects sounds = new SoundEffects();

    // last values drawn, used to decide which animation and sound to play after a round
    private GameResult lastResult = GameResult.IN_PROGRESS;
    private boolean hasStatus;
    private int lastEncounter;
    private String[] lastEnemySprite;

    public SwingGameView() {
        super("Terminal Dungeon");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        screenPanel.add(buildSetupScreen(), SETUP_SCREEN);
        screenPanel.add(buildBattleScreen(), BATTLE_SCREEN);
        screenPanel.setPreferredSize(new Dimension(1000, 800));
        setContentPane(screenPanel);
        setMinimumSize(new Dimension(900, 760));

        pack();
        setLocationRelativeTo(null);
    }

    // ---------- setup screen ----------

    private JPanel buildSetupScreen() {
        JPanel background = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics;
                g.setPaint(new GradientPaint(0, 0, new Color(34, 28, 46), 0, getHeight(), Theme.BACKGROUND));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        JPanel card = roundedPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(32, 48, 32, 48));

        JLabel title = label("TERMINAL DUNGEON", Theme.title(46), Theme.GOLD);
        JLabel subtitle = label("Defeat every enemy to win. Lose all your health and you lose.",
                Theme.ui(Font.PLAIN, 15), Theme.MUTED);

        lineupPanel.setOpaque(false);

        nameField.setFont(Theme.ui(Font.BOLD, 18));
        nameField.setBackground(Theme.PANEL_DARK);
        nameField.setForeground(Theme.TEXT);
        nameField.setCaretColor(Theme.GOLD);
        nameField.setHorizontalAlignment(SwingConstants.CENTER);
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.PANEL_BORDER, 2),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        nameField.setMaximumSize(new Dimension(360, 46));

        ButtonGroup presets = new ButtonGroup();
        presets.add(balancedCard);
        presets.add(strongAttackCard);
        balancedCard.setSelected(true);
        JPanel presetRow = new JPanel(new GridLayout(1, 2, 16, 0));
        presetRow.setOpaque(false);
        presetRow.add(balancedCard);
        presetRow.add(strongAttackCard);
        presetRow.setMaximumSize(new Dimension(480, 120));

        startButton.setMaximumSize(new Dimension(300, 52));

        addCentered(card, title, 6);
        addCentered(card, subtitle, 24);
        addCentered(card, lineupPanel, 26);
        addCentered(card, label("Name your hero", Theme.ui(Font.BOLD, 14), Theme.TEXT), 8);
        addCentered(card, nameField, 22);
        addCentered(card, label("Choose your gear", Theme.ui(Font.BOLD, 14), Theme.TEXT), 8);
        addCentered(card, presetRow, 26);
        addCentered(card, startButton, 10);
        addCentered(card, setupErrorLabel, 0);

        background.add(card);
        return background;
    }

    private static void addCentered(JPanel panel, JComponent component, int gapAfter) {
        component.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(component);
        panel.add(Box.createVerticalStrut(gapAfter));
    }

    // shows the hero against the enemies waiting in the dungeon
    public void showDungeonPreview(GameModel model) {
        lineupPanel.removeAll();
        lineupPanel.add(spriteWithCaption(SpriteArt.HERO, "You", Theme.GOLD));
        lineupPanel.add(label("vs", Theme.title(24), Theme.MUTED));
        for (Enemy enemy : model.getEnemies()) {
            lineupPanel.add(spriteWithCaption(SpriteArt.forEnemy(enemy), enemy.getName(), new Color(240, 130, 120)));
        }
        lineupPanel.revalidate();
        lineupPanel.repaint();
    }

    private static JLabel spriteWithCaption(String[] sprite, String caption, Color color) {
        JLabel label = label(caption, Theme.ui(Font.BOLD, 12), color);
        label.setIcon(SpriteArt.icon(sprite, SpriteArt.pixelSizeFor(sprite, 4)));
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        label.setVerticalTextPosition(SwingConstants.BOTTOM);
        return label;
    }

    public String getHeroName() {
        return nameField.getText();
    }

    // "1" for Balanced, "2" for Strong attack, the same choices as the terminal menu
    public String getSelectedPreset() {
        return balancedCard.isSelected() ? "1" : "2";
    }

    public void showSetupError(String message) {
        setupErrorLabel.setText(message);
        sounds.play(SoundEffects.Sound.ERROR, 0);
    }

    // ---------- battle screen ----------

    private JPanel buildBattleScreen() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(Theme.BACKGROUND);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.PANEL_DARK);
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        header.add(label("TERMINAL DUNGEON", Theme.title(20), Theme.GOLD), BorderLayout.WEST);
        // sound on/off is a display preference, so the view handles it itself
        soundButton.setPreferredSize(new Dimension(130, 34));
        soundButton.addActionListener(event -> {
            sounds.toggle();
            soundButton.setText(sounds.isEnabled() ? "Sound: ON" : "Sound: OFF");
            sounds.play(SoundEffects.Sound.CLICK, 0);
        });
        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        headerRight.setOpaque(false);
        headerRight.add(roundLabel);
        headerRight.add(soundButton);
        header.add(headerRight, BorderLayout.EAST);

        JPanel fighters = new JPanel(new GridLayout(1, 2, 20, 0));
        fighters.setOpaque(false);
        fighters.setBorder(BorderFactory.createEmptyBorder(14, 20, 6, 20));
        fighters.add(fighterCard(heroNameLabel, heroHealthBar, heroStatsLabel));
        fighters.add(fighterCard(enemyNameLabel, enemyHealthBar, enemyStatsLabel));

        JPanel middle = new JPanel(new BorderLayout());
        middle.setOpaque(false);
        middle.add(arena, BorderLayout.CENTER);
        middle.add(fighters, BorderLayout.SOUTH);

        logPane.setEditable(false);
        logPane.setBackground(Theme.PANEL_DARK);
        logPane.setFont(Theme.mono(14));
        logPane.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JScrollPane logScroll = new JScrollPane(logPane);
        logScroll.setBorder(BorderFactory.createLineBorder(Theme.PANEL_BORDER));
        logScroll.getViewport().setBackground(Theme.PANEL_DARK);
        logScroll.setPreferredSize(new Dimension(900, 150));

        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel buttons = new JPanel(new GridLayout(1, 6, 10, 0));
        buttons.setOpaque(false);
        for (JButton button : new JButton[] {attackButton, healButton, saveButton, loadButton, undoButton, helpButton}) {
            buttons.add(button);
        }

        JPanel controls = new JPanel(new BorderLayout(0, 6));
        controls.setOpaque(false);
        controls.add(errorLabel, BorderLayout.NORTH);
        controls.add(buttons, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(0, 8));
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(6, 20, 16, 20));
        bottom.add(logScroll, BorderLayout.CENTER);
        bottom.add(controls, BorderLayout.SOUTH);

        panel.add(header, BorderLayout.NORTH);
        panel.add(middle, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        bindKey(panel, KeyEvent.VK_A, attackButton);
        bindKey(panel, KeyEvent.VK_H, healButton);
        bindKey(panel, KeyEvent.VK_S, saveButton);
        bindKey(panel, KeyEvent.VK_L, loadButton);
        bindKey(panel, KeyEvent.VK_U, undoButton);
        bindKey(panel, KeyEvent.VK_F1, helpButton);
        bindKey(panel, KeyEvent.VK_M, soundButton);
        return panel;
    }

    private JPanel fighterCard(JLabel nameLabel, HealthBar healthBar, JLabel statsLabel) {
        JPanel card = roundedPanel();
        card.setLayout(new BorderLayout(0, 6));
        card.setBorder(BorderFactory.createEmptyBorder(10, 16, 12, 16));
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(nameLabel, BorderLayout.WEST);
        top.add(statsLabel, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);
        card.add(healthBar, BorderLayout.CENTER);
        return card;
    }

    // keyboard shortcut: works while focus is anywhere on the battle screen
    private static void bindKey(JComponent panel, int keyCode, JButton button) {
        String name = "press-" + keyCode;
        panel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(keyCode, 0), name);
        panel.getActionMap().put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (button.isEnabled()) {
                    button.doClick();
                }
            }
        });
    }

    public void showBattleScreen() {
        screens.show(screenPanel, BATTLE_SCREEN);
        attackButton.requestFocusInWindow();
    }

    public void showStatus(GameModel model) {
        Hero hero = model.getHero();
        if (hero != null) {
            heroNameLabel.setText(hero.getName());
            heroStatsLabel.setText("ATK " + hero.getAttack() + "   DEF " + hero.getDefence());
            heroHealthBar.setHealth(hero.getHealth(), hero.getMaxHealth(), hasStatus);
        }

        Enemy enemy = model.getCurrentEnemy();
        boolean sameEnemy = hasStatus && model.getCurrentEncounter() == lastEncounter;
        if (enemy != null) {
            String[] sprite = SpriteArt.forEnemy(enemy);
            enemyNameLabel.setText(enemy.getName());
            enemyStatsLabel.setText("ATK " + enemy.getAttack() + "   DEF " + enemy.getDefence());
            enemyHealthBar.setHealth(enemy.getHealth(), enemy.getMaxHealth(), sameEnemy);
            arena.setEnemy(sprite);
            lastEnemySprite = sprite;
            roundLabel.setText("Round " + model.getRound() + "   |   Encounter "
                    + (model.getCurrentEncounter() + 1) + " of " + model.getEnemies().size());
        } else {
            enemyNameLabel.setText("Dungeon cleared");
            enemyStatsLabel.setText(" ");
            enemyHealthBar.showEmpty();
            arena.setEnemy(null);
            roundLabel.setText("Round " + model.getRound() + "   |   All encounters cleared");
        }

        arena.setEncounters(model.getEnemies().size(), model.getCurrentEncounter());
        lastEncounter = model.getCurrentEncounter();
        hasStatus = true;
    }

    // writes the round to the log and plays its animations and sounds.
    // The player acts first, so the hero's move plays at once and the enemy's response follows.
    public void showRoundMessages(GameModel model) {
        Hero hero = model.getHero();
        appendLog("-- Round " + model.getRound() + " --", Theme.MUTED, false);
        for (String message : model.getMessages()) {
            appendLog(message, colorFor(message, hero.getName()), isEvent(message));
        }

        if (!hasStatus) {
            return;
        }

        // each battle message describes one action; the hero's comes first, the enemy's response after
        String heroPrefix = hero.getName() + " ";
        for (String message : model.getMessages()) {
            int amount = amountIn(message);
            if (amount < 0) {
                continue;
            }
            boolean byHero = message.startsWith(heroPrefix);
            long delay = byHero ? 0 : ENEMY_RESPONSE_DELAY_MS;
            if (message.contains(" attacks ")) {
                playAttack(byHero, amount, delay);
            } else if (message.contains(" heals ")) {
                playHeal(byHero, amount, delay);
            }
        }

        if (model.getCurrentEncounter() != lastEncounter) {
            arena.defeatEnemy(lastEnemySprite, 300);
            sounds.play(SoundEffects.Sound.ENEMY_DOWN, 300);
        }
    }

    // the number in "... for 12 damage." or "... heals for 6 HP.", or -1 when there is none
    private static int amountIn(String message) {
        Matcher matcher = AMOUNT.matcher(message);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : -1;
    }

    private void playAttack(boolean heroAttacks, int damage, long delayMs) {
        arena.attack(heroAttacks, damage, delayMs);
        sounds.play(SoundEffects.Sound.SWING, delayMs);
        sounds.play(SoundEffects.Sound.HIT, delayMs + ArenaPanel.IMPACT_MS);
    }

    private void playHeal(boolean onHero, int amount, long delayMs) {
        arena.heal(onHero, amount, delayMs);
        sounds.play(SoundEffects.Sound.HEAL, delayMs);
    }

    private static Color colorFor(String message, String heroName) {
        if (isEvent(message)) {
            return Theme.EVENT;
        }
        if (message.contains(" heals ")) {
            return Theme.HEAL;
        }
        if (message.contains(" HP: ")) {
            return Theme.MUTED;
        }
        if (message.startsWith(heroName + " ")) {
            return Theme.GOLD;
        }
        if (message.contains(" attacks ")) {
            return new Color(240, 120, 110);
        }
        return Theme.TEXT;
    }

    private static boolean isEvent(String message) {
        return message.contains("defeated") || message.contains("Congratulations") || message.contains("approaches");
    }

    public void showMessages(List<String> messages) {
        for (String message : messages) {
            showMessage(message);
        }
    }

    public void showMessage(String message) {
        appendLog(message, Theme.SYSTEM, false);
        sounds.play(SoundEffects.Sound.CLICK, 0);
    }

    private void appendLog(String message, Color color, boolean bold) {
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, color);
        StyleConstants.setBold(style, bold);
        StyledDocument document = logPane.getStyledDocument();
        try {
            document.insertString(document.getLength(), message + "\n", style);
        } catch (BadLocationException e) {
            throw new IllegalStateException(e);
        }
        logPane.setCaretPosition(document.getLength());
    }

    public void showError(String message) {
        errorLabel.setText(message);
        sounds.play(SoundEffects.Sound.ERROR, 0);
    }

    public void clearError() {
        errorLabel.setText(" ");
    }

    // the fanfare or defeat tune plays once, when the battle ends, after that round's animations
    public void showResult(GameResult result) {
        boolean justEnded = lastResult == GameResult.IN_PROGRESS && result != GameResult.IN_PROGRESS;
        if (result == GameResult.VICTORY) {
            arena.setBanner("VICTORY", "The dungeon is cleared!  Undo or Load to play on.", Theme.GOLD);
            if (justEnded) {
                sounds.play(SoundEffects.Sound.VICTORY, 700);
            }
        } else if (result == GameResult.DEFEAT) {
            arena.setBanner("DEFEAT", "Your hero has fallen.  Undo or Load to try again.", Theme.DANGER);
            if (justEnded) {
                sounds.play(SoundEffects.Sound.DEFEAT, ENEMY_RESPONSE_DELAY_MS + 400);
            }
        } else {
            arena.clearBanner();
        }
        lastResult = result;
    }

    // attack and heal are blocked after a win or loss; save, load, undo and help stay available
    public void setBattleActionsEnabled(boolean enabled) {
        attackButton.setEnabled(enabled);
        healButton.setEnabled(enabled);
    }

    public void showHelp() {
        JOptionPane.showMessageDialog(this,
                "Attack (A) - attack the current enemy\n"
                        + "Heal (H) - restore some of your health\n"
                        + "Save (S) - save a checkpoint\n"
                        + "Load (L) - load the saved checkpoint\n"
                        + "Undo (U) - undo the last round\n"
                        + "Help (F1) - show this help\n\n"
                        + "Only Attack and Heal use a turn; the enemy then responds.\n"
                        + "After a win or loss you can still undo, load or close the window.",
                "How to play", JOptionPane.INFORMATION_MESSAGE);
    }

    // ---------- listeners (the controller registers these) ----------

    // pressing Enter in the name field also starts the game
    public void addStartButtonListener(ActionListener listener) {
        startButton.addActionListener(listener);
        nameField.addActionListener(listener);
    }

    public void addAttackButtonListener(ActionListener listener) {
        attackButton.addActionListener(listener);
    }

    public void addHealButtonListener(ActionListener listener) {
        healButton.addActionListener(listener);
    }

    public void addSaveButtonListener(ActionListener listener) {
        saveButton.addActionListener(listener);
    }

    public void addLoadButtonListener(ActionListener listener) {
        loadButton.addActionListener(listener);
    }

    public void addUndoButtonListener(ActionListener listener) {
        undoButton.addActionListener(listener);
    }

    public void addHelpButtonListener(ActionListener listener) {
        helpButton.addActionListener(listener);
    }

    // ---------- small helpers ----------

    private static JLabel label(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    private static JPanel roundedPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(Theme.PANEL);
                g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g.setColor(Theme.PANEL_BORDER);
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g.dispose();
            }
        };
        panel.setOpaque(false);
        return panel;
    }
}
