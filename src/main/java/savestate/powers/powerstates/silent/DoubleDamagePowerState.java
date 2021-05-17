package savestate.powers.powerstates.silent;

import savestate.powers.PowerState;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DoubleDamagePower;

public class DoubleDamagePowerState extends PowerState
{
    public DoubleDamagePowerState(AbstractPower power) {
        super(power);
    }

    @Override
    public AbstractPower loadPower(AbstractCreature targetAndSource) {
        return new DoubleDamagePower(targetAndSource, amount, !targetAndSource.isPlayer);
    }
}
