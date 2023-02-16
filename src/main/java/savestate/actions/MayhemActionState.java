package savestate.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import savestate.fastobjects.MayhemAction;

public class MayhemActionState implements ActionState {
    @Override
    public AbstractGameAction loadAction() {
        return new MayhemAction();
    }
}
