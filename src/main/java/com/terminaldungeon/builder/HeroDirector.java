package com.terminaldungeon.builder;

// Director
// Decides which steps are called and in which order for each equipment preset
// Stat values are placeholders, to be agreed with Member 2's damage and healing rules
public class HeroDirector {

    // moderate attack and defence
    public void buildBalanced(HeroBuilder builder, String name) {
        builder.reset();
        builder.setName(name);
        builder.setHealth(100);
        builder.setAttack(15);
        builder.setDefence(10);
    }

    // trades some defence for damage
    public void buildStrongAttack(HeroBuilder builder, String name) {
        builder.reset();
        builder.setName(name);
        builder.setHealth(100);
        builder.setAttack(22);
        builder.setDefence(4);
    }
}
