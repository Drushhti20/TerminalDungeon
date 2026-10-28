package com.terminaldungeon;

import com.terminaldungeon.mediator.BattleCoordinator;
import com.terminaldungeon.memento.GameHistory;
import com.terminaldungeon.mvc.controller.GameController;
import com.terminaldungeon.mvc.model.GameModel;
import com.terminaldungeon.mvc.view.GameView;
import com.terminaldungeon.prototype.EnemyRegistry;

import java.util.List;
import java.util.Scanner;

// PLACEHOLDER - Member 2: startup wiring (game start) belongs to Member 2; replace or adapt this file.
// It only shows how Member 1's parts connect: build the model, prepare the dungeon from the
// enemy templates, wire the view, battle coordinator, history and controller, then run.
public class Main {
    public static void main(String[] args) {
        GameModel model = new GameModel();

        EnemyRegistry registry = new EnemyRegistry();
        model.setEnemies(List.of(
                registry.cloneEnemy("Goblin"),
                registry.cloneEnemy("Goblin"),
                registry.cloneEnemy("Orc")));

        GameView view = new GameView();
        BattleCoordinator battle = new BattleCoordinator(model);
        GameHistory history = new GameHistory(model);
        GameController controller = new GameController(model, view, battle, history, new Scanner(System.in));

        if (controller.setupHero()) {
            controller.run();
        }
    }
}
