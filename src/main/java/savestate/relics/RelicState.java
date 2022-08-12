package savestate.relics;

import basemod.ReflectionHacks;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import savestate.StateFactories;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static savestate.SaveStateMod.shouldGoFast;

public class RelicState {
    public final String relicId;
    public final int counter;
    private final boolean grayscale;
    private final boolean pulse;

    private static HashMap<String, LinkedList<AbstractRelic>> freeRelics;

    public static void resetFreeRelics() {
        freeRelics = new HashMap<>();
    }

    public static void freeRelicList(List<AbstractRelic> relics) {
        relics.forEach(RelicState::freeRelic);
    }

    public static void freeRelic(AbstractRelic relic) {
        if (relic == null) {
            return;
        }

        if (freeRelics == null) {
            freeRelics = new HashMap<>();
        }

        if (!freeRelics.containsKey(relic.relicId)) {
            freeRelics.put(relic.relicId, new LinkedList<>());
        }

        LinkedList<AbstractRelic> set = freeRelics.get(relic.relicId);
        if (set.size() > 100) {
            return;
        }

        set.add(relic);
    }

    private static Optional<AbstractRelic> getCachedRelic(String key) {
        if (freeRelics == null || !freeRelics.containsKey(key) || freeRelics.get(key).isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(freeRelics.get(key).poll());
    }

    public static AbstractRelic getRelic(String key) {
        Optional<AbstractRelic> resultOptional = getCachedRelic(key);

        AbstractRelic result;
        if (resultOptional.isPresent() && shouldGoFast) {
            result = resultOptional.get();
        } else {
            result = getFreshRelic(key);
        }

        return result;
    }

    private static AbstractRelic getFreshRelic(String key) {
        return RelicLibrary.getRelic(key).makeCopy();
    }

    public RelicState(AbstractRelic relic) {
        this.relicId = relic.relicId;
        this.counter = relic.counter;
        this.grayscale = relic.grayscale;
        this.pulse = ReflectionHacks.getPrivate(relic, AbstractRelic.class, "pulse");
    }

    public RelicState(String jsonString) {
        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();

        this.relicId = parsed.get("relic_id").getAsString();
        this.counter = parsed.get("counter").getAsInt();
        this.grayscale = parsed.get("grayscale").getAsBoolean();
        this.pulse = parsed.get("pulse").getAsBoolean();
    }

    public RelicState(JsonObject relicJson) {
        this.relicId = relicJson.get("relic_id").getAsString();
        this.counter = relicJson.get("counter").getAsInt();
        this.grayscale = relicJson.get("grayscale").getAsBoolean();
        this.pulse = relicJson.get("pulse").getAsBoolean();
    }

    public AbstractRelic loadRelic() {
        AbstractRelic result;

        result = getRelic(relicId);

        result.counter = counter;
        result.grayscale = grayscale;

        ReflectionHacks.setPrivate(result, AbstractRelic.class, "pulse", pulse);

        return result;
    }

    public String encode() {
        JsonObject relicStateJson = new JsonObject();

        relicStateJson.addProperty("relic_id", relicId);
        relicStateJson.addProperty("counter", counter);
        relicStateJson.addProperty("grayscale", grayscale);
        relicStateJson.addProperty("pulse", pulse);

        return relicStateJson.toString();
    }

    public JsonObject jsonEncode() {
        JsonObject relicStateJson = new JsonObject();

        relicStateJson.addProperty("relic_id", relicId);
        relicStateJson.addProperty("counter", counter);
        relicStateJson.addProperty("grayscale", grayscale);
        relicStateJson.addProperty("pulse", pulse);

        return relicStateJson;
    }

    public static RelicState forRelic(AbstractRelic relic) {
        if (StateFactories.relicByIdMap.containsKey(relic.relicId)) {
            return StateFactories.relicByIdMap.get(relic.relicId).factory.apply(relic);
        }

        return new RelicState(relic);
    }

    public static RelicState forJsonString(String jsonString) {
        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();

        String relicId = parsed.get("relic_id").getAsString();
        if (StateFactories.relicByIdMap.containsKey(relicId)) {
            return StateFactories.relicByIdMap.get(relicId).jsonFactory.apply(jsonString);
        }

        return new RelicState(jsonString);
    }

    public static RelicState forJsonObject(JsonObject relicJson) {

        String relicId = relicJson.get("relic_id").getAsString();
        if (StateFactories.relicByIdMap.containsKey(relicId)) {
            return StateFactories.relicByIdMap.get(relicId).jsonObjectFactory.apply(relicJson);
        }

        return new RelicState(relicJson);
    }

    public static class RelicFactories {
        public final Function<AbstractRelic, RelicState> factory;
        public final Function<String, RelicState> jsonFactory;
        public Function<JsonObject, RelicState> jsonObjectFactory;

        public RelicFactories(Function<AbstractRelic, RelicState> factory, Function<String, RelicState> jsonFactory, Function<JsonObject, RelicState> jsonObjectFactory) {
            this.jsonObjectFactory = jsonObjectFactory;
            this.factory = factory;
            this.jsonFactory = jsonFactory;
        }

//        public RelicFactories(Function<AbstractRelic, RelicState> factory, Function<String, RelicState> jsonFactory) {
//            this.factory = factory;
//            this.jsonFactory = jsonFactory;
//        }

        public RelicFactories(Function<AbstractRelic, RelicState> factory) {
            this.factory = factory;
            this.jsonFactory = json -> new RelicState(json);
        }
    }
}
