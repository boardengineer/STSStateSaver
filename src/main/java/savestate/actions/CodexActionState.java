package savestate.actions;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;

import com.megacrit.cardcrawl.actions.unique.CodexAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class CodexActionState implements CurrentActionState {
    private final boolean retrieveCard;

    public CodexActionState(AbstractGameAction action) {
        this((CodexAction) action);
    }

    public CodexActionState(CodexAction action) {
        retrieveCard = ReflectionHacks
                .getPrivate(action, CodexAction.class, "retrieveCard");
    }

    @Override
    public AbstractGameAction loadCurrentAction() {
        CodexAction result = new CodexAction();

        ReflectionHacks
                .setPrivate(result, CodexAction.class, "retrieveCard", retrieveCard);

        // This should make the action only trigger the second half of the update
        ReflectionHacks
                .setPrivate(result, AbstractGameAction.class, "duration", 0);

        return result;
    }

    @SpirePatch(
            clz = CodexAction.class,
            paramtypez = {},
            method = "update"
    )
    public static class NoDoubleTriggerActionPatch {
        public static void Postfix(CodexAction _instance) {
            // Force the action to stay in the the manager until cards are selected
            if (AbstractDungeon.isScreenUp) {
                _instance.isDone = false;
            }
        }
    }
}
