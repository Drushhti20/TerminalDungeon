
package com.terminaldungeon.template_method;

import com.terminaldungeon.mediator.BattleCoordinator;
import java.util.function.Supplier;

public class EnemyTurn extends TurnTemplate {

    private final Supplier<String> actionSelector;
    private String selectedAction;

    public EnemyTurn(
            BattleCoordinator coordinator,
            Supplier<String> actionSelector) {
        super(coordinator);
        this.actionSelector = actionSelector;
    }

    @Override
    protected boolean canAct() {
        selectedAction = null;

        if (actionSelector == null) {
            return false;
        }

        selectedAction = actionSelector.get();

        return selectedAction != null
                && !selectedAction.trim().isEmpty();
    }

    @Override
    protected String chooseAction() {
        return selectedAction;
    }

    @Override
    protected void resolveAction(String action) {
        coordinator.resolveEnemyAction(action);
    }

    @Override
    protected void finishTurn() {
        coordinator.finishRound();
    }
}
