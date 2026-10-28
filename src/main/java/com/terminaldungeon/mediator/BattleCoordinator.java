package com.terminaldungeon.mediator;

import com.terminaldungeon.mvc.model.GameModel;

// PLACEHOLDER - Member 2: replace this file with the real battle coordinator (Mediator)
// Exists only so Member 1's controller compiles; it contains no battle logic.
// The controller calls playRound("attack") or playRound("heal") for each valid battle action.
// The real version applies damage/healing rules, runs the player and enemy turns (Template Method),
// updates the round, adds battle messages to the model, moves to the next encounter
// and sets the result to VICTORY or DEFEAT.
public class BattleCoordinator {
    private GameModel model;

    public BattleCoordinator(GameModel model) {
        this.model = model;
    }

    public void playRound(String action) {
        model.addMessage("[placeholder] Member 2's battle coordinator will resolve: " + action);
    }
}
