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

    // Strategy Pattern: determines this enemy's behaviour.
    private EnemyStrategy strategy;

    public Enemy(
            String type,
            int maxHealth,
            int attack,
            int defence) {

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