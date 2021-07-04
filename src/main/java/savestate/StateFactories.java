package savestate;

import savestate.actions.Action;
import savestate.actions.ActionState;
import savestate.actions.CurrentAction;
import savestate.actions.CurrentActionState;
import savestate.monsters.Monster;
import savestate.orbs.Orb;
import savestate.powers.Power;
import savestate.powers.PowerState;
import savestate.relics.Relic;

import java.util.HashMap;

/**
 * This class contains maps to state factories.  Modded content can be included in the state saver
 * by adding keys and factories directly to these maps
 */
public class StateFactories {
    // TODO move inits to a main file
    public static HashMap<String, Monster> monsterByIdMap = createMonsterMap();
    public static HashMap<String, PowerState.PowerFactories> powerByIdMap = createPowerMap();
    public static HashMap<String, Relic> relicByIdMap = createRelicMap();
    public static HashMap<Class, ActionState.ActionFactories> actionByClassMap = createActionMap();
    public static HashMap<Class, CurrentActionState.CurrentActionFactories> currentActionByClassMap = createCurrentActionMap();
    public static HashMap<Class, Orb> orbByClassMap = createOrbMap();

    private static HashMap<String, Monster> createMonsterMap() {
        HashMap<String, Monster> monsterByIdmap = new HashMap<>();
        for (Monster monster : Monster.values()) {
            monsterByIdmap.put(monster.monsterId, monster);
        }
        return monsterByIdmap;
    }

    private static HashMap<String, PowerState.PowerFactories> createPowerMap() {
        HashMap<String, PowerState.PowerFactories> powerByIdmap = new HashMap<>();
        for (Power power : Power.values()) {
            powerByIdmap
                    .put(power.powerId, new PowerState.PowerFactories(power.factory, power.jsonFactory));
        }
        return powerByIdmap;
    }

    private static HashMap<String, Relic> createRelicMap() {
        HashMap<String, Relic> relicByIdMap = new HashMap<>();
        for (Relic relic : Relic.values()) {
            relicByIdMap.put(relic.relicId, relic);
        }
        return relicByIdMap;
    }

    private static HashMap<Class, ActionState.ActionFactories> createActionMap() {
        HashMap<Class, ActionState.ActionFactories> actionByClassMap = new HashMap<>();
        for (Action action : Action.values()) {
            actionByClassMap
                    .put(action.actionClass, new ActionState.ActionFactories(action.factory));
        }
        return actionByClassMap;
    }

    private static HashMap<Class, CurrentActionState.CurrentActionFactories> createCurrentActionMap() {
        HashMap<Class, CurrentActionState.CurrentActionFactories> currentActionByClassMap = new HashMap<>();
        for (CurrentAction action : CurrentAction.values()) {
            currentActionByClassMap
                    .put(action.actionClass, new CurrentActionState.CurrentActionFactories(action.factory));
        }
        return currentActionByClassMap;
    }

    private static HashMap<Class, Orb> createOrbMap() {
        HashMap<Class, Orb> orbByClassMap = new HashMap<>();
        for (Orb orb : Orb.values()) {
            orbByClassMap.put(orb.orbClass, orb);
        }
        return orbByClassMap;
    }
}
