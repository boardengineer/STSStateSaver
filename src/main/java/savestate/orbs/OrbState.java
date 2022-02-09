package savestate.orbs;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import savestate.StateFactories;

import java.util.function.Function;

public abstract class OrbState {
    public final int evokeAmount;
    public final int passiveAmount;
    public final String lookupKey;

    public OrbState(AbstractOrb orb) {
        this.evokeAmount = orb.evokeAmount;
        this.passiveAmount = orb.passiveAmount;
        this.lookupKey = orb.getClass().getSimpleName();
    }

    public OrbState(String jsonString) {
        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();

        this.evokeAmount = parsed.get("evoke_amount").getAsInt();
        this.passiveAmount = parsed.get("passive_amount").getAsInt();
        this.lookupKey = parsed.get("lookup_key").getAsString();
    }

    public String encode() {
        JsonObject result = new JsonObject();

        result.addProperty("evoke_amount", evokeAmount);
        result.addProperty("passive_amount", passiveAmount);
        result.addProperty("lookup_key", lookupKey);

        return result.toString();

    }

    public abstract AbstractOrb loadOrb();

    public static OrbState forOrb(AbstractOrb orb) {
        return StateFactories.orbByClassMap.get(orb.getClass().getSimpleName()).factory.apply(orb);
    }

    public static OrbState forJsonString(String jsonString) {
        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();

        String lookupKey = parsed.get("lookup_key").getAsString();

        return StateFactories.orbByClassMap.get(lookupKey).jsonFactory.apply(jsonString);
    }

    public static class OrbFactories {
        public Function<AbstractOrb, OrbState> factory;
        public Function<String, OrbState> jsonFactory;

        public OrbFactories(Function<AbstractOrb, OrbState> factory, Function<String, OrbState> jsonFactory) {
            this.factory = factory;
            this.jsonFactory = jsonFactory;
        }
    }
}
