package savestate.actions;

import savestate.StateFactories;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.function.Function;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.actionManager;

public interface CurrentActionState {
    AbstractGameAction loadCurrentAction();

    static CurrentActionState getCurrentActionState() {
        if (StateFactories.currentActionByClassMap
                .containsKey(actionManager.currentAction.getClass())) {
            return StateFactories.currentActionByClassMap
                    .get(actionManager.currentAction.getClass()).factory
                    .apply(actionManager.currentAction);
        } else {
            throw new IllegalStateException("No State Factory for Current Action " + AbstractDungeon.actionManager.currentAction);
        }
    }

    class CurrentActionFactories {
        public Function<AbstractGameAction, CurrentActionState> factory;

        public CurrentActionFactories(Function<AbstractGameAction, CurrentActionState> factory) {
            this.factory = factory;
        }
    }
}
