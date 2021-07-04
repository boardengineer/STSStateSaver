package savestate.actions;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import savestate.powers.PowerState;
import savestate.StateFactories;

public class ApplyPowerActionState implements ActionState {
    private final PowerState powerToApply;
    private final int targetIndex;
    private final int amount;

    public ApplyPowerActionState(AbstractGameAction action) {
        this((ApplyPowerAction) action);
    }

    public ApplyPowerActionState(ApplyPowerAction action) {
        this.targetIndex = ActionState.indexForCreature(action.target);

        AbstractPower power = ReflectionHacks
                .getPrivate(action, ApplyPowerAction.class, "powerToApply");
        this.powerToApply = StateFactories.powerByIdMap.get(power.ID).factory.apply(power);;
        this.amount = action.amount;
    }


    @Override
    public AbstractGameAction loadAction() {
        AbstractCreature target = ActionState.creatureForIndex(targetIndex);
        return new ApplyPowerAction(target, target, powerToApply.loadPower(target), amount);
    }
}
