package savestate.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;

public class EnqueueEndTurnActionState implements ActionState {
    @Override
    public AbstractGameAction loadAction() {
        return new EnqueueEndTurnAction();
    }
}
