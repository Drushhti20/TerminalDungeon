package com.terminaldungeon.mvc.view;

import com.terminaldungeon.builder.Hero;
import com.terminaldungeon.mvc.model.GameModel;
import com.terminaldungeon.mvc.model.GameResult;
import com.terminaldungeon.prototype.Enemy;

import java.util.ArrayList;
import java.util.List;

// Flavour text and the end-of-game summary, shared by the terminal and Swing views.
// Display only: it reads the model and never changes it.
final class Story {
    static final String OPENING =
            "You descend the worn stone steps. Torches flicker, and something growls in the dark...";
    static final String VICTORY =
            "The last monster falls. Silence returns to the dungeon - you are victorious!";
    static final String DEFEAT =
            "Your vision fades as you fall to the cold stone floor...";

    private Story() {
    }

    static String encounterIntro(Enemy enemy) {
        if ("Orc".equalsIgnoreCase(enemy.getType())) {
            return "The ground shakes. A hulking Orc chieftain blocks the final door, potions on his belt.";
        }
        if ("Skeleton".equalsIgnoreCase(enemy.getType())) {
            return "Bones rattle as a Skeleton warrior rises from the dust. Wounded, it fights in a fury.";
        }
        return "A snarling " + enemy.getName() + " leaps out of the shadows, dagger drawn.";
    }

    static String encounterTitle(GameModel model, Enemy enemy) {
        return "Encounter " + (model.getCurrentEncounter() + 1) + " of " + model.getEnemies().size()
                + ": " + enemy.getName();
    }

    // S/A/B/C by health left after a victory; D for a defeat
    static String rank(GameModel model) {
        if (model.getResult() != GameResult.VICTORY) {
            return "D";
        }
        Hero hero = model.getHero();
        int percent = hero.getHealth() * 100 / hero.getMaxHealth();
        if (percent >= 60) {
            return "S";
        }
        if (percent >= 40) {
            return "A";
        }
        if (percent >= 20) {
            return "B";
        }
        return "C";
    }

    static List<String> summary(GameModel model, BattleStats stats) {
        Hero hero = model.getHero();
        int defeated = Math.min(model.getCurrentEncounter(), model.getEnemies().size());
        int[] totals = stats.totals(model.getRound());

        List<String> lines = new ArrayList<>();
        lines.add("Rounds fought:    " + model.getRound());
        lines.add("Health left:      " + hero.getHealth() + " / " + hero.getMaxHealth());
        lines.add("Enemies defeated: " + defeated + " of " + model.getEnemies().size());
        lines.add("Damage dealt:     " + totals[BattleStats.DEALT]);
        lines.add("Damage taken:     " + totals[BattleStats.TAKEN]);
        lines.add("Heals used:       " + totals[BattleStats.HEALS] + " (+" + totals[BattleStats.HEALED] + " HP)");
        return lines;
    }
}
