package savestate.actions;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.BetterDiscardPileToHandAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BetterDiscardPileToHandActionState implements CurrentActionState {
    private final int numberOfCards;
    private final boolean optional;
    private final int newCost;
    private final boolean setCost;

    BetterDiscardPileToHandActionState(AbstractGameAction action) {
        this((BetterDiscardPileToHandAction) action);
    }

    public BetterDiscardPileToHandActionState(BetterDiscardPileToHandAction action) {
        this.numberOfCards = ReflectionHacks
                .getPrivate(action, BetterDiscardPileToHandAction.class, "numberOfCards");
        this.optional = ReflectionHacks
                .getPrivate(action, BetterDiscardPileToHandAction.class, "optional");
        this.newCost = ReflectionHacks
                .getPrivate(action, BetterDiscardPileToHandAction.class, "newCost");
        this.setCost = ReflectionHacks
                .getPrivate(action, BetterDiscardPileToHandAction.class, "setCost");
    }

    @Override
    public AbstractGameAction loadCurrentAction() {
        BetterDiscardPileToHandAction result = new BetterDiscardPileToHandAction(numberOfCards);

        ReflectionHacks
                .setPrivate(result, BetterDiscardPileToHandAction.class, "optional", optional);
        ReflectionHacks
                .setPrivate(result, BetterDiscardPileToHandAction.class, "newCost", newCost);
        ReflectionHacks
                .setPrivate(result, BetterDiscardPileToHandAction.class, "setCost", setCost);

        // This should make the action only trigger the second half of the update
        ReflectionHacks
                .setPrivate(result, AbstractGameAction.class, "duration", 0);

        return result;
    }

    @SpirePatch(
            clz = BetterDiscardPileToHandAction.class,
            paramtypez = {},
            method = "update"
    )
    public static class NoDoubleExhaustActionPatch {
        public static void Postfix(BetterDiscardPileToHandAction _instance) {
            // Force the action to stay in the the manager until cards are selected
            if (AbstractDungeon.gridSelectScreen.selectedCards
                    .isEmpty() && AbstractDungeon.isScreenUp) {
                _instance.isDone = false;
            }
        }
    }
}
