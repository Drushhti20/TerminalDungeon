package com.terminaldungeon.strategy;

import com.terminaldungeon.builder.Hero;
import com.terminaldungeon.prototype.Enemy;

// Aggressive enemies always attack when combatants are available.
public class AggressiveStrategy implements EnemyStrategy {

    @Override
    public String chooseAction(Enemy enemy, Hero hero) {
        if (enemy == null || hero == null) {
            return null;
        }

        return "attack";
    }
}
