package savestate.powers.powerstates.common;

import basemod.ReflectionHacks;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.TheBombPower;
import savestate.powers.PowerState;

// TODO this
public class TheBombPowerState extends PowerState {
    private final int damage;

    public TheBombPowerState(AbstractPower power) {
        super(power);

        this.damage = ReflectionHacks.getPrivate(power, TheBombPower.class, "damage");
    }

    public TheBombPowerState(String jsonString) {
        super(jsonString);

        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();

        this.damage = parsed.get("damage").getAsInt();
    }

    @Override
    public String encode() {
        JsonObject parsed = new JsonParser().parse(super.encode()).getAsJsonObject();

        parsed.addProperty("damage", damage);

        return parsed.toString();
    }


    @Override
    public AbstractPower loadPower(AbstractCreature targetAndSource) {
        TheBombPower result = new TheBombPower(targetAndSource, amount, damage);

        result.ID = powerId;

        return result;
    }
}
