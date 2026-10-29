
package com.terminaldungeon;

import com.terminaldungeon.mediator.BattleCoordinator;
import com.terminaldungeon.memento.GameHistory;
import com.terminaldungeon.mvc.controller.GameController;
import com.terminaldungeon.mvc.model.GameModel;
import com.terminaldungeon.mvc.view.GameView;
import com.terminaldungeon.prototype.EnemyRegistry;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // 1. Create the game model.
        GameModel model = new GameModel();

        // 2. Create independent enemies using Prototype.
        EnemyRegistry registry = new EnemyRegistry();

        model.setEnemies(List.of(
                registry.cloneEnemy("Goblin"),
                registry.cloneEnemy("Goblin"),
                registry.cloneEnemy("Skeleton"),
                registry.cloneEnemy("Orc")
        ));

        // 3. Create the terminal view.
        GameView view = new GameView();

        // 4. Create the Mediator.
        BattleCoordinator battle = new BattleCoordinator(model);

        // 5. Connect Member 3's history component.
        GameHistory history = new GameHistory(model);

        // 6. Connect the MVC controller.
        GameController controller = new GameController(
                model,
                view,
                battle,
                history,
                new Scanner(System.in)
        );

        // 7. Set up the hero and start the game.
        if (controller.setupHero()) {
            controller.run();
        }
    }
}
