package savestate.powers.powerstates.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.SurroundedPower;
import savestate.powers.PowerState;

public class SurroundedPowerState extends PowerState
{
    public SurroundedPowerState(AbstractPower power) {
        super(power);
    }

    @Override
    public AbstractPower loadPower(AbstractCreature targetAndSource) {
        return new SurroundedPower(targetAndSource);
    }
}
