package savestate.powers.powerstates.monsters;

import basemod.ReflectionHacks;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StasisPower;
import savestate.CardState;
import savestate.powers.PowerState;

public class StasisPowerState extends PowerState {
    private final CardState card;

    public StasisPowerState(AbstractPower power) {
        super(power);

        AbstractCard sourceCard = ReflectionHacks
                .getPrivate(power, StasisPower.class, "card");
        this.card = CardState.forCard(sourceCard);
        if (this.card == null) {
            throw new IllegalStateException("Bad stasis card");
        }
    }

    public StasisPowerState(String jsonString) {
        super(jsonString);

        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();

        this.card = CardState.forString(parsed.get("card").getAsString());
    }

    public StasisPowerState(JsonObject powerJson) {
        super(powerJson);

        this.card = CardState.forJson(powerJson.get("card").getAsJsonObject());
    }

    @Override
    public String encode() {
        JsonObject parsed = new JsonParser().parse(super.encode()).getAsJsonObject();

        parsed.addProperty("card", card.encode());

        return parsed.toString();
    }

    @Override
    public JsonObject jsonEncode() {
        JsonObject result = super.jsonEncode();

        result.add("card", card.jsonEncode());

        return result;
    }

    @Override
    public AbstractPower loadPower(AbstractCreature targetAndSource) {
        AbstractCard resultCard = card.loadCard();

        if (resultCard == null) {
            throw new IllegalStateException("Stasis Card Was Null");
        }

        return new StasisPower(targetAndSource, resultCard);
    }
}
