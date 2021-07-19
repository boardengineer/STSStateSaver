package savestate.actions;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;

public class LoseEnergyActionState implements ActionState {
    private final int energyLoss;

    LoseEnergyActionState(AbstractGameAction action) {
        this.energyLoss = ReflectionHacks
                .getPrivate(action, LoseEnergyAction.class, "energyLoss");
    }

    @Override
    public AbstractGameAction loadAction() {
        return new LoseEnergyAction(energyLoss);
    }
}
