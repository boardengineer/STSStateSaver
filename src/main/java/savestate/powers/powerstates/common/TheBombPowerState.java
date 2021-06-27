package savestate.powers.powerstates.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.TheBombPower;
import savestate.powers.PowerState;

// TODO this
public class TheBombPowerState extends PowerState {

    public TheBombPowerState(AbstractPower power) {
        super(power);
    }

    @Override
    public AbstractPower loadPower(AbstractCreature targetAndSource) {
        return new TheBombPower(targetAndSource, 0, 0);
    }
}
