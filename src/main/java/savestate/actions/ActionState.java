package savestate.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.ShoutAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.beyond.SpireGrowth;
import savestate.StateFactories;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface ActionState {
    int PLAYER_INDEX = -1;
    int NULL_INDEX = -2;

    AbstractGameAction loadAction();

    static int indexForCreature(AbstractCreature creature) {
        if (creature == null) {
            return NULL_INDEX;
        }

        if (creature.isPlayer) {
            return PLAYER_INDEX;
        } else {
            int foundIndex = -1;
            for (int i = 0; i < AbstractDungeon.getMonsters().monsters.size(); i++) {
                if (AbstractDungeon.getMonsters().monsters.get(i) == creature) {
                    foundIndex = i;
                    break;
                }
            }

            if (foundIndex == -1) {
                // Hack for SpireGrowth
                if (creature instanceof SpireGrowth) {
                    return 0;
                }

                throw new IllegalStateException("No Target for " + creature + " " + AbstractDungeon
                        .getMonsters().monsters);
            } else {
                return foundIndex;
            }
        }
    }

    static AbstractCreature creatureForIndex(int index) {
        if (index == NULL_INDEX) {
            return null;
        }

        return index == PLAYER_INDEX ? AbstractDungeon.player : AbstractDungeon
                .getMonsters().monsters.get(index);
    }

    Set<Class<? extends AbstractGameAction>> IGNORED_ACTIONS = new HashSet<Class<? extends AbstractGameAction>>() {{
        add(VFXAction.class);
        add(ShoutAction.class);
        add(TalkAction.class);
        add(TextAboveCreatureAction.class);
        add(SFXAction.class);
        add(RelicAboveCreatureAction.class);
        add(WaitAction.class);
    }};

    static ArrayList<ActionState> toActionStateArray(ArrayList<AbstractGameAction> actions) {
        ArrayList<ActionState> result = new ArrayList<>();

        for (AbstractGameAction action : actions) {
            if (StateFactories.actionByClassMap.containsKey(action.getClass())) {
                result.add(StateFactories.actionByClassMap.get(action.getClass()).factory
                        .apply(action));
            } else if (ActionState.IGNORED_ACTIONS.contains(action.getClass())) {
                // These are visual effects that are not worth encoding
            } else {
                throw new IllegalArgumentException("Unknown action type found in action manager: " + action);
            }
        }

        return result;
    }

    static ArrayList<AbstractGameAction> toGameActions(ArrayList<ActionState> actionStates) {
        return actionStates.stream().map(ActionState::loadAction)
                           .collect(Collectors.toCollection(ArrayList::new));
    }

    static ArrayList<ActionState> getActionQueueState() {
        return toActionStateArray(AbstractDungeon.actionManager.actions);
    }

    class ActionFactories {
        public Function<AbstractGameAction, ActionState> factory;

        public ActionFactories(Function<AbstractGameAction, ActionState> factory) {
            this.factory = factory;
        }
    }
}
