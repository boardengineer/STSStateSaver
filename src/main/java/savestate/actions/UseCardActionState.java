package savestate.actions;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import savestate.fastobjects.actions.UpdateOnlyUseCardAction;
import savestate.CardState;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;

import static savestate.SaveStateMod.shouldGoFast;

public class UseCardActionState implements ActionState {
    private final CardState card;
    private final boolean exhaustCard;

    public UseCardActionState(UseCardAction action) {
        AbstractCard card = ReflectionHacks.getPrivate(action, UseCardAction.class, "targetCard");

        this.card = CardState.forCard(card);
        this.exhaustCard = action.exhaustCard;
    }

    public UseCardActionState(UpdateOnlyUseCardAction action) {
        AbstractCard card = ReflectionHacks
                .getPrivate(action, UpdateOnlyUseCardAction.class, "targetCard");

        this.card = CardState.forCard(card);
        this.exhaustCard = action.exhaustCard;
    }

    @Override
    public UpdateOnlyUseCardAction loadAction() {
        AbstractCard resultCard = card.loadCard();

        // TODO: at some point the target here will matter
        UpdateOnlyUseCardAction result = new UpdateOnlyUseCardAction(resultCard, null);

        result.exhaustCard = exhaustCard;

        return result;
    }

    @SpirePatch(clz = UseCardAction.class, method = "update")
    public static class ResetOnUse {
        @SpirePostfixPatch
        public static void maybeReset(UseCardAction action) {
            if (shouldGoFast) {
                AbstractCard card = ReflectionHacks
                        .getPrivate(action, UseCardAction.class, "targetCard");
                // PASHA LOOK HERE
//                card.resetAttributes();
            }
        }
    }
}
