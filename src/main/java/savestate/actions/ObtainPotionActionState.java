package savestate.actions;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ObtainPotionAction;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import savestate.PotionState;

public class ObtainPotionActionState implements ActionState {
    private final PotionState potion;

    public ObtainPotionActionState(AbstractGameAction action) {
        AbstractPotion actionPotion = ReflectionHacks
                .getPrivate(action, ObtainPotionAction.class, "potion");

        potion = new PotionState(actionPotion);
    }

    @Override
    public AbstractGameAction loadAction() {
        return new ObtainPotionAction(potion.loadPotion());
    }
}
