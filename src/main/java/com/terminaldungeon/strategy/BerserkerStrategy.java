package com.terminaldungeon.strategy;

import com.terminaldungeon.builder.Hero;
import com.terminaldungeon.prototype.Enemy;

// Berserker enemies never heal; at half health or below they fly into a fury and hit harder.
public class BerserkerStrategy implements EnemyStrategy {

    @Override
    public String chooseAction(Enemy enemy, Hero hero) {
        if (enemy == null || hero == null) {
            return null;
        }

        boolean belowHalfHealth =
                enemy.getHealth() <= enemy.getMaxHealth() / 2;

        if (belowHalfHealth) {
            return "fury";
        }

        return "attack";
    }
}
