package com.terminaldungeon.builder;

// Concrete Builder
// Applies each step to the hero being assembled
public class ConcreteHeroBuilder implements HeroBuilder {
    private Hero hero;

    public ConcreteHeroBuilder() {
        reset();
    }

    @Override
    public void reset() {
        hero = new Hero();
    }

    @Override
    public void setName(String name) {
        hero.setName(name);
    }

    // starting health is also the maximum health
    @Override
    public void setHealth(int health) {
        hero.setMaxHealth(health);
        hero.setHealth(health);
    }

    @Override
    public void setAttack(int attack) {
        hero.setAttack(attack);
    }

    @Override
    public void setDefence(int defence) {
        hero.setDefence(defence);
    }

    // call getResult in last, when the whole hero is constructed
    public Hero getResult() {
        Hero result = hero;
        reset();
        return result;
    }
}
