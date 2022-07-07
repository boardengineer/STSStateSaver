package savestate.monsters;

import basemod.ReflectionHacks;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import savestate.*;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static savestate.SaveStateMod.shouldGoFast;

public abstract class MonsterState extends CreatureState {
    private static final String DAMAGE_DELIMETER = ";";
    private static final String MOVE_HISTORY_DELIMETER = "&";

    private final float deathTimer;
    private final boolean tintFadeOutCalled;
    private final boolean escaped;
    private final boolean escapeNext;
    private final boolean cannotEscape;
    private final byte nextMove;
    private final HitboxState intentHb;
    private final float intentAlpha;
    private final float intentAlphaTarget;
    private final float intentOffsetX;
    private final String moveName;
    private final EnemyMoveInfoState moveInfo;

    private final AbstractMonster.EnemyType type;

    private final AbstractMonster.Intent intent;
    private final AbstractMonster.Intent tipIntent;

    private final ArrayList<Byte> moveHistory;
    private final ArrayList<DamageInfoState> damage;

    protected final float offsetX;
    protected final float offsetY;

    public int monsterTypeNumber;

    public MonsterState(AbstractMonster monster) {
        super(monster);

        this.deathTimer = monster.deathTimer;
        this.tintFadeOutCalled = monster.tintFadeOutCalled;
        this.escaped = monster.escaped;
        this.escapeNext = monster.escapeNext;
        this.type = monster.type;
        this.cannotEscape = monster.cannotEscape;

        this.nextMove = monster.nextMove;
        this.intentHb = new HitboxState(monster.intentHb);
        this.intent = monster.intent;
        this.tipIntent = monster.tipIntent;
        this.intentAlpha = monster.intentAlpha;
        this.intentAlphaTarget = monster.intentAlphaTarget;
        this.intentOffsetX = monster.intentOffsetX;
        this.moveName = monster.moveName;

        EnemyMoveInfo monsterMoveInfo = ReflectionHacks
                .getPrivate(monster, AbstractMonster.class, "move");
        this.moveInfo = monsterMoveInfo == null ? null : new EnemyMoveInfoState(monsterMoveInfo);

        this.damage = monster.damage.stream().map(DamageInfoState::new).map(damageInfoState -> {
            damageInfoState.owner = monster;
            return damageInfoState;
        }).collect(Collectors.toCollection(ArrayList::new));

        this.moveHistory = monster.moveHistory.stream().map(Byte::byteValue)
                                              .collect(Collectors.toCollection(ArrayList::new));

        offsetX = (drawX - (float) Settings.WIDTH * 0.75F) / Settings.xScale;
        offsetY = (drawY - AbstractDungeon.floorY) / Settings.yScale;
    }

    public MonsterState(String jsonString) {
        super(new JsonParser().parse(jsonString).getAsJsonObject().get("creature").getAsString());

        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();

        this.deathTimer = parsed.get("death_timer").getAsFloat();
        this.tintFadeOutCalled = parsed.get("tint_fade_out_called").getAsBoolean();
        this.escaped = parsed.get("escaped").getAsBoolean();
        this.escapeNext = parsed.get("escape_next").getAsBoolean();
        this.type = AbstractMonster.EnemyType.valueOf(parsed.get("type_name").getAsString());
        this.cannotEscape = parsed.get("cannot_escape").getAsBoolean();
        this.nextMove = parsed.get("next_move").getAsByte();
        this.intentHb = new HitboxState(parsed.get("intent_hb").getAsString());
        this.intent = AbstractMonster.Intent.valueOf(parsed.get("intent_name").getAsString());
        this.tipIntent = AbstractMonster.Intent
                .valueOf(parsed.get("tip_intent_name").getAsString());
        this.intentAlpha = parsed.get("intent_alpha").getAsFloat();
        this.intentAlphaTarget = parsed.get("intent_alpha_target").getAsFloat();
        this.intentOffsetX = parsed.get("intent_offset_x").getAsFloat();
        this.moveName = parsed.get("move_name").isJsonNull() ? null : parsed.get("move_name")
                                                                            .getAsString();

        this.moveInfo = new EnemyMoveInfoState(parsed.get("move_info").getAsString());

        this.damage = Stream.of(parsed.get("damage").getAsString().split(DAMAGE_DELIMETER))
                            .filter(s -> !s.isEmpty()).map(s -> new DamageInfoState(s))
                            .collect(Collectors.toCollection(ArrayList::new));

        this.moveHistory = Stream
                .of(parsed.get("move_history").getAsString().split(MOVE_HISTORY_DELIMETER))
                .filter(s -> !s.isEmpty()).map(Byte::parseByte)
                .collect(Collectors.toCollection(ArrayList::new));

        float tempX = 0;
        float tempY = 0;

        try {
            tempX = (drawX - (float) Settings.WIDTH * 0.75F) / Settings.xScale;
            tempY = (drawY - AbstractDungeon.floorY) / Settings.yScale;
        } catch (ExceptionInInitializerError | NoClassDefFoundError e) {

        }
        offsetX = tempX;
        offsetY = tempY;
    }

