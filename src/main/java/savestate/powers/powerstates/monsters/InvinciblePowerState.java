package savestate.powers.powerstates.monsters;

import basemod.ReflectionHacks;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.InvinciblePower;
import savestate.powers.PowerState;

public class InvinciblePowerState extends PowerState {
    private final int maxAmt;

    public InvinciblePowerState(AbstractPower power) {
        super(power);

        this.maxAmt = ReflectionHacks.getPrivate(power, InvinciblePower.class, "maxAmt");
    }

    public InvinciblePowerState(String jsonString) {
        super(jsonString);

        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();

        this.maxAmt = parsed.get("max_amt").getAsInt();
    }

    public InvinciblePowerState(JsonObject powerJson) {
        super(powerJson);

        this.maxAmt = powerJson.get("max_amt").getAsInt();
    }


    @Override
    public AbstractPower loadPower(AbstractCreature targetAndSource) {
        InvinciblePower result = new InvinciblePower(targetAndSource, amount);

        ReflectionHacks.setPrivate(result, InvinciblePower.class, "maxAmt", maxAmt);

        return result;
    }

    @Override
    public String encode() {
        JsonObject parsed = new JsonParser().parse(super.encode()).getAsJsonObject();

        parsed.addProperty("max_amt", maxAmt);

        return parsed.toString();
    }

    @Override
    public JsonObject jsonEncode() {
        JsonObject result = super.jsonEncode();

        result.addProperty("max_amt", maxAmt);

        return result;
    }
}
