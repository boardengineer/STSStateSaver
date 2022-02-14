package savestate;

import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import savestate.actions.ActionState;

public class CardQueueItemState {
    public final CardState card;
    public final int monsterIndex;
    public final int energyOnUse;
    public final boolean ignoreEnergyTotal;
    public final boolean autoplayCard;
    public final boolean randomTarget;
    public final boolean isEndTurnAutoPlay;

    public CardQueueItemState(CardQueueItem cardQueueItem) {
        this.card = new CardState(cardQueueItem.card);
        this.monsterIndex = ActionState.indexForCreature(cardQueueItem.monster);
        this.energyOnUse = cardQueueItem.energyOnUse;
        this.ignoreEnergyTotal = cardQueueItem.ignoreEnergyTotal;
        this.autoplayCard = cardQueueItem.autoplayCard;
        this.randomTarget = cardQueueItem.randomTarget;
        this.isEndTurnAutoPlay = cardQueueItem.isEndTurnAutoPlay;
    }

    public CardQueueItem loadItem() {
        return new CardQueueItem(card.loadCard(), (AbstractMonster) ActionState
                .creatureForIndex(monsterIndex), energyOnUse, ignoreEnergyTotal, autoplayCard);
    }
}
