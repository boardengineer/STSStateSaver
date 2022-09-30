package savestate.orbs;

import com.google.gson.JsonObject;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.Plasma;

public class PlasmaOrbState extends OrbState {
    public static String LOOKUP_KEY = new Plasma().getClass().getSimpleName();

    public PlasmaOrbState(AbstractOrb orb) {
        super(orb);
    }

    public PlasmaOrbState(String jsonString) {
        super(jsonString);
    }

    public PlasmaOrbState(JsonObject orbJson) {
        super(orbJson);
    }

    @Override
    public AbstractOrb loadOrb() {
        Plasma result = new Plasma();
        result.evokeAmount = this.evokeAmount;
        result.passiveAmount = this.passiveAmount;
        return result;
    }

    @Override
    public String getLookupKey() {
        return LOOKUP_KEY;
    }
}
