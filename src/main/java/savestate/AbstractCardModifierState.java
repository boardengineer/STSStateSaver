package savestate;

import basemod.abstracts.AbstractCardModifier;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.function.Function;

public abstract class AbstractCardModifierState {
    String identifier;

    public AbstractCardModifierState(AbstractCardModifier modifier) {
        // I hope this isn't a problem
        identifier = modifier.identifier(null);
    }

    public AbstractCardModifierState(String jsonString) {
        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();

        this.identifier = parsed.get("identifier").getAsString();
    }

    public String encode() {
        JsonObject modifierJson = new JsonObject();

        modifierJson.addProperty("identifier", identifier);

        return modifierJson.toString();
    }

    public static AbstractCardModifierState forModifier(AbstractCardModifier modifier) {
        String identifier = modifier.identifier(null);
        return StateFactories.cardModifierFactories.get(identifier).factory.apply(modifier);
    }

    public static AbstractCardModifierState forString(String jsonString) {
        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();

        String identifier = parsed.get("identifier").getAsString();
        return StateFactories.cardModifierFactories.get(identifier).jsonFactory.apply(jsonString);
    }

    public abstract AbstractCardModifier loadModifier();

    public static class CardModifierStateFactories {
        public final Function<AbstractCardModifier, AbstractCardModifierState> factory;
        public final Function<String, AbstractCardModifierState> jsonFactory;

        public CardModifierStateFactories(Function<AbstractCardModifier, AbstractCardModifierState> factory, Function<String, AbstractCardModifierState> jsonFactory) {
            this.factory = factory;
            this.jsonFactory = jsonFactory;
        }
    }
}
