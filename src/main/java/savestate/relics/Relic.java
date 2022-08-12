package savestate.relics;

import com.google.gson.JsonObject;
import com.megacrit.cardcrawl.relics.*;

import java.util.function.Function;

public enum Relic {
    ART_OF_WAE(ArtOfWar.ID, relic -> new ArtOfWarState(relic), json -> new ArtOfWarState(json), jsonObject -> new ArtOfWarState(jsonObject)),
    CENTENNIAL_PUZZLE(CentennialPuzzle.ID, relic -> new CentennialPuzzleState(relic), json -> new CentennialPuzzleState(json), jsonObject -> new CentennialPuzzleState(jsonObject)),
    GAMBLING_CHIP(GamblingChip.ID, relic -> new GamblingChipState(relic), json -> new GamblingChipState(json), jsonObject -> new GamblingChipState(jsonObject)),
    HOVERING_KITE(HoveringKite.ID, relic -> new HoveringKiteState(relic), json -> new HoveringKiteState(json), jsonObject -> new HoveringKiteState(jsonObject)),
    LANTERN(Lantern.ID, relic -> new LanternState(relic), json -> new LanternState(json), jsonObject -> new LanternState(jsonObject)),
    NECRONOMICON(Necronomicon.ID, relic -> new NecronomiconState(relic), json -> new NecronomiconState(json), jsonObject -> new NecronomiconState(jsonObject)),
    ORANGE_PELLETS(OrangePellets.ID, relic -> new OrangePelletsState(relic), json -> new OrangePelletsState(json), jsonObject -> new OrangePelletsState(jsonObject)),
    ORICHALCUM(Orichalcum.ID, relic -> new OrichalcumState(relic), json -> new OrichalcumState(json), jsonObject -> new OrichalcumState(jsonObject)),
    POCKETWATCH(Pocketwatch.ID, relic -> new PocketwatchState(relic), json -> new PocketwatchState(json), jsonObject -> new PocketwatchState(jsonObject)),
    RED_SKULL(RedSkull.ID, relic -> new RedSkullState(relic), json -> new RedSkullState(json), jsonObject -> new RedSkullState(jsonObject)),
    RUNIC_CAPACITOR(RunicCapacitor.ID, relic -> new RunicCapacitorState(relic), json -> new RunicCapacitorState(json), jsonObject -> new RunicCapacitorState(jsonObject)),
    UNCEASING_TOP(UnceasingTop.ID, relic -> new UnceasingTopState(relic), json -> new UnceasingTopState(json), jsonObject -> new UnceasingTopState(jsonObject));

    public final String relicId;
    public final Function<AbstractRelic, RelicState> factory;
    public final Function<String, RelicState> jsonFactory;
    public Function<JsonObject, RelicState> jsonObjectFactory = null;

    Relic() {
        this.relicId = "";

        factory = null;
        jsonFactory = null;
    }

    Relic(String relicId) {
        this.relicId = relicId;

        this.factory = null;
        this.jsonFactory = null;
    }

    Relic(String relicId, Function<AbstractRelic, RelicState> factory, Function<String, RelicState> jsonFactory, Function<JsonObject, RelicState> jsonObjectFactory) {
        this.jsonObjectFactory = jsonObjectFactory;
        this.relicId = relicId;
        this.factory = factory;
        this.jsonFactory = jsonFactory;
    }

//    Relic(String relicId, Function<AbstractRelic, RelicState> factory, Function<String, RelicState> jsonFactory) {
//        this.relicId = relicId;
//        this.factory = factory;
//        this.jsonFactory = jsonFactory;
//    }
}
