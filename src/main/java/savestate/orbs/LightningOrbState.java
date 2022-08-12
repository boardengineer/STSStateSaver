package savestate.orbs;

import com.google.gson.JsonObject;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.Lightning;

public class LightningOrbState extends OrbState {
    public LightningOrbState(AbstractOrb orb) {
        super(orb);
    }

    public LightningOrbState(String jsonString) {
        super(jsonString);
    }

    public LightningOrbState(JsonObject orgJson) {
        super(orgJson);
    }

    @Override
    public AbstractOrb loadOrb() {
        Lightning result = new Lightning();

        result.evokeAmount = this.evokeAmount;
        result.passiveAmount = this.passiveAmount;

        return result;
    }
}
