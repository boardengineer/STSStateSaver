package savestate.powers.powerstates.silent;

import savestate.powers.PowerState;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.CorpseExplosionPower;

public class CorpseExplosionPowerState extends PowerState {
    public CorpseExplosionPowerState(AbstractPower power) {
        super(power);
    }

    @Override
    public AbstractPower loadPower(AbstractCreature targetAndSource) {
        CorpseExplosionPower result = new CorpseExplosionPower(targetAndSource);

        result.amount = amount;

        return result;
    }
}
