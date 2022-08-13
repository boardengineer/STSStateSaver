package savestate.powers;

import savestate.StateFactories;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.function.Function;

import static savestate.SaveStateMod.addRuntime;

public class PowerState {
    public static boolean IGNORE_MISSING_POWERS = false;

    public final String powerId;
    public final int amount;
    protected JsonObject powerJson = null;

    public PowerState(AbstractPower power) {
        this.powerId = power.ID;
        this.amount = power.amount;
    }

    public PowerState(String jsonString) {
        powerJson = new JsonParser().parse(jsonString).getAsJsonObject();

        this.powerId = powerJson.get("power_id").getAsString();
        this.amount = powerJson.get("amount").getAsInt();
    }

    public PowerState(JsonObject powerJson) {
        this.powerJson = powerJson;

        this.powerId = powerJson.get("power_id").getAsString();
        this.amount = powerJson.get("amount").getAsInt();
    }

    public AbstractPower loadPower(AbstractCreature targetAndSource) {
        long start = System.currentTimeMillis();

        if (StateFactories.powerByIdMap.containsKey(powerId)) {
            AbstractPower result = StateFactories.powerByIdMap.get(powerId).factory
                    .apply(new DummyPower(powerId, amount)).loadPower(targetAndSource);

            addRuntime("power" + powerId, System.currentTimeMillis() - start);

            return result;
        }

        throw new IllegalStateException("no known state for " + powerId);
    }

    public String encode() {
        return jsonEncode().toString();
    }

    public JsonObject jsonEncode() {
        if (powerJson == null) {
            powerJson = new JsonObject();
        }

        powerJson.addProperty("power_id", powerId);
        powerJson.addProperty("amount", amount);

        return powerJson;
    }

    public String diffEncode() {
        if (powerJson == null)
            powerJson = new JsonObject();

        powerJson.addProperty("power_id", powerId);
        powerJson.addProperty("amount", amount);

        return powerJson.toString();
    }

    // A generic empty power so that power factories can be used for basic json powers
    private class DummyPower extends AbstractPower {
        DummyPower(String powerId, int amount) {
            this.ID = powerId;
            this.amount = amount;
        }
    }

    public static PowerState forPower(AbstractPower power) {
        String id = power.ID;

        for (String prefix : StateFactories.powerPrefixes) {
            if (id.startsWith(prefix)) {
                id = prefix;
                break;
            }
        }

        if (!StateFactories.powerByIdMap.containsKey(id)) {
            throw new IllegalStateException("No Power State for " + id);
        }
        return StateFactories.powerByIdMap.get(id).factory.apply(power);
    }


    public static PowerState forJsonString(String jsonString) {
        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();

        String id = parsed.get("power_id").getAsString();

        for (String prefix : StateFactories.powerPrefixes) {
            if (id.startsWith(prefix)) {
                id = prefix;
                break;
            }
        }

        if (!StateFactories.powerByIdMap.containsKey(id)) {
            if (IGNORE_MISSING_POWERS) {
                return null;
            } else {
                throw new IllegalStateException("No Power State for " + id);
            }
        }
        return StateFactories.powerByIdMap.get(id).jsonFactory.apply(jsonString);
    }

    public static PowerState forJsonObject(JsonObject powerJson) {
        String id = powerJson.get("power_id").getAsString();

        for (String prefix : StateFactories.powerPrefixes) {
            if (id.startsWith(prefix)) {
                id = prefix;
                break;
            }
        }

        if (!StateFactories.powerByIdMap.containsKey(id)) {
            if (IGNORE_MISSING_POWERS) {
                return null;
            } else {
                throw new IllegalStateException("No Power State for " + id);
            }
        }
        return StateFactories.powerByIdMap.get(id).jsonObjectFactory.apply(powerJson);
    }

    public static class PowerFactories {
        public final Function<AbstractPower, PowerState> factory;
        public final Function<String, PowerState> jsonFactory;
        public final Function<JsonObject, PowerState> jsonObjectFactory;

        public PowerFactories(Function<AbstractPower, PowerState> factory,
                              Function<String, PowerState> jsonFactory,
                              Function<JsonObject, PowerState> jsonObjectFactory) {
            this.jsonObjectFactory = jsonObjectFactory;
            this.factory = factory;
            this.jsonFactory = jsonFactory;
        }

//        public PowerFactories(Function<AbstractPower, PowerState> factory,
//                              Function<String, PowerState> jsonFactory) {
//            this.factory = factory;
//            this.jsonFactory = jsonFactory;
//        }

        public PowerFactories(Function<AbstractPower, PowerState> factory) {
            this.factory = factory;
            this.jsonFactory = json -> new PowerState(json);
            this.jsonObjectFactory = jsonObject -> new PowerState(jsonObject);
        }
    }
}
