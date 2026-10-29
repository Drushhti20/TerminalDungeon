package com.terminaldungeon.mvc.controller;

import com.terminaldungeon.builder.ConcreteHeroBuilder;
import com.terminaldungeon.builder.HeroDirector;
import com.terminaldungeon.mediator.BattleCoordinator;
import com.terminaldungeon.memento.GameHistory;
import com.terminaldungeon.mvc.model.GameModel;
import com.terminaldungeon.mvc.view.SwingGameView;

// Controller (Swing)
// Listens to the window's buttons and routes each request the same way as the terminal GameController:
// attack/heal go to the battle coordinator, undo/save/load go to history. It implements neither itself.
public class SwingGameController {
    private final GameModel model;
    private final SwingGameView view;
    private final BattleCoordinator battle;
    private final GameHistory history;
    private final HeroDirector director = new HeroDirector();
    private final ConcreteHeroBuilder heroBuilder = new ConcreteHeroBuilder();

    public SwingGameController(GameModel model, SwingGameView view,
                               BattleCoordinator battle, GameHistory history) {
        this.model = model;
        this.view = view;
        this.battle = battle;
        this.history = history;

        view.addStartButtonListener(event -> startGame());
        view.addAttackButtonListener(event -> playRound("attack"));
        view.addHealButtonListener(event -> playRound("heal"));
        view.addUndoButtonListener(event -> undo());
        view.addSaveButtonListener(event -> saveCheckpoint());
        view.addLoadButtonListener(event -> loadCheckpoint());
        view.addHelpButtonListener(event -> view.showHelp());

        view.showDungeonPreview(model);
    }

    // builds the hero from the setup screen, then switches to the battle screen
    public void startGame() {
        String name = view.getHeroName().trim();
        String errorMessage = model.validateHeroName(name);
        if (errorMessage != null) {
            view.showSetupError(errorMessage);
            return;
        }

        if (view.getSelectedPreset().equals("1")) {
            director.buildBalanced(heroBuilder, name);
        } else {
            director.buildStrongAttack(heroBuilder, name);
        }
        model.setHero(heroBuilder.getResult());

        view.showBattleScreen();
        view.showMessage(name + " enters the dungeon.");
        refresh();
    }

    // a valid battle action: history keeps a snapshot first, then the battle coordinator runs the round
    public void playRound(String action) {
        view.clearError();
        String errorMessage = model.checkCanAct();
        if (errorMessage != null) {
            view.showError(errorMessage);
            return;
        }

        history.recordRound();
        model.clearMessages();
        battle.playRound(action);

        view.showRoundMessages(model);
        refresh();
    }

    public void undo() {
        showHistoryResult(history.undo(), "Last round undone.");
    }

    public void saveCheckpoint() {
        showHistoryResult(history.saveCheckpoint(), "Checkpoint saved.");
    }

    public void loadCheckpoint() {
        showHistoryResult(history.loadCheckpoint(), "Checkpoint loaded.");
    }

    private void showHistoryResult(String errorMessage, String successMessage) {
        view.clearError();
        if (errorMessage == null) {
            view.showMessage(successMessage);
            refresh();
        } else {
            view.showError(errorMessage);
        }
    }

    // redraws the fighters and result; attack/heal are re-enabled if undo or load leaves the game over state
    private void refresh() {
        view.showStatus(model);
        view.showResult(model.getResult());
        view.setBattleActionsEnabled(!model.isGameOver());
    }
}
