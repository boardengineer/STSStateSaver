package savestate.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.watcher.TriggerMarksAction;

public class TriggerMarksActionState implements ActionState {
    @Override
    public AbstractGameAction loadAction() {
        return new TriggerMarksAction(null);
    }
}
