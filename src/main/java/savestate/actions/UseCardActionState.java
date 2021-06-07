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

    public UseCardActionState(UseCardAction action) {
        AbstractCard card = ReflectionHacks.getPrivate(action, UseCardAction.class, "targetCard");
        this.card = new CardState(card);
    }

    public UseCardActionState(UpdateOnlyUseCardAction action) {
        AbstractCard card = ReflectionHacks
                .getPrivate(action, UpdateOnlyUseCardAction.class, "targetCard");
        this.card = new CardState(card);
    }

    @Override
    public UpdateOnlyUseCardAction loadAction() {
        AbstractCard resultCard = card.loadCard();

        // TODO: at some point the target here will matter
        return new UpdateOnlyUseCardAction(resultCard, null);
    }

    @SpirePatch(clz = UseCardAction.class, method = "update")
    public static class ResetOnUse {
        @SpirePostfixPatch
        public static void maybeReset(UseCardAction action) {
            if (shouldGoFast) {
                AbstractCard card = ReflectionHacks
                        .getPrivate(action, UseCardAction.class, "targetCard");
                card.resetAttributes();
            }
        }
    }
}
