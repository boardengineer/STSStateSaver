package savestate.orbs;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.Plasma;

public class PlasmaOrbState extends OrbState {
    public PlasmaOrbState(AbstractOrb orb) {
        super(orb);
    }

    public PlasmaOrbState(String jsonString) {
        super(jsonString);
    }

    @Override
    public AbstractOrb loadOrb() {
        Plasma result = new Plasma();
        result.evokeAmount = this.evokeAmount;
        result.passiveAmount = this.passiveAmount;
        return result;
    }
}
