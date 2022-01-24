package savestate.actions;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import savestate.CardState;

import java.util.ArrayList;

public class ChooseOneActionState implements CurrentActionState {
    private final ArrayList<CardState> choices;

    ChooseOneActionState(AbstractGameAction action) {
        this((ChooseOneAction) action);
    }

    ChooseOneActionState(ChooseOneAction action) {
        ArrayList<CardState> choicesBuilder = new ArrayList();
        ArrayList<AbstractCard> actionChoices = ReflectionHacks
                .getPrivate(action, ChooseOneAction.class, "choices");
        actionChoices.forEach(card -> choicesBuilder.add(new CardState(card)));
        choices = choicesBuilder;
    }

    @Override
    public AbstractGameAction loadCurrentAction() {
        ArrayList<AbstractCard> actionChoices = new ArrayList<>();
        choices.forEach(cardState -> actionChoices.add(cardState.loadCard()));
        ChooseOneAction result = new ChooseOneAction(actionChoices);

        // This should make the action only trigger the second half of the update
        ReflectionHacks
                .setPrivate(result, AbstractGameAction.class, "duration", 0);

        return result;
    }

    @SpirePatch(
            clz = ChooseOneAction.class,
            paramtypez = {},
            method = "update"
    )
    public static class NoDoubleTriggerActionPatch {
        public static void Postfix(ChooseOneAction _instance) {
            // Force the action to stay in the the manager until cards are selected
            if (AbstractDungeon.isScreenUp) {
                _instance.isDone = false;
            }
        }
    }
}
