package savestate.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MonsterStartTurnAction;

public class MonsterStartTurnActionState implements ActionState {
    @Override
    public AbstractGameAction loadAction() {
        return new MonsterStartTurnAction();
    }
}
