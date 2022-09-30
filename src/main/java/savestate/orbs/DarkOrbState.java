package savestate.orbs;

import com.google.gson.JsonObject;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.Dark;

public class DarkOrbState extends OrbState {
    private static String LOOKUP_KEY = new Dark().getClass().getSimpleName();

    public DarkOrbState(AbstractOrb orb) {
        super(orb);
    }

    public DarkOrbState(String jsonString) {
        super(jsonString);
    }

    public DarkOrbState(JsonObject orbJson) {
        super(orbJson);
    }

    @Override
    public AbstractOrb loadOrb() {
        Dark result = new Dark();
        result.evokeAmount = this.evokeAmount;
        result.passiveAmount = this.passiveAmount;
        return result;
    }

    @Override
    public String getLookupKey() {
        return LOOKUP_KEY;
    }
}
