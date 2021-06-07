package savestate.relics;

import basemod.ReflectionHacks;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.ArtOfWar;

public class ArtOfWarState extends RelicState {
    private final boolean gainEnergyNext;
    private final boolean firstTurn;


    public ArtOfWarState(AbstractRelic relic) {
        super(relic);

        this.gainEnergyNext = ReflectionHacks
                .getPrivate(relic, ArtOfWar.class, "gainEnergyNext");
        this.firstTurn = ReflectionHacks
                .getPrivate(relic, ArtOfWar.class, "firstTurn");
    }

    public ArtOfWarState(String jsonString) {
        super(jsonString);

        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();

        this.gainEnergyNext = parsed.get("gain_energy_next").getAsBoolean();
        this.firstTurn = parsed.get("first_turn").getAsBoolean();
    }

    @Override
    public AbstractRelic loadRelic() {
        ArtOfWar result = (ArtOfWar) super.loadRelic();

        ReflectionHacks
                .setPrivate(result, ArtOfWar.class, "gainEnergyNext", gainEnergyNext);
        ReflectionHacks
                .setPrivate(result, ArtOfWar.class, "firstTurn", firstTurn);

        return result;
    }

    @Override
    public String encode() {
        JsonObject parsed = new JsonParser().parse(super.encode()).getAsJsonObject();

        parsed.addProperty("gain_energy_next", gainEnergyNext);
        parsed.addProperty("first_turn", firstTurn);



        return parsed.toString();
    }
}
