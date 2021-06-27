package savestate.actions;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.ScryAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ScryActionState implements CurrentActionState {
    private final int amount;

    public ScryActionState(AbstractGameAction action) {
        this.amount = action.amount;
    }

    @Override
    public AbstractGameAction loadCurrentAction() {
        ScryAction result = new ScryAction(amount);

        // This should make the action only trigger the second half of the update
        ReflectionHacks
                .setPrivate(result, AbstractGameAction.class, "duration", 0);

        return new ScryAction(amount);
    }

    @SpirePatch(
            clz = ScryAction.class,
            paramtypez = {},
            method = "update"
    )
    public static class NoDoubleScryPatch {
        public static void Postfix(ScryAction _instance) {
            // Force the action to stay in the the manager until cards are selected
            if (AbstractDungeon.isScreenUp) {
                _instance.isDone = false;
            }
        }
    }
}
