package savestate.orbs;

import com.google.gson.JsonObject;
import com.megacrit.cardcrawl.orbs.*;

import java.util.function.Function;

public enum Orb {
    DARK(Dark.class, orb -> new DarkOrbState(orb), json -> new DarkOrbState(json), jsonObject -> new DarkOrbState(jsonObject)),
    EMPTY(EmptyOrbSlot.class, orb -> new EmptyOrbSlotState(orb), json -> new EmptyOrbSlotState(json), jsonObject -> new EmptyOrbSlotState(jsonObject)),
    FROST(Frost.class, orb -> new FrostOrbState(orb), json -> new FrostOrbState(json), jsonObject -> new FrostOrbState(jsonObject)),
    LIGHTNING(Lightning.class, orb -> new LightningOrbState(orb), json -> new LightningOrbState(json), jsonObject -> new LightningOrbState(jsonObject)),
    PLASMA(Plasma.class, orb -> new PlasmaOrbState(orb), json -> new PlasmaOrbState(json), jsonObject -> new PlasmaOrbState(jsonObject));

    public Class<? extends AbstractOrb> orbClass;
    public Function<AbstractOrb, OrbState> factory;
    public Function<String, OrbState> jsonFactory;
    public Function<JsonObject, OrbState> jsonObjectFactory;

    Orb() {
    }

    Orb(Class<? extends AbstractOrb> orbClass, Function<AbstractOrb, OrbState> factory, Function<String, OrbState> jsonFactory, Function<JsonObject, OrbState> jsonObjectFactory) {
        this.jsonObjectFactory = jsonObjectFactory;
        this.orbClass = orbClass;
        this.factory = factory;
        this.jsonFactory = jsonFactory;
    }
}
