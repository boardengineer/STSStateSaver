package savestate.orbs;

import com.google.gson.JsonObject;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;

public class EmptyOrbSlotState extends OrbState {
    public EmptyOrbSlotState(AbstractOrb orb) {
        super(orb);
    }

    public EmptyOrbSlotState(String jsonString) {
        super(jsonString);
    }

    public EmptyOrbSlotState(JsonObject orbJson) {
        super(orbJson);
    }

    @Override
    public AbstractOrb loadOrb() {
        EmptyOrbSlot result = new EmptyOrbSlot();
        result.evokeAmount = this.evokeAmount;
        result.passiveAmount = this.passiveAmount;
        return result;
    }
}
