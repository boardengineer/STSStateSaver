package savestate.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ShuffleAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ShuffleActionState  implements ActionState{
    @Override
    public AbstractGameAction loadAction() {
        // TODO maybe its not always this trigger
        return new ShuffleAction(AbstractDungeon.player.drawPile, true);
    }
}
