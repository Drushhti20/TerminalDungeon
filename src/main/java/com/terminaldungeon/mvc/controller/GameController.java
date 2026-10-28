package com.terminaldungeon.mvc.controller;

import com.terminaldungeon.builder.ConcreteHeroBuilder;
import com.terminaldungeon.builder.HeroDirector;
import com.terminaldungeon.mediator.BattleCoordinator;
import com.terminaldungeon.memento.GameHistory;
import com.terminaldungeon.mvc.model.GameModel;
import com.terminaldungeon.mvc.view.GameView;

import java.util.Scanner;

// Controller
// Receives and checks terminal input, then routes each request to the right feature.
// It does not implement combat (Member 2) or history (Member 3) itself.
public class GameController {
    private static final int MAX_NAME_LENGTH = 20;

    private final GameModel model;
    private final GameView view;
    private final BattleCoordinator battle;
    private final GameHistory history;
    private final Scanner input;
    private final HeroDirector director = new HeroDirector();
    private final ConcreteHeroBuilder heroBuilder = new ConcreteHeroBuilder();

    public GameController(GameModel model, GameView view, BattleCoordinator battle,
                          GameHistory history, Scanner input) {
        this.model = model;
        this.view = view;
        this.battle = battle;
        this.history = history;
        this.input = input;
    }

    // asks for a name and a preset, then builds the hero and stores it in the model
    // returns false if the input ends before setup is complete
    public boolean setupHero() {
        view.showWelcome();

        String name = readHeroName();
        if (name == null) {
            return false;
        }

        String preset = readPreset();
        if (preset == null) {
            return false;
        }

        if (preset.equals("1")) {
            director.buildBalanced(heroBuilder, name);
        } else {
            director.buildStrongAttack(heroBuilder, name);
        }
        model.setHero(heroBuilder.getResult());
        view.showHeroCreated(model.getHero());
        return true;
    }

    // reads commands until the player quits or the input ends
    public void run() {
        view.showHelp();
        view.showStatus(model);

        boolean running = true;
        while (running) {
            view.showCommandPrompt();
            if (!input.hasNextLine()) {
                break;
            }
            running = handleCommand(input.nextLine());
        }
        view.showGoodbye();
    }

    // returns false when the player quits
    public boolean handleCommand(String line) {
        String command = line.trim().toLowerCase();

        switch (command) {
            case "attack":
            case "heal":
                playRound(command);
                break;

            case "status":
                view.showStatus(model);
                break;

            case "help":
                view.showHelp();
                break;

            case "undo":
                showHistoryResult(history.undo(), "Last round undone.");
                break;

            case "save":
                showHistoryResult(history.saveCheckpoint(), "Checkpoint saved.");
                break;

            case "load":
                showHistoryResult(history.loadCheckpoint(), "Checkpoint loaded.");
                break;

            case "quit":
                return false;

            case "":
                view.showError("Please type a command. Type help to see the list.");
                break;

            default:
                view.showError("Unknown command '" + command + "'. Type help to see the list.");
        }
        return true;
    }

    // a valid battle action: history keeps a snapshot first, then the battle coordinator runs the round
    private void playRound(String action) {
        String errorMessage = model.checkCanAct();
        if (errorMessage != null) {
            view.showError(errorMessage);
            return;
        }

        history.recordRound();
        model.clearMessages();
        battle.playRound(action);

        view.showMessages(model.getMessages());
        view.showStatus(model);
        if (model.isGameOver()) {
            view.showResult(model.getResult());
        }
    }

    private void showHistoryResult(String errorMessage, String successMessage) {
        if (errorMessage == null) {
            view.showMessage(successMessage);
            view.showStatus(model);
        } else {
            view.showError(errorMessage);
        }
    }

    private String readHeroName() {
        while (true) {
            view.showNamePrompt();
            if (!input.hasNextLine()) {
                return null;
            }
            String name = input.nextLine().trim();

            if (name.isEmpty()) {
                view.showError("Name cannot be empty.");
            } else if (name.length() > MAX_NAME_LENGTH) {
                view.showError("Name must be " + MAX_NAME_LENGTH + " characters or fewer.");
            } else {
                return name;
            }
        }
    }

    private String readPreset() {
        while (true) {
            view.showPresetMenu();
            if (!input.hasNextLine()) {
                return null;
            }
            String choice = input.nextLine().trim();

            if (choice.equals("1") || choice.equals("2")) {
                return choice;
            }
            view.showError("Please enter 1 or 2.");
        }
    }
}
