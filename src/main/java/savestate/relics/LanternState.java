package savestate.relics;

import basemod.ReflectionHacks;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Lantern;

public class LanternState extends RelicState {
    private final boolean firstTurn;

    public LanternState(AbstractRelic relic) {
        super(relic);

        this.firstTurn = ReflectionHacks
                .getPrivate(relic, Lantern.class, "firstTurn");
    }

    public LanternState(String jsonString) {
        super(jsonString);

        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();

        this.firstTurn = parsed.get("first_turn").getAsBoolean();
    }

    public LanternState(JsonObject relicJson) {
        super(relicJson);

        this.firstTurn = relicJson.get("first_turn").getAsBoolean();
    }

    @Override
    public AbstractRelic loadRelic() {
        Lantern result = (Lantern) super.loadRelic();

        ReflectionHacks
                .setPrivate(result, Lantern.class, "firstTurn", firstTurn);

        return result;
    }

    @Override
    public String encode() {
        JsonObject parsed = new JsonParser().parse(super.encode()).getAsJsonObject();

        parsed.addProperty("first_turn", firstTurn);

        return parsed.toString();
    }

    @Override
    public JsonObject jsonEncode() {
        JsonObject result = super.jsonEncode();

        result.addProperty("first_turn", firstTurn);

        return result;
    }
}
