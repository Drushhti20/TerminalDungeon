package com.terminaldungeon.builder;

// Product
// Hero assembled step by step by the HeroBuilder
public class Hero {
    private String name;
    private int maxHealth;
    private int health;
    private int attack;
    private int defence;

    // ~ package private, so only the builder can create and configure a hero
    Hero() {
    }

    void setName(String name) {
        this.name = name;
    }

    void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    void setAttack(int attack) {
        this.attack = attack;
    }

    void setDefence(int defence) {
        this.defence = defence;
    }

    // public because health changes during battle (Member 2) and on undo/load (Member 3)
    public void setHealth(int health) {
        this.health = health;
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