    public abstract AbstractMonster loadMonster();

    public void populateSharedFields(AbstractMonster monster) {
        super.loadCreature(monster);

        if (!shouldGoFast) {
            monster.showHealthBar();
            monster.healthBarUpdatedEvent();
        }

        monster.deathTimer = this.deathTimer;
        monster.tintFadeOutCalled = this.tintFadeOutCalled;
        monster.escaped = this.escaped;
        monster.escapeNext = this.escapeNext;
        monster.type = this.type;
        monster.cannotEscape = this.cannotEscape;

        monster.damage = this.damage.stream().map(DamageInfoState::loadDamageInfo)
                                    .peek(state -> state.owner = monster)
                                    .collect(Collectors.toCollection(ArrayList::new));


        ReflectionHacks.setPrivate(monster, AbstractMonster.class, "move", moveInfo.loadMoveInfo());
        monster.moveHistory = new ArrayList<>(this.moveHistory);

        monster.nextMove = this.nextMove;
        monster.intentHb = this.intentHb.loadHitbox();
        monster.intent = this.intent;
        monster.tipIntent = this.tipIntent;
        monster.intentAlpha = this.intentAlpha;
        monster.intentAlphaTarget = this.intentAlphaTarget;
        monster.intentOffsetX = this.intentOffsetX;
        monster.moveName = this.moveName;

        if (!shouldGoFast) {
            monster.showHealthBar();
        }

        monster.createIntent();


        if (!shouldGoFast && monster.currentBlock > 0) {
            ReflectionHacks
                    .setPrivate(monster, AbstractCreature.class, "blockAnimTimer", 0.7F);
            ReflectionHacks
                    .setPrivate(monster, AbstractCreature.class, "blockTextColor", 0.0F);
        }
    }

    public String encode() {
        JsonObject monsterStateJson = new JsonObject();

        monsterStateJson.addProperty("creature", super.encode());
        monsterStateJson.addProperty("death_timer", deathTimer);
        monsterStateJson.addProperty("tint_fade_out_called", tintFadeOutCalled);
        monsterStateJson.addProperty("escaped", escaped);
        monsterStateJson.addProperty("escape_next", escapeNext);
        monsterStateJson.addProperty("cannot_escape", cannotEscape);
        monsterStateJson.addProperty("next_move", nextMove);
        monsterStateJson.addProperty("intent_hb", intentHb.encode());
        monsterStateJson.addProperty("intent_alpha", intentAlpha);
        monsterStateJson.addProperty("intent_alpha_target", intentAlphaTarget);
        monsterStateJson.addProperty("intent_offset_x", intentOffsetX);
        monsterStateJson.addProperty("move_name", moveName);
        monsterStateJson.addProperty("move_info", moveInfo.encode());
        monsterStateJson.addProperty("intent_name", intent.name());
        monsterStateJson.addProperty("type_name", type.name());
        monsterStateJson.addProperty("tip_intent_name", tipIntent.name());

        monsterStateJson
                .addProperty("move_history", moveHistory.stream().map(b -> String.valueOf(b))
                                                        .collect(Collectors
                                                                .joining(MOVE_HISTORY_DELIMETER)));

        monsterStateJson.addProperty("damage", damage.stream().map(DamageInfoState::encode)
                                                     .collect(Collectors
                                                             .joining(DAMAGE_DELIMETER)));

        return monsterStateJson.toString();
    }

