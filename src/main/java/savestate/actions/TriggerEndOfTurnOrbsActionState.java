package savestate.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.defect.TriggerEndOfTurnOrbsAction;

public class TriggerEndOfTurnOrbsActionState implements CurrentActionState, ActionState {
    @Override
    public AbstractGameAction loadCurrentAction() {
        return new TriggerEndOfTurnOrbsAction();
    }

    @Override
    public AbstractGameAction loadAction() {
        return loadCurrentAction();
    }
}
