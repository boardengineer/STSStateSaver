package savestate.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.defect.RecycleAction;
import com.megacrit.cardcrawl.actions.unique.*;

import java.util.function.Function;

public enum CurrentAction {
    ARMAMENTS_ACTION(ArmamentsAction.class, action -> new ArmamentsActionState(action)),
    BETTER_DISCARD_PILE_TO_HAND_ACTION(BetterDiscardPileToHandAction.class, action -> new BetterDiscardPileToHandActionState(action)),
    BETTER_DRAW_PILE_TO_HAND_ACTION(BetterDrawPileToHandAction.class, action -> new BetterDrawPileToHandActionState(action)),
    DISCARD_ACTION(DiscardAction.class, action -> new DiscardActionState((DiscardAction) action)),
    DISCARD_PILE_TO_TOP_OF_DECK_ACTION(DiscardPileToTopOfDeckAction.class, action -> new DiscardPileToTopOfDeckActionState(action)),
    DUAL_WIELD_ACTION(DualWieldAction.class, action -> new DualWieldActionState(action)),
    EXHAUST_ACTION(ExhaustAction.class, action -> new ExhaustActionState(action)),
    PUT_ON_DECK_ACTION(PutOnDeckAction.class, action -> new PutOnDeckActionState(action)),
    NIGHTMARE_ACTION(NightmareAction.class, action -> new NightmareActionState(action)),
    RECYCLE_ACTION(RecycleAction.class, action -> new RecycleActionState()),
    RETAIN_CARDS_ACTION(RetainCardsAction.class, action -> new RetainCardsActionState(action)),
    SETUP_ACTION(SetupAction.class, action -> new SetupActionState());

    public Function<AbstractGameAction, CurrentActionState> factory;
    public Class<? extends AbstractGameAction> actionClass;

    CurrentAction(Class<? extends AbstractGameAction> actionClass, Function<AbstractGameAction, CurrentActionState> factory) {
        this.factory = factory;
        this.actionClass = actionClass;
    }

}
