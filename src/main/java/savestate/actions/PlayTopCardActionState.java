package savestate.actions;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.PlayTopCardAction;

public class PlayTopCardActionState implements ActionState {
    private final int targetIndex;
    private final boolean exhaustCards;

    public PlayTopCardActionState(AbstractGameAction action) {
        targetIndex = ActionState.indexForCreature(action.target);
        exhaustCards = ReflectionHacks.getPrivate(action, PlayTopCardAction.class, "exhaustCards");
    }

    @Override
    public AbstractGameAction loadAction() {
        return new PlayTopCardAction(ActionState.creatureForIndex(targetIndex), exhaustCards);
    }
}
