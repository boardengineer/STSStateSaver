package savestate.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.HandCheckAction;

public class HandCheckActionState implements ActionState {
    public HandCheckActionState(AbstractGameAction action) {
    }

    @Override
    public AbstractGameAction loadAction() {
        return new HandCheckAction();
    }
}
