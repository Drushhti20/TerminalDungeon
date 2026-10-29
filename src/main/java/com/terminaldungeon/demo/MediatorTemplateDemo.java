package com.terminaldungeon.demo;

import com.terminaldungeon.builder.ConcreteHeroBuilder;
import com.terminaldungeon.builder.HeroDirector;
import com.terminaldungeon.mediator.BattleCoordinator;
import com.terminaldungeon.mvc.model.GameModel;
import com.terminaldungeon.prototype.Enemy;
import com.terminaldungeon.prototype.EnemyRegistry;

import java.util.List;

// Demo: Mediator and Template Method (Member 2)
// Every action goes through the BattleCoordinator; characters never act on each other directly.
// Each round runs the fixed turn sequence: the player turn first, then the enemy turn only if the battle is still on.
public class MediatorTemplateDemo {
    public static void main(String[] args) {
        GameModel model = new GameModel();

        HeroDirector director = new HeroDirector();
        ConcreteHeroBuilder builder = new ConcreteHeroBuilder();
        director.buildBalanced(builder, "Aria");
        model.setHero(builder.getResult());

        EnemyRegistry registry = new EnemyRegistry();
        Enemy firstGoblin = registry.cloneEnemy("Goblin");
        Enemy secondGoblin = registry.cloneEnemy("Goblin");
        firstGoblin.setName("Goblin 1");
        secondGoblin.setName("Goblin 2");
        model.setEnemies(List.of(firstGoblin, secondGoblin));

        BattleCoordinator coordinator = new BattleCoordinator(model);

        System.out.println("===== Round 1: hero attacks, enemy responds =====");
        playRound(model, coordinator, "attack");

        System.out.println("\n===== Round 2: hero heals, enemy responds =====");
        playRound(model, coordinator, "heal");

        System.out.println("\n===== Round 3: Goblin 1 is down to 5 HP, so the hero defeats it =====");
        firstGoblin.setHealth(5);
        playRound(model, coordinator, "attack");
        System.out.println("(Goblin 1 did not get a response, and Goblin 2 is the next encounter)");

        System.out.println("\n===== Round 4: the last enemy is defeated =====");
        secondGoblin.setHealth(5);
        playRound(model, coordinator, "attack");
        System.out.println("Result: " + model.getResult());
    }

    private static void playRound(GameModel model, BattleCoordinator coordinator, String action) {
        model.clearMessages();
        coordinator.playRound(action);
        for (String message : model.getMessages()) {
            System.out.println("  " + message);
        }
        System.out.println("  Round counter: " + model.getRound());
    }
}
