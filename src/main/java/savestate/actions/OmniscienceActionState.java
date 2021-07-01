package savestate.actions;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.watcher.OmniscienceAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class OmniscienceActionState implements CurrentActionState {
    private final int playAmt;

    public OmniscienceActionState(AbstractGameAction action) {
        this.playAmt = ReflectionHacks.getPrivate(action, OmniscienceAction.class, "playAmt");
    }

    @Override
    public AbstractGameAction loadCurrentAction() {
        OmniscienceAction result = new OmniscienceAction(playAmt);

        // This should make the action only trigger the second half of the update
        ReflectionHacks
                .setPrivate(result, AbstractGameAction.class, "duration", 0);

        return result;
    }

    @SpirePatch(
            clz = OmniscienceAction.class,
            paramtypez = {},
            method = "update"
    )
    public static class NoOmniscienceActionPatch {
        public static void Postfix(OmniscienceAction _instance) {
            // Force the action to stay in the the manager until cards are selected
            if (AbstractDungeon.gridSelectScreen.selectedCards
                    .isEmpty() && AbstractDungeon.isScreenUp) {
                _instance.isDone = false;
            }
        }
    }
}
