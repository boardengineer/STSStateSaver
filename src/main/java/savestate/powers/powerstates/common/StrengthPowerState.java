package savestate.powers.powerstates.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import savestate.powers.PowerState;

public class StrengthPowerState extends PowerState {
    public StrengthPowerState(AbstractPower power) {
        super(power);
    }

    @Override
    public AbstractPower loadPower(AbstractCreature targetAndSource) {
        return new StrengthPower(targetAndSource, amount);
    }
}
