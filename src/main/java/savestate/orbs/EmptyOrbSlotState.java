package savestate.orbs;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;

public class EmptyOrbSlotState extends OrbState {
    public EmptyOrbSlotState(AbstractOrb orb) {
        super(orb);
    }

    public EmptyOrbSlotState(String jsonString) {
        super(jsonString);
    }

    @Override
    public AbstractOrb loadOrb() {
        EmptyOrbSlot result = new EmptyOrbSlot();
        result.evokeAmount = this.evokeAmount;
        result.passiveAmount = this.passiveAmount;
        return result;
    }
}
