package savestate;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;
import com.megacrit.cardcrawl.orbs.Lightning;
import com.megacrit.cardcrawl.powers.TheBombPower;
import savestate.actions.Action;
import savestate.actions.ActionState;
import savestate.actions.CurrentAction;
import savestate.actions.CurrentActionState;
import savestate.monsters.Monster;
import savestate.monsters.MonsterState;
import savestate.orbs.Orb;
import savestate.orbs.OrbState;
import savestate.powers.Power;
import savestate.powers.PowerState;
import savestate.relics.Relic;
import savestate.relics.RelicState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Function;

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

    public static HashMap<Class<? extends AbstractCard>, CardState.CardFactories> cardFactoriesByType = new HashMap<>();

    // DO NOT POPULATE MANUALLY
    public static HashMap<Class<? extends AbstractCard>, Function<AbstractCard, CardState>> dynamicCardFactoriesByType = new HashMap<>();

    public static HashMap<String, CardState.CardFactories> cardFactoriesByTypeName = new HashMap<>();
    public static HashMap<String, CardState.CardFactories> cardFactoriesByCardId = new HashMap<>();

    public static HashMap<String, StateElement.ElementFactories> elementFactories = new HashMap<>();

    public static HashMap<String, Integer> cardIdToIndexMap = new HashMap<>();
    public static String[] cardIds;

    public static HashMap<String, RelicState.RelicFactories> relicByIdMap = createRelicMap();
    public static HashMap<Class, ActionState.ActionFactories> actionByClassMap = createActionMap();
    public static HashMap<Class, CurrentActionState.CurrentActionFactories> currentActionByClassMap = createCurrentActionMap();
    public static HashMap<Class<? extends AbstractOrb>, OrbState.OrbFactories> orbByClassMap = createOrbMap();
    public static HashMap<String, Class<? extends AbstractOrb>> orbClassByName = createOrbClassNameMap();

    private static HashMap<String, Class<? extends AbstractOrb>> createOrbClassNameMap() {
        HashMap<String, Class<? extends AbstractOrb>> result = new HashMap<>();
        orbByClassMap.keySet().forEach(clazz -> result.put(clazz.getSimpleName(), clazz));

        // Hacky fix for intermediate format
        result.put("LightningOrbState", Lightning.class);
        result.put("EmptyOrbSlotState", EmptyOrbSlot.class);

        return result;
    }

    public static HashMap<String, AbstractCardModifierState.CardModifierStateFactories> cardModifierFactories = new HashMap<>();

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

    private static HashMap<Class<? extends AbstractOrb>, OrbState.OrbFactories> createOrbMap() {
        HashMap<Class<? extends AbstractOrb>, OrbState.OrbFactories> orbFactoryByClassMap = new HashMap<>();

        for (Orb orbEnum : Orb.values()) {
            orbFactoryByClassMap
                    .put(orbEnum.orbClass, new OrbState.OrbFactories(orbEnum.factory, orbEnum.jsonFactory));
        }
        return orbFactoryByClassMap;
    }
}
