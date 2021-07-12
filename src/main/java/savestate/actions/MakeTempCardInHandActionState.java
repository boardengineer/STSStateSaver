package savestate.actions;

import basemod.ReflectionHacks;
import savestate.CardState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class MakeTempCardInHandActionState implements ActionState {
    private final CardState c;
    int amount;

    public MakeTempCardInHandActionState(AbstractGameAction action) {
        this((MakeTempCardInHandAction) action);
    }

    public MakeTempCardInHandActionState(MakeTempCardInHandAction action) {
        AbstractCard card = ReflectionHacks.getPrivate(action, MakeTempCardInHandAction.class, "c");
        this.c = CardState.forCard(card);
        this.amount = action.amount;
    }


    @Override
    public AbstractGameAction loadAction() {
        return new MakeTempCardInHandAction(c.loadCard(), this.amount);
    }
}
