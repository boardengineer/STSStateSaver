package savestate.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;

// TODO this may be too simple and that may be a problem
public class NewQueueCardActionState implements ActionState {
    @Override
    public AbstractGameAction loadAction() {
        System.err.println("this is happening");
        return new NewQueueCardAction();
    }
}
