package savestate.actions;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;

public class RemoveDebuffsActionState implements ActionState {
    private final int creatureIndex;

    public RemoveDebuffsActionState(AbstractGameAction action) {
        creatureIndex = ActionState.indexForCreature(ReflectionHacks
                .getPrivate(action, RemoveDebuffsAction.class, "c"));
    }

    @Override
    public AbstractGameAction loadAction() {
        return new RemoveDebuffsAction(ActionState.creatureForIndex(creatureIndex));
    }
}
