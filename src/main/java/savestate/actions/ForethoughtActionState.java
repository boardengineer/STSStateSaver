package savestate.actions;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.ForethoughtAction;

public class ForethoughtActionState implements CurrentActionState {
    private final boolean chooseAny;

    public ForethoughtActionState(AbstractGameAction action) {
        this.chooseAny = ReflectionHacks.getPrivate(action, ForethoughtAction.class, "chooseAny");
    }

    @Override
    public AbstractGameAction loadCurrentAction() {
        ForethoughtAction result = new ForethoughtAction(chooseAny);

        // This should make the action only trigger the second half of the update
        ReflectionHacks
                .setPrivate(result, AbstractGameAction.class, "duration", 0);

        return result;
    }
}
