package com.terminaldungeon.mvc.model;

import com.terminaldungeon.builder.Hero;
import com.terminaldungeon.prototype.Enemy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Model
// Holds the current game data: hero, enemies, current encounter, round, messages and result.
public class GameModel {
    private static final int MAX_NAME_LENGTH = 20;

    private Hero hero;
    private List<Enemy> enemies = new ArrayList<>();
    private int currentEncounter;
    private int round;
    private List<String> messages = new ArrayList<>();
    private GameResult result = GameResult.IN_PROGRESS;

    public Hero getHero() {
        return hero;
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    public List<Enemy> getEnemies() {
        return Collections.unmodifiableList(enemies);
    }

    public void setEnemies(List<Enemy> enemies) {
        this.enemies = new ArrayList<>(enemies);
    }

    // Returns null when every encounter is finished.
    public Enemy getCurrentEnemy() {
        if (currentEncounter < 0 || currentEncounter >= enemies.size()) {
            return null;
        }
        return enemies.get(currentEncounter);
    }

    public int getCurrentEncounter() {
        return currentEncounter;
    }

    public void setCurrentEncounter(int currentEncounter) {
        this.currentEncounter = currentEncounter;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public List<String> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public void addMessage(String message) {
        messages.add(message);
    }

    public void clearMessages() {
        messages.clear();
    }

    public GameResult getResult() {
        return result;
    }

    public void setResult(GameResult result) {
        this.result = result;
    }

    public boolean isGameOver() {
        return result != GameResult.IN_PROGRESS;
    }

    // Checks a hero name typed during setup; shared by the terminal and Swing controllers.
    // Returns an error message, or null when the name is valid.
    public String validateHeroName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Name cannot be empty.";
        }
        if (name.trim().length() > MAX_NAME_LENGTH) {
            return "Name must be " + MAX_NAME_LENGTH + " characters or fewer.";
        }
        return null;
    }

    // Checks whether a new attack/heal action is allowed.
    public String checkCanAct() {
        if (hero == null) {
            return "Create a hero before starting the battle.";
        }
        if (isGameOver()) {
            return "The battle is over. You can still undo, load or quit.";
        }
        if (getCurrentEnemy() == null) {
            return "There is no enemy to fight.";
        }
        return null;
    }

    // Memento: capture the game state needed for undo and checkpoint loading.
    public Snapshot createSnapshot() {
        int[] enemyHealth = new int[enemies.size()];
        int[] enemyPotions = new int[enemies.size()];

        for (int i = 0; i < enemies.size(); i++) {
            enemyHealth[i] = enemies.get(i).getHealth();
            enemyPotions[i] = enemies.get(i).getHealPotions();
        }

        return new Snapshot(
                hero == null ? null : hero.getHealth(),
                enemyHealth,
                enemyPotions,
                currentEncounter,
                round,
                new ArrayList<>(messages),
                result
        );
    }

    // Restore the game to the captured state.
    public void restoreSnapshot(Snapshot snapshot) {
        if (snapshot == null) {
            throw new IllegalArgumentException("Snapshot cannot be null.");
        }

        if (snapshot.enemyHealth.length != enemies.size()) {
            throw new IllegalArgumentException(
                    "Snapshot does not match the current enemy list."
            );
        }

        if (hero != null && snapshot.heroHealth != null) {
            hero.setHealth(snapshot.heroHealth);
        }

        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).setHealth(snapshot.enemyHealth[i]);
            enemies.get(i).setHealPotions(snapshot.enemyPotions[i]);
        }

        currentEncounter = snapshot.currentEncounter;
        round = snapshot.round;
        messages = new ArrayList<>(snapshot.messages);
        result = snapshot.result;
    }

    // Immutable snapshot: GameHistory can store it but cannot change its contents.
    public static final class Snapshot {
        private final Integer heroHealth;
        private final int[] enemyHealth;
        private final int[] enemyPotions;
        private final int currentEncounter;
        private final int round;
        private final List<String> messages;
        private final GameResult result;

        private Snapshot(
                Integer heroHealth,
                int[] enemyHealth,
                int[] enemyPotions,
                int currentEncounter,
                int round,
                List<String> messages,
                GameResult result) {

            this.heroHealth = heroHealth;
            this.enemyHealth = enemyHealth.clone();
            this.enemyPotions = enemyPotions.clone();
            this.currentEncounter = currentEncounter;
            this.round = round;
            this.messages = Collections.unmodifiableList(
                    new ArrayList<>(messages)
            );
            this.result = result;
        }
    }
}