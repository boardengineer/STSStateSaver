package savestate.actions;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.SetDontTriggerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import savestate.CardState;

public class SetDontTriggerActionState implements ActionState {
    private final int cardIndex;
    private final boolean trigger;

    public SetDontTriggerActionState(AbstractGameAction action) {
        this((SetDontTriggerAction) action);
    }

    public SetDontTriggerActionState(SetDontTriggerAction action) {
        AbstractCard card = ReflectionHacks
                .getPrivate(action, SetDontTriggerAction.class, "card");
        this.cardIndex = CardState.indexForCard(card);
        this.trigger = ReflectionHacks.getPrivate(action, SetDontTriggerAction.class, "trigger");
    }

    @Override
    public AbstractGameAction loadAction() {
        AbstractCard card = new Madness().makeCopy();

        try {
            card = CardState.cardForIndex(cardIndex);
        } catch (IllegalStateException e) {
        }
        return new SetDontTriggerAction(card, trigger);
    }
}
