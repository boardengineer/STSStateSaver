package savestate.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.EndTurnAction;
import com.megacrit.cardcrawl.actions.common.MonsterStartTurnAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class EnqueueEndTurnAction extends AbstractGameAction {
    @Override
    public void update() {
        this.addToBot(new EndTurnAction());

        if (!AbstractDungeon.getCurrRoom().skipMonsterTurn) {
            this.addToBot(new MonsterStartTurnAction());
        }

        AbstractDungeon.actionManager.monsterAttacksQueued = false;
        this.isDone = true;
    }
}