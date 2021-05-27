package savestate.actions;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.BetterDrawPileToHandAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BetterDrawPileToHandActionState implements CurrentActionState {
    private final int numberOfCards;
    private final boolean optional;

    public BetterDrawPileToHandActionState(AbstractGameAction action) {
        this.numberOfCards = ReflectionHacks
                .getPrivate(action, BetterDrawPileToHandAction.class, "numberOfCards");
        this.optional = ReflectionHacks
                .getPrivate(action, BetterDrawPileToHandAction.class, "optional");
    }

    @Override
    public AbstractGameAction loadCurrentAction() {
        BetterDrawPileToHandAction result = new BetterDrawPileToHandAction(numberOfCards);

        ReflectionHacks
                .setPrivate(result, BetterDrawPileToHandAction.class, "numberOfCards", numberOfCards);

        ReflectionHacks
                .setPrivate(result, BetterDrawPileToHandAction.class, "optional", optional);

        // This should make the action only trigger the second half of the update
        ReflectionHacks
                .setPrivate(result, AbstractGameAction.class, "duration", 0);

        return result;
    }

    @SpirePatch(
            clz = BetterDrawPileToHandAction.class,
            paramtypez = {},
            method = "update"
    )
    public static class NoDoubleExhaustActionPatch {
        public static void Postfix(BetterDrawPileToHandAction _instance) {
            // Force the action to stay in the the manager until cards are selected
            if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved && AbstractDungeon.isScreenUp) {
                _instance.isDone = false;
            }
        }
    }
}
