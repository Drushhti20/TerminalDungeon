package com.terminaldungeon.demo;

import com.terminaldungeon.builder.ConcreteHeroBuilder;
import com.terminaldungeon.builder.Hero;
import com.terminaldungeon.builder.HeroDirector;
import com.terminaldungeon.prototype.Enemy;
import com.terminaldungeon.prototype.EnemyRegistry;

// Demo: Builder and Prototype (Member 1)
// Builder - two differently configured heroes from the same building process
// Prototype - two copies of one template; damaging one leaves the other and the template unchanged
public class BuilderPrototypeDemo {
    public static void main(String[] args) {
        System.out.println("===== Builder: same process, two presets =====");
        HeroDirector director = new HeroDirector();
        ConcreteHeroBuilder builder = new ConcreteHeroBuilder();

        director.buildBalanced(builder, "Aria");
        Hero balanced = builder.getResult();

        director.buildStrongAttack(builder, "Brak");
        Hero strongAttack = builder.getResult();

        System.out.println("Balanced      -> " + describe(balanced));
        System.out.println("Strong attack -> " + describe(strongAttack));

        System.out.println("\n===== Prototype: independent copies of one template =====");
        EnemyRegistry registry = new EnemyRegistry();
        Enemy firstGoblin = registry.cloneEnemy("Goblin");
        Enemy secondGoblin = registry.cloneEnemy("Goblin");
        firstGoblin.setName("Goblin 1");
        secondGoblin.setName("Goblin 2");

        System.out.println("Before damage:");
        System.out.println("  " + describe(firstGoblin));
        System.out.println("  " + describe(secondGoblin));

        firstGoblin.setHealth(firstGoblin.getHealth() - 25);

        System.out.println("After Goblin 1 takes 25 damage:");
        System.out.println("  " + describe(firstGoblin));
        System.out.println("  " + describe(secondGoblin));
        System.out.println("  New copy from the template -> " + describe(registry.cloneEnemy("Goblin")));
        System.out.println("Goblin 1 and Goblin 2 are different objects: " + (firstGoblin != secondGoblin));
    }

    private static String describe(Hero hero) {
        return hero.getName() + " | HP " + hero.getHealth() + "/" + hero.getMaxHealth()
                + " | ATK " + hero.getAttack() + " | DEF " + hero.getDefence();
    }

    private static String describe(Enemy enemy) {
        return enemy.getName() + " | HP " + enemy.getHealth() + "/" + enemy.getMaxHealth();
    }
}
