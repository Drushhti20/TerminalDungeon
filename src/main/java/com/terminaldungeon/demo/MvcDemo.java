package com.terminaldungeon.demo;

import com.terminaldungeon.mediator.BattleCoordinator;
import com.terminaldungeon.memento.GameHistory;
import com.terminaldungeon.mvc.controller.GameController;
import com.terminaldungeon.mvc.model.GameModel;
import com.terminaldungeon.mvc.view.GameView;
import com.terminaldungeon.prototype.EnemyRegistry;

import java.util.List;
import java.util.Scanner;

// Demo: MVC (Member 1)
// Plays a scripted session through the real controller:
// Controller checks each input line and routes it, Model holds the result, View only prints it.
// Includes invalid input, which is explained without costing a turn.
public class MvcDemo {
    private static final String SCRIPTED_INPUT = String.join("\n",
            "",          // empty name - rejected
            "Aria",
            "3",         // invalid preset - rejected
            "1",
            "status",    // no turn used
            "fly",       // unknown command - no turn used
            "attack",    // routed to the battle coordinator
            "undo",      // routed to history
            "quit") + "\n";

    public static void main(String[] args) {
        GameModel model = new GameModel();
        EnemyRegistry registry = new EnemyRegistry();
        model.setEnemies(List.of(registry.cloneEnemy("Goblin"), registry.cloneEnemy("Orc")));

        GameView view = new GameView();
        BattleCoordinator battle = new BattleCoordinator(model);
        GameHistory history = new GameHistory(model);
        GameController controller = new GameController(model, view, battle, history, new Scanner(SCRIPTED_INPUT));

        System.out.println("Scripted input: " + SCRIPTED_INPUT.replace("\n", " | "));
        if (controller.setupHero()) {
            controller.run();
        }
    }
}
