package savestate.actions;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.PutOnDeckAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class PutOnDeckActionState implements CurrentActionState {
    private final int amount;
    private final boolean isRandom;

    public PutOnDeckActionState(AbstractGameAction action) {
        this.amount = action.amount;

        this.isRandom = ReflectionHacks.getPrivate(action, PutOnDeckAction.class, "isRandom");
    }

    @Override
    public AbstractGameAction loadCurrentAction() {
        PutOnDeckAction action = new PutOnDeckAction(AbstractDungeon.player, AbstractDungeon.player, amount, isRandom);

        ReflectionHacks
                .setPrivate(action, AbstractGameAction.class, "duration", 0);

        return action;
    }

    @SpirePatch(
            clz = PutOnDeckAction.class,
            paramtypez = {},
            method = "update"
    )
    public static class forceNotDonePatch {
        public static void Postfix(PutOnDeckAction _instance) {
            // Force the action to stay in the the manager until cards are selected
            if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved && AbstractDungeon.isScreenUp) {
                _instance.isDone = false;
            }
        }
    }
}
