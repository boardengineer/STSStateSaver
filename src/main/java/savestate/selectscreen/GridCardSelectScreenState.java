package savestate.selectscreen;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import savestate.CardQueueItemState;
import savestate.SaveState;
import savestate.actions.ActionState;
import savestate.actions.CurrentActionState;

import java.util.ArrayList;

public class GridCardSelectScreenState {
    private final ArrayList<SaveState.CardStateContainer> selectedCards;

    private final CurrentActionState currentActionState;
    private final ArrayList<ActionState> actionQueue;

    // TODO this will probably need to be turned into a State object
    private final boolean isDiscard;
    private final ArrayList<SaveState.CardStateContainer> groupCards;
    private final ArrayList<CardQueueItemState> cardQueueState;

    private final boolean isConfirmButtonDisabled;
    private final int cardSelectAmount;
    private final int numCards;
    private final boolean anyNumber;
    private final boolean forClarity;

    private final boolean forUpgrade;
    private final boolean forTransform;
    private final boolean canCancel;
    private final boolean forPurge;

    public GridCardSelectScreenState() {
        ArrayList<AbstractCard> allCards = new ArrayList<>();

        AbstractPlayer player = AbstractDungeon.player;

        allCards.addAll(player.masterDeck.group);
        allCards.addAll(player.drawPile.group);
        allCards.addAll(player.hand.group);
        allCards.addAll(player.discardPile.group);
        allCards.addAll(player.exhaustPile.group);
        allCards.addAll(player.limbo.group);

        GridCardSelectScreen screen = AbstractDungeon.gridSelectScreen;
        this.selectedCards = new ArrayList<>();
        screen.selectedCards
                .forEach(card -> this.selectedCards.add(SaveState.CardStateContainer
                        .forCard(card, allCards)));

        this.isDiscard = screen.targetGroup.type == CardGroup.CardGroupType.DISCARD_PILE;
        this.groupCards = new ArrayList<>();
        screen.targetGroup.group
                .forEach(card -> groupCards
                        .add(SaveState.CardStateContainer.forCard(card, allCards)));

        this.isConfirmButtonDisabled = screen.confirmButton.isDisabled;

        this.cardSelectAmount = ReflectionHacks
                .getPrivate(screen, GridCardSelectScreen.class, "cardSelectAmount");
        this.numCards = ReflectionHacks
                .getPrivate(screen, GridCardSelectScreen.class, "numCards");
        this.forUpgrade = screen.forUpgrade;

        this.forTransform = screen.forTransform;
        this.anyNumber = screen.anyNumber;
        this.forClarity = screen.forClarity;
        this.forPurge = screen.forPurge;

        this.canCancel = ReflectionHacks
                .getPrivate(screen, GridCardSelectScreen.class, "canCancel");

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

    public void loadGridSelectScreen() {
        ArrayList<AbstractCard> allCards = new ArrayList<>();

        AbstractPlayer player = AbstractDungeon.player;

        allCards.addAll(player.masterDeck.group);
        allCards.addAll(player.drawPile.group);
        allCards.addAll(player.hand.group);
        allCards.addAll(player.discardPile.group);
        allCards.addAll(player.exhaustPile.group);
        allCards.addAll(player.limbo.group);

        GridCardSelectScreen screen = AbstractDungeon.gridSelectScreen;
        screen.selectedCards = new ArrayList<>();
        selectedCards.forEach(cardStateContainer -> {
            AbstractCard card = cardStateContainer.loadCard(allCards);
            screen.selectedCards
                    .add(card);
            card.isGlowing = true;
        });

        if (currentActionState != null) {
            AbstractDungeon.actionManager.currentAction = currentActionState.loadCurrentAction();
            AbstractDungeon.actionManager.phase = GameActionManager.Phase.EXECUTING_ACTIONS;

            actionQueue.forEach(action -> AbstractDungeon.actionManager.actions.add(action
                    .loadAction()));
            AbstractDungeon.actionManager.cardQueue.clear();
            cardQueueState.forEach(cardQueueItemState -> AbstractDungeon.actionManager.cardQueue
                    .add(cardQueueItemState.loadItem()));
        }

        if (isDiscard) {
            screen.targetGroup = AbstractDungeon.player.discardPile;
        } else {
            screen.targetGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            this.groupCards
                    .forEach(cardStateContainer -> screen.targetGroup
                            .addToTop(cardStateContainer.loadCard(allCards)));
        }

        screen.confirmButton.isDisabled = this.isConfirmButtonDisabled;
        screen.forUpgrade = forUpgrade;
        screen.forPurge = forPurge;
        screen.forTransform = forTransform;
        screen.anyNumber = anyNumber;
        screen.forClarity = forClarity;

        ReflectionHacks
                .setPrivate(screen, GridCardSelectScreen.class, "canCancel", canCancel);

        ReflectionHacks
                .setPrivate(screen, GridCardSelectScreen.class, "cardSelectAmount", cardSelectAmount);

        ReflectionHacks.setPrivate(screen, GridCardSelectScreen.class, "numCards", numCards);
    }
}
