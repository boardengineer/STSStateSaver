package savestate;

import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import savestate.actions.ActionState;

import java.util.Optional;

public class CardQueueItemState {
    public final CardState card;
    public final Optional<Integer> monsterIndex;
    public final int energyOnUse;
    public final boolean ignoreEnergyTotal;
    public final boolean autoplayCard;
    public final boolean randomTarget;
    public final boolean isEndTurnAutoPlay;

    public CardQueueItemState(CardQueueItem cardQueueItem) {
        this.card = cardQueueItem.card == null ? null : new CardState(cardQueueItem.card);
        this.monsterIndex = cardQueueItem.monster == null ? Optional.empty() : Optional
                .of(ActionState.indexForCreature(cardQueueItem.monster));
        this.energyOnUse = cardQueueItem.energyOnUse;
        this.ignoreEnergyTotal = cardQueueItem.ignoreEnergyTotal;
        this.autoplayCard = cardQueueItem.autoplayCard;
        this.randomTarget = cardQueueItem.randomTarget;
        this.isEndTurnAutoPlay = cardQueueItem.isEndTurnAutoPlay;
    }

    public CardQueueItem loadItem() {
        CardQueueItem result = new CardQueueItem(card == null ? null : card
                .loadCard(), (AbstractMonster) (monsterIndex
                .isPresent() ? ActionState
                .creatureForIndex(monsterIndex
                        .get()) : null), energyOnUse, ignoreEnergyTotal, autoplayCard);
        result.randomTarget = randomTarget;
        return result;
    }
}
