package com.terminaldungeon.prototype;

import com.terminaldungeon.strategy.AggressiveStrategy;
import com.terminaldungeon.strategy.CautiousStrategy;
import com.terminaldungeon.strategy.EnemyStrategy;

// Concrete Prototype: every encounter has independent enemy health and stats.
public class Enemy implements EnemyPrototype, Cloneable {
    private String type;
    private String name;
    private int maxHealth;
    private int health;
    private int attack;
    private int defence;

    // Healing potions left; each enemy heal uses one, so an enemy cannot heal forever.
    private int healPotions;

    // Strategy Pattern: determines this enemy's behaviour.
    private EnemyStrategy strategy;

    public Enemy(
            String type,
            int maxHealth,
            int attack,
            int defence) {
        this(type, maxHealth, attack, defence, 0);
    }

    public Enemy(
            String type,
            int maxHealth,
            int attack,
            int defence,
            int healPotions) {

        this.healPotions = healPotions;
        this.type = type;
        this.name = type;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.attack = attack;
        this.defence = defence;

        // Keep the existing enemy setup and assign a default strategy.
        if ("Orc".equalsIgnoreCase(type)) {
            this.strategy = new CautiousStrategy();
        } else {
            this.strategy = new AggressiveStrategy();
        }
    }

    @Override
    public Enemy clone() {
        try {
            Enemy copy = (Enemy) super.clone();

            // Current strategies have no mutable instance state.
            // If mutable state is added later, copy that state here.
            // healPotions is a primitive, so each copy already has its own count.

            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(
                    "Enemy implements Cloneable", e
            );
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    // public because a heal uses a potion (Member 2) and undo/load restore the count (Member 3)
    public void setHealPotions(int healPotions) {
        this.healPotions = healPotions;
    }

    public int getHealPotions() {
        return healPotions;
    }

    public void setStrategy(EnemyStrategy strategy) {
        this.strategy = strategy;
    }

    public EnemyStrategy getStrategy() {
        return strategy;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getHealth() {
        return health;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefence() {
        return defence;
    }
}