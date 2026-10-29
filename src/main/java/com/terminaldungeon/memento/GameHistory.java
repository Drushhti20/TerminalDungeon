package com.terminaldungeon.memento;

import com.terminaldungeon.mvc.model.GameModel;

// Memento caretaker: stores one undo snapshot and one in-memory checkpoint.
public class GameHistory {
    private final GameModel model;
    private GameModel.Snapshot undoSnapshot;
    private GameModel.Snapshot checkpoint;

    public GameHistory(GameModel model) {
        if (model == null) {
            throw new IllegalArgumentException("GameModel cannot be null.");
        }
        this.model = model;
    }

    // Called before every valid attack or heal round.
    public void recordRound() {
        undoSnapshot = model.createSnapshot();
    }

    // Undo is available once for the most recently recorded round.
    public String undo() {
        if (undoSnapshot == null) {
            return "No earlier round is available to undo.";
        }

        model.restoreSnapshot(undoSnapshot);
        undoSnapshot = null;

        return null;
    }

    // Store one checkpoint in memory.
    public String saveCheckpoint() {
        checkpoint = model.createSnapshot();
        return null;
    }

    // Restore the saved checkpoint.
    public String loadCheckpoint() {
        if (checkpoint == null) {
            return "No checkpoint has been saved yet.";
        }

        model.restoreSnapshot(checkpoint);

        // Loading clears undo history so it cannot undo past the loaded state.
        undoSnapshot = null;

        return null;
    }
}