package savestate.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainGoldAction;

public class GainGoldActionState implements ActionState {
    private final int amount;

    public GainGoldActionState(AbstractGameAction action) {
        this.amount = action.amount;
    }

    @Override
    public AbstractGameAction loadAction() {
        return new GainGoldAction(amount);
    }
}
