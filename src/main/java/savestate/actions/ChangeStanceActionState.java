package savestate.actions;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;

public class ChangeStanceActionState implements ActionState {
    private final String id;

    public ChangeStanceActionState(AbstractGameAction action) {
        this.id = ReflectionHacks.getPrivate(action, ChangeStanceAction.class, "id");
    }

    @Override
    public AbstractGameAction loadAction() {
        return new ChangeStanceAction(id);
    }
}
