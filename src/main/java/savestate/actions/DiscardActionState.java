package savestate.actions;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class DiscardActionState implements CurrentActionState, ActionState {
    private final int amount;
    private final boolean isRandom;

    public DiscardActionState(AbstractGameAction action) {
        amount = action.amount;
        isRandom = ReflectionHacks.getPrivate(action, DiscardAction.class, "isRandom");
    }

    @Override
    public AbstractGameAction loadCurrentAction() {
        DiscardAction result = new DiscardAction(AbstractDungeon.player, AbstractDungeon.player, amount, isRandom);

        ReflectionHacks.setPrivate(result, AbstractGameAction.class, "duration", 0);

        return result;
    }

    @Override
    public AbstractGameAction loadAction() {
        return new DiscardAction(AbstractDungeon.player, AbstractDungeon.player, amount, isRandom);
    }

    @SpirePatch(
            clz = DiscardAction.class,
            paramtypez = {},
            method = "update"
    )
    public static class NoDoubleExhaustActionPatch {
        public static void Postfix(DiscardAction _instance) {
            // Force the action to stay in the the manager until cards are selected
            if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved && AbstractDungeon.isScreenUp) {
                _instance.isDone = false;
            }
        }
    }
}
