package com.terminaldungeon;

import com.terminaldungeon.mediator.BattleCoordinator;
import com.terminaldungeon.memento.GameHistory;
import com.terminaldungeon.mvc.controller.SwingGameController;
import com.terminaldungeon.mvc.model.GameModel;
import com.terminaldungeon.mvc.view.SwingGameView;
import com.terminaldungeon.prototype.Enemy;
import com.terminaldungeon.prototype.EnemyRegistry;

import javax.swing.SwingUtilities;
import java.util.List;

// Launcher for the Swing window version of the game (MVC, Member 1)
// Same wiring as Main, but with SwingGameView and SwingGameController.
// The model, battle coordinator and history are the same classes the terminal game uses.
public class GuiMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameModel model = new GameModel();

            EnemyRegistry registry = new EnemyRegistry();
            Enemy firstGoblin = registry.cloneEnemy("Goblin");
            Enemy secondGoblin = registry.cloneEnemy("Goblin");
            firstGoblin.setName("Goblin 1");
            secondGoblin.setName("Goblin 2");
            model.setEnemies(List.of(firstGoblin, secondGoblin, registry.cloneEnemy("Orc")));

            SwingGameView view = new SwingGameView();
            BattleCoordinator battle = new BattleCoordinator(model);
            GameHistory history = new GameHistory(model);
            new SwingGameController(model, view, battle, history);

            view.setVisible(true);
        });
    }
}
