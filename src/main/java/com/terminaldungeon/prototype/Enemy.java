package com.terminaldungeon.prototype;

// Concrete Prototype
// Goblin and Orc templates are Enemy objects; each encounter gets its own copy
public class Enemy implements EnemyPrototype, Cloneable {
    private String type;
    private String name;
    private int maxHealth;
    private int health;
    private int attack;
    private int defence;

    // PLACEHOLDER - Member 3: enemy behaviour (aggressive / cautious strategy)
    // add the strategy field with a getter and setter here, and if the strategy
    // has changing internal state, give each copy its own strategy in clone()

    public Enemy(String type, int maxHealth, int attack, int defence) {
        this.type = type;
        this.name = type;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.attack = attack;
        this.defence = defence;
    }

    // calls Object.clone() using super.clone()
    // every field is a primitive or an immutable String, so the copy shares no changing state
    @Override
    public Enemy clone() {
        try {
            return (Enemy) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Enemy implements Cloneable", e);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    // public because health changes during battle (Member 2) and on undo/load (Member 3)
    public void setHealth(int health) {
        this.health = health;
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
