package savestate.selectscreen;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import savestate.CardQueueItemState;
import savestate.CardState;
import savestate.actions.ActionState;
import savestate.actions.CurrentActionState;

import java.util.ArrayList;

public class CardRewardScreenState {
    private final CurrentActionState currentActionState;
    private final ArrayList<ActionState> actionQueue;
    private final ArrayList<CardQueueItemState> cardQueueState;

    private final RewardItem rItem;
    private final boolean discovery;
    private final boolean chooseOne;
    private final boolean skippable;
    private final boolean draft;
    private final CardState discoveryCard;
    private final CardState touchCard;

    ArrayList<CardState> rewardGroup;

    public CardRewardScreenState() {
        rewardGroup = new ArrayList<>();

        CardRewardScreen screen = AbstractDungeon.cardRewardScreen;

        screen.rewardGroup.forEach(card -> rewardGroup.add(new CardState(card)));

        rItem = screen.rItem;
        discovery = ReflectionHacks.getPrivate(screen, CardRewardScreen.class, "discovery");
        chooseOne = ReflectionHacks.getPrivate(screen, CardRewardScreen.class, "chooseOne");
        skippable = ReflectionHacks.getPrivate(screen, CardRewardScreen.class, "skippable");
        draft = ReflectionHacks.getPrivate(screen, CardRewardScreen.class, "draft");

        discoveryCard = screen.discoveryCard == null ? null : new CardState(screen.discoveryCard);

        AbstractCard screenTouchCard = ReflectionHacks
                .getPrivate(screen, CardRewardScreen.class, "touchCard");
        touchCard = screenTouchCard == null ? null : new CardState(screenTouchCard);

        // store the action state
        if (AbstractDungeon.actionManager.currentAction != null) {
            currentActionState = CurrentActionState.getCurrentActionState();
            actionQueue = ActionState.getActionQueueState();

            cardQueueState = new ArrayList<>();
            AbstractDungeon.actionManager.cardQueue.forEach(cardQueueItem -> cardQueueState
                    .add(new CardQueueItemState(cardQueueItem)));
        } else {
            currentActionState = null;
            actionQueue = null;
            cardQueueState = null;
        }
    }

    public void loadCardRewardScreen() {
        CardRewardScreen screen = AbstractDungeon.cardRewardScreen;

        screen.rItem = rItem;
        ReflectionHacks.setPrivate(screen, CardRewardScreen.class, "discovery", discovery);
        ReflectionHacks.setPrivate(screen, CardRewardScreen.class, "chooseOne", chooseOne);
        ReflectionHacks.setPrivate(screen, CardRewardScreen.class, "skippable", skippable);
        ReflectionHacks.setPrivate(screen, CardRewardScreen.class, "draft", draft);

        screen.discoveryCard = discoveryCard == null ? null : discoveryCard.loadCard();

        AbstractCard screenTouchCard = touchCard == null ? null : touchCard.loadCard();
        ReflectionHacks
                .setPrivate(screen, CardRewardScreen.class, "touchCard", screenTouchCard);

        if (currentActionState != null) {
            AbstractDungeon.actionManager.currentAction = currentActionState.loadCurrentAction();
            AbstractDungeon.actionManager.phase = GameActionManager.Phase.EXECUTING_ACTIONS;

            actionQueue.forEach(action -> AbstractDungeon.actionManager.actions.add(action
                    .loadAction()));

            AbstractDungeon.actionManager.cardQueue.clear();
            cardQueueState.forEach(cardQueueItemState -> AbstractDungeon.actionManager.cardQueue
                    .add(cardQueueItemState.loadItem()));
        }

        screen.rewardGroup.clear();
        rewardGroup.forEach(cardState -> screen.rewardGroup.add(cardState.loadCard()));
    }
}
