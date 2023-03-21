package savestate.actions;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;

public class EmptyDeckShuffleActionState implements ActionState {
    private final int count;

    public EmptyDeckShuffleActionState(AbstractGameAction action) {
        this.count = ReflectionHacks.getPrivate(action, EmptyDeckShuffleAction.class, "count");
    }


    @Override
    public AbstractGameAction loadAction() {
        EmptyDeckShuffleAction result = new EmptyDeckShuffleAction();

        ReflectionHacks.setPrivate(result, EmptyDeckShuffleAction.class, "count", count);

        return result;
    }
}
