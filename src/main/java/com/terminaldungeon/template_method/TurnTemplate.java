
package com.terminaldungeon.template_method;

import com.terminaldungeon.mediator.BattleCoordinator;

public abstract class TurnTemplate {

    protected final BattleCoordinator coordinator;

    public TurnTemplate(BattleCoordinator coordinator) {
        this.coordinator = coordinator;
    }

    // Template Method: defines the fixed sequence of a turn.
    public final void executeTurn() {
        if (!canAct()) {
            return;
        }

        String action = chooseAction();

        if (action == null || action.trim().isEmpty()) {
            return;
        }

        resolveAction(action);
        finishTurn();
    }

    protected abstract boolean canAct();

    protected abstract String chooseAction();

    protected abstract void resolveAction(String action);

    protected void finishTurn() {
        // Optional final step.
    }
}