    public String diffEncode() {
        JsonObject monsterStateJson = new JsonObject();

        monsterStateJson.addProperty("creature", super.diffEncode());
        monsterStateJson.addProperty("intent_name", intent.name());

        monsterStateJson
                .addProperty("move_history", moveHistory.stream().map(b -> String.valueOf(b))
                                                        .collect(Collectors
                                                                .joining(MOVE_HISTORY_DELIMETER)));

        monsterStateJson.addProperty("move_info", moveInfo.encode());

        return monsterStateJson.toString();
    }

    public static boolean diff(String diffString1, String diffString2) {
        boolean result = true;

        JsonObject one = new JsonParser().parse(diffString1).getAsJsonObject();
        JsonObject two = new JsonParser().parse(diffString2).getAsJsonObject();

        boolean statsEqual = CreatureState
                .diff(one.get("creature").getAsString(), two.get("creature").getAsString());
        if (!statsEqual) {
            result = false;
        }

        boolean intentEqual = one.get("intent_name").getAsString()
                                 .equals(two.get("intent_name").getAsString());
        if (!intentEqual) {
            System.err.printf("intent one: %s, intent two: %s\n", one.get("intent_name")
                                                                     .getAsString(), two
                    .get("intent_name").getAsString());
            result = false;
        }

        boolean moveHistoryEqual = one.get("move_history").getAsString()
                                      .equals(two.get("move_history").getAsString());
        if (!moveHistoryEqual) {
            System.err
                    .printf("move_history one: %s, move_history two: %s\n", one.get("move_history")
                                                                               .getAsString(), two
                            .get("move_history").getAsString());
            result = false;
        }

        boolean moveEqual = one.get("move_info").getAsString()
                               .equals(two.get("move_info").getAsString());
        if (!moveEqual) {
            System.err.printf("move_info one: %s, move_info two: %s\n", one.get("move_info")
                                                                           .getAsString(), two
                    .get("move_info").getAsString());
            result = false;
        }

        return result;
    }

    // static copy of AbstractMonster.setHp
    public static void setHp(AbstractMonster monster, int minHp, int maxHp) {
        monster.currentHealth = AbstractDungeon.monsterHpRng.random(minHp, maxHp);
        if (Settings.isEndless && AbstractDungeon.player.hasBlight("ToughEnemies")) {
            float mod = AbstractDungeon.player.getBlight("ToughEnemies").effectFloat();
            monster.currentHealth = (int) ((float) monster.currentHealth * mod);
        }

        if (ModHelper.isModEnabled("MonsterHunter")) {
            monster.currentHealth = (int) ((float) monster.currentHealth * 1.5F);
        }

        monster.maxHealth = monster.currentHealth;
    }

    public static MonsterState forMonster(AbstractMonster monster) {
        if (StateFactories.monsterByIdMap.containsKey(monster.id)) {
            return StateFactories.monsterByIdMap.get(monster.id).factory.apply(monster);
        }

        throw new IllegalStateException("No Factory for monster " + monster.id);
    }

    public static MonsterState forJsonString(String jsonString) {
        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();
        JsonObject moreParsed = new JsonParser().parse(parsed.get("creature").getAsString())
                                                .getAsJsonObject();

        String id = moreParsed.get("id").getAsString();
        if (!StateFactories.monsterByIdMap.containsKey(id)) {
            throw new IllegalStateException("Missing json factory for " + id);
        }

        return StateFactories.monsterByIdMap.get(id).jsonFactory.apply(jsonString);
    }

    public static class MonsterFactories {
        public final Function<AbstractMonster, MonsterState> factory;
        public final Function<String, MonsterState> jsonFactory;

        public MonsterFactories(Function<AbstractMonster, MonsterState> factory, Function<String, MonsterState> jsonFactory) {
            this.factory = factory;
            this.jsonFactory = jsonFactory;
        }
    }
}
