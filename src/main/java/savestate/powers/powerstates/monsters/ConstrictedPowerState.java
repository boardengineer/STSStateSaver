package savestate.powers.powerstates.monsters;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ConstrictedPower;
import savestate.powers.PowerState;

public class ConstrictedPowerState extends PowerState {
    public ConstrictedPowerState(AbstractPower power) {
        super(power);
    }

    @Override
    public AbstractPower loadPower(AbstractCreature targetAndSource) {
        return new ConstrictedPower(targetAndSource,
                AbstractDungeon.getMonsters().monsters.get(0), amount);
    }
}
