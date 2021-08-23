package savestate.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;

public class PressEndTurnButtonActionState implements ActionState {
    @Override
    public AbstractGameAction loadAction() {
        return new PressEndTurnButtonAction();
    }
}
