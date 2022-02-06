package savestate.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.defect.DiscardPileToHandAction;
import com.megacrit.cardcrawl.actions.defect.RecycleAction;
import com.megacrit.cardcrawl.actions.defect.TriggerEndOfTurnOrbsAction;
import com.megacrit.cardcrawl.actions.unique.*;
import com.megacrit.cardcrawl.actions.utility.ScryAction;
import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.actions.watcher.ForeignInfluenceAction;
import com.megacrit.cardcrawl.actions.watcher.MeditateAction;
import com.megacrit.cardcrawl.actions.watcher.OmniscienceAction;

import java.util.function.Function;

public enum CurrentAction {
    ARMAMENTS_ACTION(ArmamentsAction.class, action -> new ArmamentsActionState(action)),
    ATTACKS_FROM_DECK_TO_HAND_ACTION(AttackFromDeckToHandAction.class, action -> new AttackFromDeckToHandActionState(action)),
    BETTER_DISCARD_PILE_TO_HAND_ACTION(BetterDiscardPileToHandAction.class, action -> new BetterDiscardPileToHandActionState(action)),
    BETTER_DRAW_PILE_TO_HAND_ACTION(BetterDrawPileToHandAction.class, action -> new BetterDrawPileToHandActionState(action)),
    CHOOSE_ONE_ACTION(ChooseOneAction.class, action -> new ChooseOneActionState(action)),
    CODEX_ACTION(CodexAction.class, action -> new CodexActionState(action)),
    DISCARD_ACTION(DiscardAction.class, action -> new DiscardActionState((DiscardAction) action)),
    DISCARD_PILE_TO_HAND_ACTION(DiscardPileToHandAction.class, action -> new DiscardPileToHandActionState(action)),
    DISCARD_PILE_TO_TOP_OF_DECK_ACTION(DiscardPileToTopOfDeckAction.class, action -> new DiscardPileToTopOfDeckActionState(action)),
    DICOVERY_ACTION(DiscoveryAction.class, action -> new DiscoveryActionState(action)),
    DUAL_WIELD_ACTION(DualWieldAction.class, action -> new DualWieldActionState(action)),
    EXHAUST_ACTION(ExhaustAction.class, action -> new ExhaustActionState(action)),
    EXHUME_ACTION(ExhumeAction.class, action -> new ExhumeActionState(action)),
    FOREIGN_INFLUENCE_ACTION(ForeignInfluenceAction.class, action -> new ForeignInfluenceActionState(action)),
    FORETHOUGHT_ACTION(ForethoughtAction.class, action -> new ForethoughtActionState(action)),
    PUT_ON_DECK_ACTION(PutOnDeckAction.class, action -> new PutOnDeckActionState(action)),
    OMNISCIENCE_ACTION(OmniscienceAction.class, action -> new OmniscienceActionState(action)),
    MEDITATE_ACTION(MeditateAction.class, action -> new MeditateActionState(action)),
    NIGHTMARE_ACTION(NightmareAction.class, action -> new NightmareActionState(action)),
    RECYCLE_ACTION(RecycleAction.class, action -> new RecycleActionState()),
    RETAIN_CARDS_ACTION(RetainCardsAction.class, action -> new RetainCardsActionState(action)),
    SCRY_ACTION(ScryAction.class, action -> new ScryActionState(action)),
    SETUP_ACTION(SetupAction.class, action -> new SetupActionState()),
    SKILL_FROM_DECK_TO_HAND_ACTION(SkillFromDeckToHandAction.class, action -> new SkillFromDeckToHandActionState(action)),
    TRIGGER_END_OF_TURN_ORBS_ACTION(TriggerEndOfTurnOrbsAction.class, action -> new TriggerEndOfTurnOrbsActionState());

    public Function<AbstractGameAction, CurrentActionState> factory;
    public Class<? extends AbstractGameAction> actionClass;

    CurrentAction(Class<? extends AbstractGameAction> actionClass, Function<AbstractGameAction, CurrentActionState> factory) {
        this.factory = factory;
        this.actionClass = actionClass;
    }

}
