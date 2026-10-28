package com.terminaldungeon.builder;

// Builder interface
// Common steps used to assemble every hero, whatever the equipment preset
public interface HeroBuilder {
    void reset();

    void setName(String name);

    void setHealth(int health);

    void setAttack(int attack);

    void setDefence(int defence);
}
