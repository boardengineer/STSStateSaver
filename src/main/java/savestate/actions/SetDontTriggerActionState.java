package savestate.actions;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.SetDontTriggerAction;
import savestate.CardState;

public class SetDontTriggerActionState implements ActionState {
    private final int cardIndex;
    private final boolean trigger;

    public SetDontTriggerActionState(AbstractGameAction action) {
        this((SetDontTriggerAction) action);
    }

    public SetDontTriggerActionState(SetDontTriggerAction action) {
        this.cardIndex = CardState.indexForCard(ReflectionHacks
                .getPrivate(action, SetDontTriggerAction.class, "card"));
        this.trigger = ReflectionHacks.getPrivate(action, SetDontTriggerAction.class, "trigger");
    }

    @Override
    public AbstractGameAction loadAction() {
        return new SetDontTriggerAction(CardState.cardForIndex(cardIndex), trigger);
    }
}
