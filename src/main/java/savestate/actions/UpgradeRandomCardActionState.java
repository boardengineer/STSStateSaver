package savestate.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.UpgradeRandomCardAction;

public class UpgradeRandomCardActionState implements ActionState {
    @Override
    public AbstractGameAction loadAction() {
        return new UpgradeRandomCardAction();
    }
}
