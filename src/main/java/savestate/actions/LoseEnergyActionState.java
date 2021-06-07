package savestate.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;

public class LoseEnergyActionState implements ActionState {
    private final int amount;

    LoseEnergyActionState(AbstractGameAction action) {
        this.amount = action.amount;
    }

    @Override
    public AbstractGameAction loadAction() {
        return new LoseEnergyAction(amount);
    }
}
