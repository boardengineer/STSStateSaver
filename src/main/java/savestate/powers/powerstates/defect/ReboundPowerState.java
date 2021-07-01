package savestate.powers.powerstates.defect;

import basemod.ReflectionHacks;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import savestate.powers.PowerState;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ReboundPower;

public class ReboundPowerState extends PowerState {
    private final boolean justEvoked;

    public ReboundPowerState(AbstractPower power) {
        super(power);

        this.justEvoked = ReflectionHacks.getPrivate(power, ReboundPower.class, "justEvoked");
    }

    public ReboundPowerState(String jsonString) {
        super(jsonString);

        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();

        this.justEvoked = parsed.get("just_evoked").getAsBoolean();
    }

    @Override
    public AbstractPower loadPower(AbstractCreature targetAndSource) {
        ReboundPower result = new ReboundPower(targetAndSource);

        ReflectionHacks.setPrivate(result, ReboundPower.class, "justEvoked", false);

        return result;
    }

    @Override
    public String encode() {
        JsonObject parsed = new JsonParser().parse(super.encode()).getAsJsonObject();

        parsed.addProperty("just_evoked", justEvoked);

        return parsed.toString();
    }
}
