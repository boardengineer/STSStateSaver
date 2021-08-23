package savestate.actions;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.DiscardToHandAction;
import savestate.CardState;

public class DiscardToHandActionState implements ActionState {
    private final int cardIndex;

    public DiscardToHandActionState(AbstractGameAction action) {
        this((DiscardToHandAction) action);
    }

    public DiscardToHandActionState(DiscardToHandAction action) {
        this.cardIndex = CardState.indexForCard(ReflectionHacks
                .getPrivate(action, DiscardToHandAction.class, "card"));
    }

    @Override
    public AbstractGameAction loadAction() {
        return new DiscardToHandAction(CardState.cardForIndex(cardIndex));
    }
}
