package savestate;

import com.badlogic.gdx.math.RandomXS128;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.EventHelper;
import com.megacrit.cardcrawl.random.Random;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stores all RNG counters for the game as well as event chances.
 */
public class RngState {
    private final Random monsterRng;
    private final Random mapRng;
    private final Random eventRng;
    private final Random merchantRng;
    private final Random cardRng;
    private final Random treasureRng;
    private final Random relicRng;
    private final Random potionRng;
    private final Random monsterHpRng;
    private final Random aiRng;
    private final Random shuffleRng;
    private final Random cardRandomRng;
    private final Random miscRng;
    public final long seed;

    private final ArrayList<Float> eventHelperChances;

    public RngState() {
        seed = Settings.seed;
        monsterRng = betterCopy(AbstractDungeon.monsterRng);
        mapRng = betterCopy(AbstractDungeon.mapRng);
        eventRng = betterCopy(AbstractDungeon.eventRng);
        merchantRng = betterCopy(AbstractDungeon.merchantRng);
        cardRng = betterCopy(AbstractDungeon.cardRng);
        treasureRng = betterCopy(AbstractDungeon.treasureRng);
        relicRng = betterCopy(AbstractDungeon.relicRng);
        potionRng = betterCopy(AbstractDungeon.potionRng);
        monsterHpRng = betterCopy(AbstractDungeon.monsterHpRng);
        aiRng = betterCopy(AbstractDungeon.aiRng);
        shuffleRng = betterCopy(AbstractDungeon.shuffleRng);
        cardRandomRng = betterCopy(AbstractDungeon.cardRandomRng);
        miscRng = betterCopy(AbstractDungeon.miscRng);
        eventHelperChances = EventHelper.getChances();
    }

    public RngState(String jsonString, int floorNum) {
        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();

        seed = parsed.get("seed").getAsLong();
        monsterRng = new Random(seed, parsed.get("monster_rng_counter").getAsInt());
        mapRng = new Random(seed, parsed.get("map_rng_counter").getAsInt());
        eventRng = new Random(seed, parsed.get("event_rng_counter").getAsInt());
        merchantRng = new Random(seed, parsed.get("merchant_rng_counter").getAsInt());
        cardRng = new Random(seed, parsed.get("card_rng_counter").getAsInt());
        treasureRng = new Random(seed, parsed.get("treasure_rng_counter").getAsInt());
        relicRng = new Random(seed, parsed.get("relic_rng_counter").getAsInt());
        potionRng = new Random(seed, parsed.get("potion_rng_counter").getAsInt());

        monsterHpRng = new Random(seed + floorNum, parsed.get("monster_hp_rng_counter").getAsInt());
        aiRng = new Random(seed + floorNum, parsed.get("ai_rng_counter").getAsInt());
        shuffleRng = new Random(seed + floorNum, parsed.get("shuffle_rng_counter").getAsInt());
        cardRandomRng = new Random(seed + floorNum, parsed.get("card_random_rng_counter")
                                                          .getAsInt());
        miscRng = new Random(seed + floorNum, parsed.get("misc_rng_counter").getAsInt());

        String helperString = parsed.get("event_helper_chances").getAsString();
        eventHelperChances = Stream.of(helperString.split(",")).map(Float::parseFloat)
                                   .collect(Collectors.toCollection(ArrayList::new));
    }

    public void loadRng(long floorNum) {
        Settings.seed = seed;
        AbstractDungeon.monsterRng = betterCopy(monsterRng);
        AbstractDungeon.mapRng = betterCopy(mapRng);
        AbstractDungeon.eventRng = betterCopy(eventRng);
        AbstractDungeon.merchantRng = betterCopy(merchantRng);
        AbstractDungeon.cardRng = betterCopy(cardRng);
        AbstractDungeon.treasureRng = betterCopy(treasureRng);
        AbstractDungeon.relicRng = betterCopy(relicRng);
        AbstractDungeon.potionRng = betterCopy(potionRng);

        AbstractDungeon.monsterHpRng = betterCopy(monsterHpRng);
        AbstractDungeon.aiRng = betterCopy(aiRng);
        AbstractDungeon.shuffleRng = betterCopy(shuffleRng);
        AbstractDungeon.cardRandomRng = betterCopy(cardRandomRng);
        AbstractDungeon.miscRng = betterCopy(miscRng);

        EventHelper.setChances(eventHelperChances);
    }

    public String encode() {
        JsonObject rngStateJson = new JsonObject();

        rngStateJson.addProperty("seed", seed);
        rngStateJson.addProperty("monster_rng_counter", monsterRng.counter);
        rngStateJson.addProperty("map_rng_counter", mapRng.counter);
        rngStateJson.addProperty("event_rng_counter", eventRng.counter);
        rngStateJson.addProperty("merchant_rng_counter", merchantRng.counter);
        rngStateJson.addProperty("card_rng_counter", cardRng.counter);
        rngStateJson.addProperty("treasure_rng_counter", treasureRng.counter);
        rngStateJson.addProperty("relic_rng_counter", relicRng.counter);
        rngStateJson.addProperty("potion_rng_counter", potionRng.counter);
        rngStateJson.addProperty("monster_hp_rng_counter", monsterHpRng.counter);
        rngStateJson.addProperty("ai_rng_counter", aiRng.counter);
        rngStateJson.addProperty("shuffle_rng_counter", shuffleRng.counter);
        rngStateJson.addProperty("card_random_rng_counter", cardRandomRng.counter);
        rngStateJson.addProperty("misc_rng_counter", miscRng.counter);

        String helperString = eventHelperChances.stream().map(f -> Float.toString(f))
                                                .collect(Collectors.joining(","));

        rngStateJson.addProperty("event_helper_chances", helperString);

        return rngStateJson.toString();
    }

    /**
     * A copy of Random.copy but uses new Random(long) instead of new Random() which clicks
     * the counter a bunch of times for no reason.
     */
    public static Random betterCopy(Random toCopy) {
        Random result = new Random(1L);
        result.random = new RandomXS128(toCopy.random.getState(0), toCopy.random.getState(1));
        result.counter = toCopy.counter;
        return result;
    }
}
