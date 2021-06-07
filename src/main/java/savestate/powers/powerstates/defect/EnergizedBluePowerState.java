package savestate.powers.powerstates.defect;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.EnergizedBluePower;
import savestate.powers.PowerState;

public class EnergizedBluePowerState extends PowerState {
    public EnergizedBluePowerState(AbstractPower power) {
        super(power);
    }

    @Override
    public AbstractPower loadPower(AbstractCreature targetAndSource) {
        return new EnergizedBluePower(targetAndSource, amount);
    }
}
