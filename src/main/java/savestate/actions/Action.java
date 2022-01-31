package savestate.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.ClearCardQueueAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.defect.TriggerEndOfTurnOrbsAction;
import com.megacrit.cardcrawl.actions.unique.EstablishmentPowerAction;
import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.actions.utility.DiscardToHandAction;
import com.megacrit.cardcrawl.actions.utility.HandCheckAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;
import com.megacrit.cardcrawl.actions.watcher.TriggerMarksAction;
import savestate.fastobjects.actions.UpdateOnlyUseCardAction;

import java.util.function.Function;

public enum Action {
    APPLY_POWER_ACTION(ApplyPowerAction.class, action -> new ApplyPowerActionState(action)),
    APPLY_POWER_TO_RANDOM_CREATURE_ACTION(ApplyPowerToRandomEnemyAction.class, action -> new ApplyPowerToRandomEnemyActionState(action)),
    CHANGE_STANCE_ACTION(ChangeStanceAction.class, action -> new ChangeStanceActionState(action)),
    CHANGE_STATE_ACTION(ChangeStateAction.class, action -> new ChangeStateActionState(action)),
    CLEAR_CARD_QUEUE_ACTION(ClearCardQueueAction.class, action -> new ClearCardQueueActionState()),
    DAMAGE_ACTION(DamageAction.class, action -> new DamageActionState(action)),
    DAMAGE_ALL_ENEMIES_ACTION(DamageAllEnemiesAction.class, action -> new DamageAllEnemiesActionState(action)),
    DAMAGE_RANDOM_ENEMY_ACTION(DamageRandomEnemyAction.class, action -> new DamageRandomEnemyActionState(action)),
    DISCARD_AT_END_OF_TURN_ACTION(DiscardAtEndOfTurnAction.class, action -> new DiscardAtEndOfTurnActionState()),
    DISCARD_TO_HAND_ACTION(DiscardToHandAction.class, action -> new DiscardToHandActionState(action)),
    DRAW_CARD_ACTION(DrawCardAction.class, action -> new DrawCardActionState(action)),
    ENABLE_END_TURN_BUTTON_ACTION(EnableEndTurnButtonAction.class, action -> new EnableEndTurnButtonActionState(action)),
    END_TURN_ACTION(EndTurnAction.class, action -> new EndTurnActionState()),
    ENQUEUE_END_TURN_ACTION(EnqueueEndTurnAction.class, action -> new EnqueueEndTurnActionState()),
    ESCAPE_ACTION(EscapeAction.class, action -> new EscapeActionState(action)),
    ESTABLISHMENT_POWER_ACTION(EstablishmentPowerAction.class, action -> new EstablishmentPowerActionState(action)),
    GAIN_BLOCK_ACTION(GainBlockAction.class, action -> new GainBlockActionState(action)),
    GAIN_ENERGY_ACTION(GainEnergyAction.class, action -> new GainEnergyActionState(action)),
    HAND_CHECK_ACTION(HandCheckAction.class, action -> new HandCheckActionState(action)),
    HEAL_ACTION(HealAction.class, action -> new HealActionState(action)),
    LOSE_ENERGY_ACTION(LoseEnergyAction.class, action -> new LoseEnergyActionState(action)),
    LOSE_HP_ACTION(LoseHPAction.class, action -> new LoseHPActionState(action)),
    MAKE_TEMP_CARD_IN_DRAW_PILE_ACTION(MakeTempCardInDrawPileAction.class, action -> new MakeTempCardInDrawPileActionState(action)),
    MAKE_TEMP_CARD_IN_HAND_ACTION(MakeTempCardInHandAction.class, action -> new MakeTempCardInHandActionState(action)),
    MONSTER_START_TURN_ACTION(MonsterStartTurnAction.class, action -> new MonsterStartTurnActionState()),
    NEW_QUEUE_CARD_ACTION(NewQueueCardAction.class, action -> new NewQueueCardActionState()),
    PRESS_END_TURN_BUTTON_ACTION(PressEndTurnButtonAction.class, action -> new PressEndTurnButtonActionState()),
    REDUCE_POWER_ACTION(ReducePowerAction.class, action -> new ReducePowerActionState(action)),
    REMOVE_DEBUFFS_ACTION(RemoveDebuffsAction.class, action -> new RemoveDebuffsActionState(action)),
    REMOVE_SPECIFIC_POWER_ACTION(RemoveSpecificPowerAction.class, action -> new RemoveSpecificPowerActionState(action)),
    ROLL_MOVE_ACTION(RollMoveAction.class, action -> new RollMoveActionState(action)),
    SET_DONT_TRIGGER_ACTION(SetDontTriggerAction.class, action -> new SetDontTriggerActionState(action)),
    SET_MOVE_ACTION(SetMoveAction.class, action -> new SetMoveActionState(action)),
    TRIGGER_END_OF_TURN_ORBS_ACTION(TriggerEndOfTurnOrbsAction.class, action -> new TriggerEndOfTurnOrbsActionState()),
    TRIGGER_MARKS_ACTION(TriggerMarksAction.class, action -> new TriggerMarksActionState()),
    UPDATE_ONLY_USE_CARD_ACTION(UpdateOnlyUseCardAction.class, action -> new UseCardActionState((UpdateOnlyUseCardAction) action)),
    UPGRADE_RANDOM_CARD_ACTION(UpgradeRandomCardAction.class, action -> new UpgradeRandomCardActionState()),
    USE_CARD_ACTION(UseCardAction.class, action -> new UseCardActionState((UseCardAction) action));

    public Function<AbstractGameAction, ActionState> factory;
    public Class<? extends AbstractGameAction> actionClass;

    Action(Class<? extends AbstractGameAction> actionClass, Function<AbstractGameAction, ActionState> factory) {
        this.factory = factory;
        this.actionClass = actionClass;
    }

}
