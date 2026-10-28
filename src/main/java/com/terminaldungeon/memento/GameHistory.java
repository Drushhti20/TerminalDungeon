package com.terminaldungeon.memento;

import com.terminaldungeon.mvc.model.GameModel;

// PLACEHOLDER - Member 3: replace this file with the real history keeper (Memento caretaker)
// Exists only so Member 1's controller compiles; it stores no snapshots.
// The controller calls recordRound() before each valid battle round, and undo(), saveCheckpoint()
// and loadCheckpoint() for the matching commands. Each returns an error message, or null on success.
public class GameHistory {
    private GameModel model;

    public GameHistory(GameModel model) {
        this.model = model;
    }

    public void recordRound() {
    }

    public String undo() {
        return "Undo is not available yet (Member 3's history).";
    }

    public String saveCheckpoint() {
        return "Save is not available yet (Member 3's history).";
    }

    public String loadCheckpoint() {
        return "Load is not available yet (Member 3's history).";
    }
}
