package com.terminaldungeon.demo;

import com.terminaldungeon.builder.ConcreteHeroBuilder;
import com.terminaldungeon.builder.Hero;
import com.terminaldungeon.builder.HeroDirector;
import com.terminaldungeon.mediator.BattleCoordinator;
import com.terminaldungeon.memento.GameHistory;
import com.terminaldungeon.mvc.model.GameModel;
import com.terminaldungeon.prototype.Enemy;
import com.terminaldungeon.prototype.EnemyRegistry;
import com.terminaldungeon.strategy.AggressiveStrategy;
import com.terminaldungeon.strategy.CautiousStrategy;

import java.util.List;

// Demo: Strategy and Memento (Member 3)
// Strategy - two Goblins with different behaviour choose differently at low health
// Memento - one undo snapshot, and a checkpoint that loads the same position every time
public class StrategyMementoDemo {
    public static void main(String[] args) {
        HeroDirector director = new HeroDirector();
        ConcreteHeroBuilder builder = new ConcreteHeroBuilder();
        director.buildBalanced(builder, "Aria");
        Hero hero = builder.getResult();

        EnemyRegistry registry = new EnemyRegistry();

        System.out.println("===== Strategy: same enemy type, different behaviour =====");
        Enemy aggressiveGoblin = registry.cloneEnemy("Goblin");
        Enemy cautiousGoblin = registry.cloneEnemy("Goblin");
        aggressiveGoblin.setName("Aggressive Goblin");
        cautiousGoblin.setName("Cautious Goblin");
        aggressiveGoblin.setStrategy(new AggressiveStrategy());
        cautiousGoblin.setStrategy(new CautiousStrategy());
        // both carry the same potions, so any difference comes from the strategy alone
        aggressiveGoblin.setHealPotions(1);
        cautiousGoblin.setHealPotions(1);

        System.out.println("At full health:");
        showChoice(aggressiveGoblin, hero);
        showChoice(cautiousGoblin, hero);

        aggressiveGoblin.setHealth(10);
        cautiousGoblin.setHealth(10);
        System.out.println("At low health (10 HP), 1 potion each:");
        showChoice(aggressiveGoblin, hero);
        showChoice(cautiousGoblin, hero);

        cautiousGoblin.setHealPotions(0);
        System.out.println("Cautious Goblin out of potions:");
        showChoice(cautiousGoblin, hero);

        System.out.println("\n===== Memento: undo the last round =====");
        GameModel model = new GameModel();
        model.setHero(hero);
        hero.setHealth(hero.getMaxHealth());
        model.setEnemies(List.of(registry.cloneEnemy("Goblin"), registry.cloneEnemy("Orc")));
        BattleCoordinator coordinator = new BattleCoordinator(model);
        GameHistory history = new GameHistory(model);

        showState("Start", model);
        playRound(model, coordinator, history);
        showState("After 1 round", model);
        showHistoryResult("Undo", history.undo(), model);
        showHistoryResult("Second undo", history.undo(), model);

        System.out.println("\n===== Memento: save and load a checkpoint =====");
        showHistoryResult("Save", history.saveCheckpoint(), model);
        playRound(model, coordinator, history);
        playRound(model, coordinator, history);
        showState("After 2 more rounds", model);
        showHistoryResult("Load", history.loadCheckpoint(), model);
        playRound(model, coordinator, history);
        showState("After 1 more round", model);
        showHistoryResult("Load again", history.loadCheckpoint(), model);
        showHistoryResult("Undo after load", history.undo(), model);
    }

    private static void showChoice(Enemy enemy, Hero hero) {
        System.out.println("  " + enemy.getName() + " (HP " + enemy.getHealth() + "/" + enemy.getMaxHealth()
                + ") chooses: " + enemy.getStrategy().chooseAction(enemy, hero));
    }

    // same order as the controller: history keeps a snapshot, then the coordinator runs the round
    private static void playRound(GameModel model, BattleCoordinator coordinator, GameHistory history) {
        history.recordRound();
        model.clearMessages();
        coordinator.playRound("attack");
    }

    private static void showHistoryResult(String label, String errorMessage, GameModel model) {
        if (errorMessage == null) {
            showState(label, model);
        } else {
            System.out.println("  " + label + ": " + errorMessage);
        }
    }

    private static void showState(String label, GameModel model) {
        Enemy enemy = model.getCurrentEnemy();
        System.out.println("  " + label + ": round " + model.getRound()
                + " | hero HP " + model.getHero().getHealth()
                + " | " + (enemy == null ? "no enemy" : enemy.getName() + " HP " + enemy.getHealth())
                + " | " + model.getResult());
    }
}
