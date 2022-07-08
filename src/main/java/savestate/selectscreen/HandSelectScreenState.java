package savestate.selectscreen;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.select.HandCardSelectScreen;
import savestate.CardQueueItemState;
import savestate.CardState;
import savestate.PlayerState;
import savestate.actions.ActionState;
import savestate.actions.CurrentActionState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class HandSelectScreenState {
    private final int numCardsToSelect;

    private final CardState[] selectedCards;
    private final ArrayList<ActionState> actionQueue;
    private final ArrayList<CardQueueItemState> cardQueueState;

    private final boolean wereCardsRetrieved;
    private final boolean canPickZero;
    private final boolean upTo;
    private final boolean anyNumber;
    private final boolean forTransform;
    private final boolean forUpgrade;
    private final int numSelected;
    private final CurrentActionState currentActionState;
    private final boolean isDisabled;

    public HandSelectScreenState() {
        selectedCards = PlayerState
                .toCardStateArray(AbstractDungeon.handCardSelectScreen.selectedCards.group);

        this.numCardsToSelect = AbstractDungeon.handCardSelectScreen.numCardsToSelect;
        this.wereCardsRetrieved = AbstractDungeon.handCardSelectScreen.wereCardsRetrieved;
        this.canPickZero = AbstractDungeon.handCardSelectScreen.canPickZero;
        this.upTo = AbstractDungeon.handCardSelectScreen.upTo;
        this.anyNumber = ReflectionHacks
                .getPrivate(AbstractDungeon.handCardSelectScreen, HandCardSelectScreen.class, "anyNumber");
        this.forTransform = ReflectionHacks
                .getPrivate(AbstractDungeon.handCardSelectScreen, HandCardSelectScreen.class, "forTransform");
        this.forUpgrade = ReflectionHacks
                .getPrivate(AbstractDungeon.handCardSelectScreen, HandCardSelectScreen.class, "forUpgrade");
        this.numSelected = AbstractDungeon.handCardSelectScreen.numSelected;

        AbstractGameAction currentAction = AbstractDungeon.actionManager.currentAction;

        isDisabled = AbstractDungeon.handCardSelectScreen.button.isDisabled;

        if (currentAction != null) {
            currentActionState = CurrentActionState.getCurrentActionState();
            actionQueue = ActionState.getActionQueueState();

            cardQueueState = new ArrayList<>();
            AbstractDungeon.actionManager.cardQueue.forEach(cardQueueItem -> cardQueueState
                    .add(new CardQueueItemState(cardQueueItem)));

            if (actionQueue.isEmpty()) {
                throw new IllegalStateException("The action queue shouldn't be empty in the middle of a selection screen");
            }
        } else {
            currentActionState = null;
            actionQueue = null;
            cardQueueState = null;
        }

    }

    public void loadHandSelectScreenState() {
        AbstractDungeon.handCardSelectScreen.button.isDisabled = isDisabled;

        AbstractDungeon.handCardSelectScreen.selectedCards.group = Arrays.stream(selectedCards)
                                                                         .map(CardState::loadCard)
                                                                         .collect(Collectors
                                                                                 .toCollection(ArrayList::new));

        AbstractDungeon.handCardSelectScreen.numSelected = numSelected;
        AbstractDungeon.handCardSelectScreen.numCardsToSelect = numCardsToSelect;
        AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = wereCardsRetrieved;
        AbstractDungeon.handCardSelectScreen.canPickZero = canPickZero;
        AbstractDungeon.handCardSelectScreen.upTo = upTo;

        ReflectionHacks
                .setPrivate(AbstractDungeon.handCardSelectScreen, HandCardSelectScreen.class, "anyNumber", anyNumber);
        ReflectionHacks
                .setPrivate(AbstractDungeon.handCardSelectScreen, HandCardSelectScreen.class, "forTransform", forTransform);
        ReflectionHacks
                .setPrivate(AbstractDungeon.handCardSelectScreen, HandCardSelectScreen.class, "forUpgrade", forUpgrade);
        ReflectionHacks
                .setPrivate(AbstractDungeon.handCardSelectScreen, HandCardSelectScreen.class, "hand", AbstractDungeon.player.hand);
        AbstractDungeon.handCardSelectScreen.numSelected = numSelected;

        if (currentActionState != null) {
            AbstractDungeon.actionManager.actions.clear();
            actionQueue.forEach(action -> AbstractDungeon.actionManager.actions.add(action
                    .loadAction()));

            AbstractDungeon.actionManager.cardQueue.clear();
            cardQueueState.forEach(cardQueueItemState -> AbstractDungeon.actionManager.cardQueue
                    .add(cardQueueItemState.loadItem()));

            AbstractDungeon.actionManager.currentAction = currentActionState.loadCurrentAction();
            AbstractDungeon.actionManager.phase = GameActionManager.Phase.EXECUTING_ACTIONS;


            if (AbstractDungeon.actionManager.actions.isEmpty()) {
                throw new IllegalStateException("this too shouldn't happen");
            }

        }
    }
}
