package com.terminaldungeon.strategy;

import com.terminaldungeon.builder.Hero;
import com.terminaldungeon.prototype.Enemy;

// Cautious enemies heal at low health if they are missing health.
public class CautiousStrategy implements EnemyStrategy {

    @Override
    public String chooseAction(Enemy enemy, Hero hero) {
        if (enemy == null || hero == null) {
            return null;
        }

        boolean belowHalfHealth =
                enemy.getHealth() <= enemy.getMaxHealth() / 2;

        boolean missingHealth =
                enemy.getHealth() < enemy.getMaxHealth();

        if (belowHalfHealth && missingHealth) {
            return "heal";
        }

        return "attack";
    }
}
