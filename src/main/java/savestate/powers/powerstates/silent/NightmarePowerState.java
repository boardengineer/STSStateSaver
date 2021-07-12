package savestate.powers.powerstates.silent;

import basemod.ReflectionHacks;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.NightmarePower;
import savestate.CardState;
import savestate.powers.PowerState;

public class NightmarePowerState extends PowerState {
    CardState card;

    public NightmarePowerState(AbstractPower power) {
        super(power);

        this.card = CardState.forCard((AbstractCard) ReflectionHacks
                .getPrivate(power, NightmarePower.class, "card"));
    }

    public NightmarePowerState(String jsonString) {
        super(jsonString);

        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();

        this.card = CardState.forString(parsed.get("card").getAsString());
    }

    @Override
    public AbstractPower loadPower(AbstractCreature targetAndSource) {
        return new NightmarePower(targetAndSource, amount, card.loadCard());
    }

    @Override
    public String encode() {
        JsonObject parsed = new JsonParser().parse(super.encode()).getAsJsonObject();

        parsed.addProperty("card", card.encode());

        return parsed.toString();
    }
}
