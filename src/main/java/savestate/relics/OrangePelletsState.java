package savestate.relics;

import basemod.ReflectionHacks;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.OrangePellets;

public class OrangePelletsState extends RelicState {
    private final boolean SKILL;
    private final boolean POWER;
    private final boolean ATTACK;

    public OrangePelletsState(AbstractRelic relic) {
        super(relic);

        this.SKILL = ReflectionHacks.getPrivateStatic(OrangePellets.class, "SKILL");
        this.POWER = ReflectionHacks.getPrivateStatic(OrangePellets.class, "POWER");
        this.ATTACK = ReflectionHacks.getPrivateStatic(OrangePellets.class, "ATTACK");
    }

    public OrangePelletsState(String jsonString) {
        super(jsonString);

        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();

        this.SKILL = parsed.get("skill").getAsBoolean();
        this.POWER = parsed.get("power").getAsBoolean();
        this.ATTACK = parsed.get("attack").getAsBoolean();
    }

    @Override
    public AbstractRelic loadRelic() {
        OrangePellets result = (OrangePellets) super.loadRelic();

        ReflectionHacks.setPrivateStatic(OrangePellets.class, "SKILL", SKILL);
        ReflectionHacks.setPrivateStatic(OrangePellets.class, "POWER", POWER);
        ReflectionHacks.setPrivateStatic(OrangePellets.class, "ATTACK", ATTACK);

        return result;
    }

    @Override
    public String encode() {
        JsonObject parsed = new JsonParser().parse(super.encode()).getAsJsonObject();

        parsed.addProperty("skill", SKILL);
        parsed.addProperty("power", POWER);
        parsed.addProperty("attack", ATTACK);

        return parsed.toString();
    }
}
