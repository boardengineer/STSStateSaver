package savestate.actions;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import savestate.CardState;
import savestate.fastobjects.actions.UpdateOnlyUseCardAction;

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
}
