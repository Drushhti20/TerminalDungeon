package com.terminaldungeon.mvc.model;

import com.terminaldungeon.builder.Hero;
import com.terminaldungeon.prototype.Enemy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Model
// Holds the current game data: hero, enemies, current encounter, round, battle messages and result
// Battle rules (Member 2) and history (Member 3) change this data; the view only reads it
public class GameModel {
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

    // returns null when every encounter is finished
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

    // checks whether a new attack/heal action is allowed
    // returns an error message, or null when the action can go ahead
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

    // PLACEHOLDER - Member 3: snapshot creation and restore (Memento originator side)
    // createSnapshot() must capture hero and all enemy health, other changing character values,
    // current encounter, round, messages and result; restoreSnapshot(...) puts them back.
}
