
package com.terminaldungeon.template_method;

import com.terminaldungeon.mediator.BattleCoordinator;

public class PlayerTurn extends TurnTemplate {

    private final String playerAction;

    public PlayerTurn(
            BattleCoordinator coordinator,
            String playerAction) {
        super(coordinator);
        this.playerAction = playerAction;
    }

    @Override
    protected boolean canAct() {
        return playerAction != null
                && (playerAction.equalsIgnoreCase("attack")
                || playerAction.equalsIgnoreCase("heal"));
    }

    @Override
    protected String chooseAction() {
        return playerAction;
    }

    @Override
    protected void resolveAction(String action) {
        coordinator.resolvePlayerAction(action);
    }

    @Override
    protected void finishTurn() {
        coordinator.finishPlayerTurn();
    }
}
