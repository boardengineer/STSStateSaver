package savestate;

import com.megacrit.cardcrawl.powers.TheBombPower;
import savestate.actions.Action;
import savestate.actions.ActionState;
import savestate.actions.CurrentAction;
import savestate.actions.CurrentActionState;
import savestate.monsters.Monster;
import savestate.monsters.MonsterState;
import savestate.orbs.Orb;
import savestate.powers.Power;
import savestate.powers.PowerState;
import savestate.relics.Relic;
import savestate.relics.RelicState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class contains maps to state factories.  Modded content can be included in the state saver
 * by adding keys and factories directly to these maps
 */
public class StateFactories {
    // TODO move inits to a main file
    public static HashMap<String, MonsterState.MonsterFactories> monsterByIdMap = createMonsterMap();

    // Power factories and a set of power names
    public static HashMap<String, PowerState.PowerFactories> powerByIdMap = createPowerMap();
    public static HashSet<String> powerPrefixes = createPowerPrefixes();

    public static ArrayList<CardState.CardFactories> cardFactories = new ArrayList<>();
    public static HashMap<String, StateElement.ElementFactories> elementFactories = new HashMap<>();

    public static HashMap<String, RelicState.RelicFactories> relicByIdMap = createRelicMap();
    public static HashMap<Class, ActionState.ActionFactories> actionByClassMap = createActionMap();
    public static HashMap<Class, CurrentActionState.CurrentActionFactories> currentActionByClassMap = createCurrentActionMap();
    public static HashMap<Class, Orb> orbByClassMap = createOrbMap();

    private static HashMap<String, MonsterState.MonsterFactories> createMonsterMap() {
        HashMap<String, MonsterState.MonsterFactories> monsterByIdmap = new HashMap<>();
        for (Monster monster : Monster.values()) {
            monsterByIdmap
                    .put(monster.monsterId, new MonsterState.MonsterFactories(monster.factory, monster.jsonFactory));
        }
        return monsterByIdmap;
    }

    private static HashSet<String> createPowerPrefixes() {
        HashSet<String> powerPrefixes = new HashSet<>();

        powerPrefixes.add(TheBombPower.POWER_ID);

        return powerPrefixes;
    }

    private static HashMap<String, PowerState.PowerFactories> createPowerMap() {
        HashMap<String, PowerState.PowerFactories> powerByIdmap = new HashMap<>();
        for (Power power : Power.values()) {
            powerByIdmap
                    .put(power.powerId, new PowerState.PowerFactories(power.factory, power.jsonFactory));
        }
        return powerByIdmap;
    }

    private static HashMap<String, RelicState.RelicFactories> createRelicMap() {
        HashMap<String, RelicState.RelicFactories> relicByIdMap = new HashMap<>();
        for (Relic relic : Relic.values()) {
            relicByIdMap
                    .put(relic.relicId, new RelicState.RelicFactories(relic.factory, relic.jsonFactory));
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
