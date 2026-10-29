package com.terminaldungeon.strategy;

import com.terminaldungeon.builder.Hero;
import com.terminaldungeon.prototype.Enemy;

// Strategy interface: defines how an enemy chooses its action.
public interface EnemyStrategy {

    String chooseAction(Enemy enemy, Hero hero);
}
