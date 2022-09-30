package savestate.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class LoseHPActionState implements ActionState {
    private final int amount;
    private final int targetIndex;

    public LoseHPActionState(AbstractGameAction action) {
        this((LoseHPAction) action);
    }

    public LoseHPActionState(LoseHPAction action) {
        this.amount = action.amount;
        this.targetIndex = ActionState.indexForCreature(action.target);
    }

    @Override
    public AbstractGameAction loadAction() {
        return new LoseHPAction(ActionState.creatureForIndex(targetIndex), AbstractDungeon.player, amount);
    }
}
