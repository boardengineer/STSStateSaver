package savestate.powers.powerstates.monsters;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import savestate.EnemyMoveInfoState;
import savestate.powers.PowerState;

public class StunMonsterPowerState extends PowerState {
    private final byte moveByte;
    private final int moveIntentOrdinal;
    private final EnemyMoveInfoState move;

    public StunMonsterPowerState(AbstractPower power) {
        super(power);

        this.moveByte = ReflectionHacks.getPrivate(power, StunMonsterPower.class, "moveByte");

        AbstractMonster.Intent powerIntent = ReflectionHacks
                .getPrivate(power, StunMonsterPower.class, "moveIntent");
        moveIntentOrdinal = powerIntent.ordinal();

        EnemyMoveInfo powerMoveInfo = ReflectionHacks
                .getPrivate(power, StunMonsterPower.class, "move");
        move = new EnemyMoveInfoState(powerMoveInfo);
    }

    public StunMonsterPowerState(String jsonString) {
        super(jsonString);

        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();

        this.moveByte = parsed.get("move_byte").getAsByte();
        this.moveIntentOrdinal = parsed.get("move_intent_ordinal").getAsInt();
        this.move = new EnemyMoveInfoState(parsed.get("move").getAsString());
    }

    public StunMonsterPowerState(JsonObject powerJson) {
        super(powerJson);

        this.moveByte = powerJson.get("move_byte").getAsByte();
        this.moveIntentOrdinal = powerJson.get("move_intent_ordinal").getAsInt();
        this.move = new EnemyMoveInfoState(powerJson.get("move").getAsJsonObject());
    }

    @Override
    public AbstractPower loadPower(AbstractCreature targetAndSource) {
        // Hopefully no one tries to set this to a player, if they do we'll deal with this then
        StunMonsterPower result = new StunMonsterPower((AbstractMonster) targetAndSource);

        ReflectionHacks.setPrivate(result, StunMonsterPower.class, "moveByte", moveByte);
        ReflectionHacks
                .setPrivate(result, StunMonsterPower.class, "moveIntent", AbstractMonster.Intent
                        .values()[moveIntentOrdinal]);
        ReflectionHacks.setPrivate(result, StunMonsterPower.class, "move", move.loadMoveInfo());

        result.amount = amount;

        return result;
    }

    @Override
    public String encode() {
        JsonObject parsed = new JsonParser().parse(super.encode()).getAsJsonObject();

        parsed.addProperty("move_byte", moveByte);
        parsed.addProperty("move_intent_ordinal", moveIntentOrdinal);
        parsed.addProperty("move", move.encode());

        return parsed.toString();
    }

    @Override
    public JsonObject jsonEncode() {
        JsonObject result = super.jsonEncode();

        result.addProperty("move_byte", moveByte);
        result.addProperty("move_intent_ordinal", moveIntentOrdinal);
        result.addProperty("move", move.encode());

        return result;
    }
}
