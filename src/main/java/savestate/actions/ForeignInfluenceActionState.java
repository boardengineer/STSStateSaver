package savestate.actions;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.watcher.ForeignInfluenceAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ForeignInfluenceActionState implements CurrentActionState {
    private final boolean retrieveCard;
    private final boolean upgraded;

    public ForeignInfluenceActionState(AbstractGameAction action) {
        this((ForeignInfluenceAction) action);
    }

    public ForeignInfluenceActionState(ForeignInfluenceAction action) {
        retrieveCard = ReflectionHacks
                .getPrivate(action, ForeignInfluenceAction.class, "retrieveCard");
        upgraded = ReflectionHacks.getPrivate(action, ForeignInfluenceAction.class, "upgraded");
    }

    @Override
    public AbstractGameAction loadCurrentAction() {
        ForeignInfluenceAction result = new ForeignInfluenceAction(upgraded);

        ReflectionHacks
                .setPrivate(result, ForeignInfluenceAction.class, "retrieveCard", retrieveCard);

        // This should make the action only trigger the second half of the update
        ReflectionHacks
                .setPrivate(result, AbstractGameAction.class, "duration", 0);

        return result;
    }

    @SpirePatch(
            clz = ForeignInfluenceAction.class,
            paramtypez = {},
            method = "update"
    )
    public static class NoDoubleTriggerActionPatch {
        public static void Postfix(ForeignInfluenceAction _instance) {
            // Force the action to stay in the the manager until cards are selected
            if (AbstractDungeon.isScreenUp) {
                _instance.isDone = false;
            }
        }
    }
}
