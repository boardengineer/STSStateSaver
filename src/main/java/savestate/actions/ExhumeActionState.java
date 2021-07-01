package savestate.actions;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.ExhumeAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import savestate.CardState;

import java.util.ArrayList;

public class ExhumeActionState implements CurrentActionState {
    private final ArrayList<CardState> exhumes;
    private final boolean upgrade;

    ExhumeActionState(AbstractGameAction action) {
        this.upgrade = ReflectionHacks.getPrivate(action, ExhumeAction.class, "upgrade");

        this.exhumes = new ArrayList<>();
        ArrayList<AbstractCard> actionExhumes = ReflectionHacks.getPrivate(action, ExhumeAction.class, "exhumes");
        actionExhumes.forEach(card -> exhumes.add(new CardState(card)));
    }

    @Override
    public AbstractGameAction loadCurrentAction() {
        ExhumeAction result = new ExhumeAction(upgrade);
        ArrayList<AbstractCard> actionExhumes = new ArrayList<>();
        exhumes.forEach(cardState -> actionExhumes.add(cardState.loadCard()));
        ReflectionHacks.setPrivate(result, ExhumeAction.class, "exhumes", actionExhumes);

        // This should make the action only trigger the second half of the update
        ReflectionHacks
                .setPrivate(result, AbstractGameAction.class, "duration", 0);

        return result;
    }

    @SpirePatch(
            clz = ExhumeAction.class,
            paramtypez = {},
            method = "update"
    )
    public static class NoDoubleExhumePatch {
        public static void Postfix(ExhumeAction _instance) {
            // Force the action to stay in the the manager until cards are selected
            if (AbstractDungeon.gridSelectScreen.selectedCards
                    .isEmpty() && AbstractDungeon.isScreenUp) {
                _instance.isDone = false;
            }
        }
    }
}
