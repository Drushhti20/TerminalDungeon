package com.terminaldungeon.mediator;

import com.terminaldungeon.builder.Hero;
import com.terminaldungeon.mvc.model.GameModel;
import com.terminaldungeon.mvc.model.GameResult;
import com.terminaldungeon.prototype.Enemy;
import com.terminaldungeon.template_method.PlayerTurn;
import com.terminaldungeon.template_method.EnemyTurn;

// Mediator: coordinates player actions, enemy actions and round completion.
public class BattleCoordinator {

    private static final int HEAL_AMOUNT = 20;

    private final GameModel model;
    private boolean enemyDefeatedThisRound = false;

    public BattleCoordinator(GameModel model) {
        this.model = model;
    }

    // Entry point called by GameController.
    public void playRound(String action) {
        String error = model.checkCanAct();

        if (error != null) {
            model.addMessage(error);
            return;
        }

        Enemy enemy = model.getCurrentEnemy();

        if (enemy == null) {
            model.setResult(GameResult.VICTORY);
            model.addMessage(
                    "Congratulations! You cleared the dungeon!"
            );
            return;
        }

        if (!"attack".equalsIgnoreCase(action)
                && !"heal".equalsIgnoreCase(action)) {
            model.addMessage("Invalid battle action.");
            return;
        }

        enemyDefeatedThisRound = false;

        // Keep the existing Template Method player-turn flow.
        new PlayerTurn(this, action).executeTurn();
    }

    public void resolvePlayerAction(String action) {
        Hero hero = model.getHero();
        Enemy enemy = model.getCurrentEnemy();

        if (hero == null || enemy == null) {
            return;
        }

        if ("attack".equalsIgnoreCase(action)) {
            int damage = Math.max(
                    1,
                    hero.getAttack() - enemy.getDefence()
            );

            enemy.setHealth(
                    Math.max(0, enemy.getHealth() - damage)
            );

            model.addMessage(
                    hero.getName() + " attacks "
                    + enemy.getName() + " for "
                    + damage + " damage."
            );

            model.addMessage(
                    enemy.getName() + " HP: "
                    + enemy.getHealth() + "/"
                    + enemy.getMaxHealth()
            );

            if (enemy.getHealth() <= 0) {
                model.addMessage(
                        enemy.getName() + " has been defeated!"
                );

                enemyDefeatedThisRound = true;
                advanceEncounter();
            }

        } else if ("heal".equalsIgnoreCase(action)) {
            int oldHealth = hero.getHealth();

            int newHealth = Math.min(
                    hero.getMaxHealth(),
                    oldHealth + HEAL_AMOUNT
            );

            hero.setHealth(newHealth);

            model.addMessage(
                    hero.getName() + " heals for "
                    + (newHealth - oldHealth) + " HP."
            );
        }
    }

    public void finishPlayerTurn() {
        Hero hero = model.getHero();

        if (hero.getHealth() <= 0) {
            finishRound();
            return;
        }

        if (enemyDefeatedThisRound) {
            finishRound();
            return;
        }

        Enemy enemy = model.getCurrentEnemy();

        if (enemy != null
                && model.getResult() == GameResult.IN_PROGRESS) {

            // Strategy chooses the action.
            // EnemyTurn still controls the turn sequence.
            new EnemyTurn(this, () -> {
                if (enemy.getStrategy() == null) {
                    return "attack";
                }

                String selected = enemy.getStrategy()
                        .chooseAction(enemy, model.getHero());

                return selected == null ? "attack" : selected;
            }).executeTurn();

        } else {
            finishRound();
        }
    }

    public void resolveEnemyAction(String action) {
        Hero hero = model.getHero();
        Enemy enemy = model.getCurrentEnemy();

        if (hero == null || enemy == null) {
            return;
        }

        if ("attack".equalsIgnoreCase(action)) {
            int damage = Math.max(
                    1,
                    enemy.getAttack() - hero.getDefence()
            );

            hero.setHealth(
                    Math.max(0, hero.getHealth() - damage)
            );

            model.addMessage(
                    enemy.getName() + " attacks "
                    + hero.getName() + " for "
                    + damage + " damage."
            );

            model.addMessage(
                    hero.getName() + " HP: "
                    + hero.getHealth() + "/"
                    + hero.getMaxHealth()
            );

        } else if ("heal".equalsIgnoreCase(action)) {
            int oldHealth = enemy.getHealth();

            int newHealth = Math.min(
                    enemy.getMaxHealth(),
                    oldHealth + HEAL_AMOUNT
            );

            enemy.setHealth(newHealth);

            model.addMessage(
                    enemy.getName() + " heals for "
                    + (newHealth - oldHealth) + " HP."
            );
        }
    }

    public void finishRound() {
        model.setRound(model.getRound() + 1);

        Hero hero = model.getHero();

        if (hero != null && hero.getHealth() <= 0) {
            model.setResult(GameResult.DEFEAT);

            model.addMessage(
                    hero.getName() + " has been defeated!"
            );

            return;
        }

        if (model.getCurrentEnemy() == null) {
            model.setResult(GameResult.VICTORY);

            model.addMessage(
                    "Congratulations! You cleared the dungeon!"
            );
        }
    }

    private void advanceEncounter() {
        model.setCurrentEncounter(
                model.getCurrentEncounter() + 1
        );

        Enemy nextEnemy = model.getCurrentEnemy();

        if (nextEnemy != null) {
            model.addMessage(
                    "The next enemy approaches: "
                    + nextEnemy.getName()
            );
        }
    }
}