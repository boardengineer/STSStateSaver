package savestate.powers.powerstates.defect;

import basemod.ReflectionHacks;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.EchoPower;
import savestate.powers.PowerState;

public class EchoPowerState extends PowerState {
    public final int cardsDoubledThisTurn;

    public EchoPowerState(AbstractPower power) {
        super(power);
        cardsDoubledThisTurn = ReflectionHacks
                .getPrivate(power, EchoPower.class, "cardsDoubledThisTurn");
    }

    public EchoPowerState(String jsonString) {
        super(jsonString);

        this.cardsDoubledThisTurn = powerJson.get("cards_doubled_this_turn").getAsInt();
    }

    public EchoPowerState(JsonObject tempPowerJson) {
        super(tempPowerJson);

        this.cardsDoubledThisTurn = tempPowerJson.get("cards_doubled_this_turn").getAsInt();
    }

    @Override
    public AbstractPower loadPower(AbstractCreature targetAndSource) {
        EchoPower retVal = new EchoPower(targetAndSource, amount);
        ReflectionHacks
                .setPrivate(retVal, EchoPower.class, "cardsDoubledThisTurn", cardsDoubledThisTurn);
        return retVal;
    }

    @Override
    public String encode() {
        if (powerJson == null) {
            powerJson = new JsonParser().parse(super.encode()).getAsJsonObject();
        }

        powerJson.addProperty("cards_doubled_this_turn", cardsDoubledThisTurn);

        return powerJson.toString();
    }

    @Override
    public JsonObject jsonEncode() {
        JsonObject result =  super.jsonEncode();

        result.addProperty("cards_doubled_this_turn", cardsDoubledThisTurn);

        return result;
    }
}