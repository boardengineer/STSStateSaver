package savestate.actions;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.watcher.MeditateAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class MeditateActionState implements CurrentActionState {
    private final int numberOfCards;

    public MeditateActionState(AbstractGameAction action) {
        this.numberOfCards = ReflectionHacks
                .getPrivate(action, MeditateAction.class, "numberOfCards");
    }

    @Override
    public AbstractGameAction loadCurrentAction() {
        MeditateAction result = new MeditateAction(numberOfCards);

        // This should make the action only trigger the second half of the update
        ReflectionHacks
                .setPrivate(result, AbstractGameAction.class, "duration", 0);

        return result;
    }

    @SpirePatch(
            clz = MeditateAction.class,
            paramtypez = {},
            method = "update"
    )
    public static class NoDoubleScryPatch {
        public static void Postfix(MeditateAction _instance) {
            // Force the action to stay in the the manager until cards are selected
            if (AbstractDungeon.gridSelectScreen.selectedCards
                    .isEmpty() && AbstractDungeon.isScreenUp) {
                _instance.isDone = false;
            }
        }
    }
}
