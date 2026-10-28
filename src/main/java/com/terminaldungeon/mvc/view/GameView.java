package com.terminaldungeon.mvc.view;

import com.terminaldungeon.builder.Hero;
import com.terminaldungeon.mvc.model.GameModel;
import com.terminaldungeon.mvc.model.GameResult;
import com.terminaldungeon.prototype.Enemy;

import java.util.List;

// View
// Shows the text the player sees; it only displays data and never calculates damage or decides enemy moves
public class GameView {

    public void showWelcome() {
        System.out.println("=================================");
        System.out.println("        TERMINAL DUNGEON");
        System.out.println("=================================");
        System.out.println("Defeat every enemy to win. Lose all your health and you lose.");
    }

    public void showNamePrompt() {
        System.out.print("\nEnter your hero's name: ");
    }

    public void showPresetMenu() {
        System.out.println("\nChoose your equipment preset:");
        System.out.println("  1. Balanced      - moderate attack and defence");
        System.out.println("  2. Strong attack - more damage, less defence");
        System.out.print("Enter 1 or 2: ");
    }

    public void showHeroCreated(Hero hero) {
        System.out.println("\nHero ready: " + hero.getName()
                + " | HP " + hero.getHealth() + "/" + hero.getMaxHealth()
                + " | ATK " + hero.getAttack()
                + " | DEF " + hero.getDefence());
    }

    public void showCommandPrompt() {
        System.out.print("\n> ");
    }

    public void showStatus(GameModel model) {
        Hero hero = model.getHero();
        Enemy enemy = model.getCurrentEnemy();

        System.out.println("\n---------- Round " + model.getRound() + " ----------");
        if (hero != null) {
            System.out.println("Hero : " + hero.getName() + "  HP " + hero.getHealth() + "/" + hero.getMaxHealth());
        }
        if (enemy != null) {
            System.out.println("Enemy: " + enemy.getName() + "  HP " + enemy.getHealth() + "/" + enemy.getMaxHealth()
                    + "  (encounter " + (model.getCurrentEncounter() + 1) + " of " + model.getEnemies().size() + ")");
        }
    }

    public void showMessages(List<String> messages) {
        for (String message : messages) {
            System.out.println(message);
        }
    }

    public void showHelp() {
        System.out.println("\nCommands:");
        System.out.println("  attack  - attack the current enemy");
        System.out.println("  heal    - restore some of your health");
        System.out.println("  status  - show health and round");
        System.out.println("  save    - save a checkpoint");
        System.out.println("  load    - load the saved checkpoint");
        System.out.println("  undo    - undo the last round");
        System.out.println("  help    - show this list");
        System.out.println("  quit    - leave the game");
        System.out.println("Status, help, save, load and undo do not cost a turn.");
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void showError(String message) {
        System.out.println("! " + message);
    }

    public void showResult(GameResult result) {
        if (result == GameResult.VICTORY) {
            System.out.println("\n*** VICTORY! Every enemy in the dungeon is defeated. ***");
        } else if (result == GameResult.DEFEAT) {
            System.out.println("\n*** DEFEAT. Your hero has fallen. ***");
        }
        if (result != GameResult.IN_PROGRESS) {
            System.out.println("You can still undo, load or quit.");
        }
    }

    public void showGoodbye() {
        System.out.println("\nThanks for playing Terminal Dungeon.");
    }
}
