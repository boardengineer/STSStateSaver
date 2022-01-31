package savestate.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.EndTurnAction;

public class EndTurnActionState implements ActionState {
    @Override
    public AbstractGameAction loadAction() {
        return new EndTurnAction();
    }
}
