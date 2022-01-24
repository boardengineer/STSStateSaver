package savestate.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class HealActionState implements ActionState {
    private final AbstractCreature target;
    private final AbstractCreature source;
    private final int amount;

    public HealActionState(AbstractGameAction action) {
        target = action.target;
        source = action.source;
        this.amount = action.amount;
    }

    @Override
    public AbstractGameAction loadAction() {
        return new HealAction(target, source, amount);
    }
}
