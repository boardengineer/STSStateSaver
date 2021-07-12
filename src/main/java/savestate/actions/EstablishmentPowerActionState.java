package savestate.actions;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.EstablishmentPowerAction;

public class EstablishmentPowerActionState implements ActionState {
    private final int discountAmount;

    public EstablishmentPowerActionState(AbstractGameAction action) {
        this.discountAmount = ReflectionHacks
                .getPrivate(action, EstablishmentPowerAction.class, "discountAmount");
    }

    @Override
    public AbstractGameAction loadAction() {
        return new EstablishmentPowerAction(discountAmount);
    }
}
