package savestate.selectscreen;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
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

    private final boolean isDisabled;
    private final int cardSelectAmount;
    private final int numCards;

    public GridCardSelectScreenState() {
        ArrayList<AbstractCard> allCards = new ArrayList<>();

        AbstractPlayer player = AbstractDungeon.player;

        allCards.addAll(player.masterDeck.group);
        allCards.addAll(player.drawPile.group);
        allCards.addAll(player.hand.group);
        allCards.addAll(player.discardPile.group);
        allCards.addAll(player.exhaustPile.group);
        allCards.addAll(player.limbo.group);

        this.selectedCards = new ArrayList<>();
        AbstractDungeon.gridSelectScreen.selectedCards
                .forEach(card -> this.selectedCards.add(SaveState.CardStateContainer
                        .forCard(card, allCards)));

        this.isDiscard = AbstractDungeon.gridSelectScreen.targetGroup.type == CardGroup.CardGroupType.DISCARD_PILE;
        this.groupCards = new ArrayList<>();
        AbstractDungeon.gridSelectScreen.targetGroup.group
                .forEach(card -> groupCards
                        .add(SaveState.CardStateContainer.forCard(card, allCards)));

        this.isDisabled = AbstractDungeon.gridSelectScreen.confirmButton.isDisabled;

        this.cardSelectAmount = ReflectionHacks
                .getPrivate(AbstractDungeon.gridSelectScreen, GridCardSelectScreen.class, "cardSelectAmount");
        this.numCards = ReflectionHacks
                .getPrivate(AbstractDungeon.gridSelectScreen, GridCardSelectScreen.class, "numCards");

        if (AbstractDungeon.actionManager.currentAction != null) {
            currentActionState = CurrentActionState.getCurrentActionState();
            actionQueue = ActionState.getActionQueueState();

            if (actionQueue.isEmpty()) {
                throw new IllegalStateException("The action queue shouldn't be empty in the middle of a selection screen");
            }
        } else {
            currentActionState = null;
            actionQueue = null;
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

        AbstractDungeon.gridSelectScreen.selectedCards = new ArrayList<>();
        selectedCards.forEach(cardStateContainer -> AbstractDungeon.gridSelectScreen.selectedCards
                .add(cardStateContainer.loadCard(allCards)));

        if (currentActionState != null) {
            AbstractDungeon.actionManager.currentAction = currentActionState.loadCurrentAction();
            AbstractDungeon.actionManager.phase = GameActionManager.Phase.EXECUTING_ACTIONS;

            actionQueue.forEach(action -> AbstractDungeon.actionManager.actions.add(action
                    .loadAction()));

            if (AbstractDungeon.actionManager.actions.isEmpty()) {
                throw new IllegalStateException("this too shouldn't happen");
            }
        }

        if (isDiscard) {
            AbstractDungeon.gridSelectScreen.targetGroup = AbstractDungeon.player.discardPile;
        } else {
            AbstractDungeon.gridSelectScreen.targetGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            this.groupCards
                    .forEach(cardStateContainer -> AbstractDungeon.gridSelectScreen.targetGroup
                            .addToTop(cardStateContainer.loadCard(allCards)));
        }

        AbstractDungeon.gridSelectScreen.confirmButton.isDisabled = this.isDisabled;

        ReflectionHacks
                .setPrivate(AbstractDungeon.gridSelectScreen, GridCardSelectScreen.class, "cardSelectAmount", cardSelectAmount);

        ReflectionHacks
                .setPrivate(AbstractDungeon.gridSelectScreen, GridCardSelectScreen.class, "numCards", numCards);
    }
}
