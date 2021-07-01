package savestate.powers.powerstates.watcher;

import basemod.ReflectionHacks;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import savestate.powers.PowerState;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.watcher.DevaPower;

public class DevaPowerState extends PowerState {
    private final int energyGainAmount;

    public DevaPowerState(AbstractPower power) {
        super(power);
        this.energyGainAmount = ReflectionHacks
                .getPrivate(power, DevaPower.class, "energyGainAmount");
    }

    public DevaPowerState(String jsonString) {
        super(jsonString);

        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();

        this.energyGainAmount = parsed.get("energy_gain_amount").getAsInt();
    }

    @Override
    public AbstractPower loadPower(AbstractCreature targetAndSource) {
        DevaPower result = new DevaPower(targetAndSource);

        ReflectionHacks.setPrivate(result, DevaPower.class, "energyGainAmount", energyGainAmount);
        result.amount = amount;

        return result;
    }

    @Override
    public String encode() {
        JsonObject parsed = new JsonParser().parse(super.encode()).getAsJsonObject();

        parsed.addProperty("energy_gain_amount", energyGainAmount);

        return parsed.toString();
    }
}
