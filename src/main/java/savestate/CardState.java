package savestate;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;

import java.util.*;
import java.util.function.Function;

import static savestate.SaveStateMod.shouldGoFast;

public class CardState {
    public static boolean IGNORE_MOD_SUBTYPES = false;
    private static HashMap<String, LinkedList<AbstractCard>> freeCards;

    public final int cardIdIndex;
    public final boolean upgraded;
    private final int timesUpgraded;
    public final int baseDamage;
    private final int baseBlock;

    public final int misc;
    public final int cost;

    private final int damage;

    private final int costForTurn;
    private final int magicNumber;
    public final int baseMagicNumber;
    private final int block;
    private final boolean freeToPlayOnce;

    private final boolean inBottleTornado;
    private final boolean inBottleLightning;
    private final boolean inBottleFlame;
    private final boolean isCostModifiedForTurn;
    private final boolean isCostModified;
    private final boolean dontTriggerOnUseCard;
    private final boolean exhaust;
    private final boolean purgeOnUse;
    private final boolean isEthereal;
    private final boolean retain;
    private final boolean selfRetain;
    private final boolean shuffleBackIntoDrawPile;
    private final int targetEnumOrdinal;

    private final UUID uuid;

    private final ArrayList<AbstractCardModifierState> cardModifiers;

    // private final HitboxState hb;
    public CardState(AbstractCard card) {
        long cardConstructorStartTime = System.currentTimeMillis();

        this.cardIdIndex = StateFactories.cardIdToIndexMap.get(card.cardID);
        this.block = card.block;
        this.upgraded = card.upgraded;
        this.baseDamage = card.baseDamage;
        this.cost = card.cost;
        this.exhaust = card.exhaust;
        this.purgeOnUse = card.purgeOnUse;
        this.retain = card.retain;
        this.damage = card.damage;
        this.costForTurn = card.costForTurn;

        this.inBottleFlame = card.inBottleFlame;
        this.inBottleTornado = card.inBottleTornado;
        this.inBottleLightning = card.inBottleLightning;
        this.freeToPlayOnce = card.freeToPlayOnce;
        this.baseBlock = card.baseBlock;

        this.uuid = card.uuid;
        this.isCostModifiedForTurn = card.isCostModifiedForTurn;
        this.isCostModified = card.isCostModified;
        this.magicNumber = card.magicNumber;
        this.baseMagicNumber = card.baseMagicNumber;
        this.selfRetain = card.selfRetain;

        this.misc = card.misc;

        this.timesUpgraded = card.timesUpgraded;
        this.dontTriggerOnUseCard = card.dontTriggerOnUseCard;
        this.isEthereal = card.isEthereal;
        this.shuffleBackIntoDrawPile = card.shuffleBackIntoDrawPile;
        this.targetEnumOrdinal = card.target.ordinal();

        ArrayList<AbstractCardModifier> mods = CardModifierManager.modifiers(card);

        if (mods.isEmpty()) {
            this.cardModifiers = null;
        } else {
            this.cardModifiers = new ArrayList<>();
            CardModifierManager.modifiers(card).forEach(modifier -> cardModifiers
                    .add(AbstractCardModifierState.forModifier(modifier)));
        }
    }

    public CardState(String jsonString) {
        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();

        this.cardIdIndex = StateFactories.cardIdToIndexMap.get(parsed.get("card_id").getAsString());
        this.upgraded = parsed.get("upgraded").getAsBoolean();
        this.baseDamage = parsed.get("base_damage").getAsInt();
        this.cost = parsed.get("cost").getAsInt();
        this.costForTurn = parsed.get("cost_for_turn").getAsInt();

        this.inBottleLightning = parsed.get("in_bottle_lightning").getAsBoolean();
        this.inBottleTornado = parsed.get("in_bottle_tornado").getAsBoolean();
        this.inBottleFlame = parsed.get("in_bottle_flame").getAsBoolean();

        JsonElement nameElement = parsed.get("name");
        this.uuid = UUID.fromString(parsed.get("uuid").getAsString());
        this.freeToPlayOnce = parsed.get("free_to_play_once").getAsBoolean();
        this.isCostModifiedForTurn = parsed.get("is_cost_modified_for_turn").getAsBoolean();
        this.isCostModified = parsed.get("is_cost_modified").getAsBoolean();
        this.magicNumber = parsed.get("magic_number").getAsInt();
        this.block = parsed.get("block").getAsInt();
        this.baseMagicNumber = parsed.get("base_magic_number").getAsInt();
        this.baseBlock = parsed.get("base_block").getAsInt();
        this.timesUpgraded = parsed.get("times_upgraded").getAsInt();
        this.exhaust = parsed.get("exhaust").getAsBoolean();
        this.purgeOnUse = parsed.get("purge_on_use").getAsBoolean();
        this.isEthereal = parsed.get("is_ethereal").getAsBoolean();
        this.misc = parsed.get("misc").getAsInt();
        this.damage = parsed.get("damage").getAsInt();
        this.retain = parsed.get("retain").getAsBoolean();
        this.selfRetain = parsed.get("self_retain").getAsBoolean();
        this.shuffleBackIntoDrawPile = parsed.get("shuffle_back_into_draw_pile").getAsBoolean();
        if (parsed.has("target_enum_ordinal")) {
            this.targetEnumOrdinal = parsed.get("target_enum_ordinal").getAsInt();
        } else {
            this.targetEnumOrdinal = -1;
        }

        JsonElement modsElement = parsed.get("modifiers");
        if (modsElement.isJsonNull()) {
            this.cardModifiers = null;
        } else {
            this.cardModifiers = new ArrayList<>();
            parsed.get("modifiers").getAsJsonArray().forEach(jsonElement -> {
                AbstractCardModifierState modState = AbstractCardModifierState
                        .forString(jsonElement.getAsString());
                if (modState != null) {
                    cardModifiers
                            .add(AbstractCardModifierState.forString(jsonElement.getAsString()));
                }
            });
        }

        this.dontTriggerOnUseCard = false;
    }

    public CardState(JsonObject cardJson) {
        this.cardIdIndex =
                StateFactories.cardIdToIndexMap.get(cardJson.get("card_id").getAsString());
        System.err.println(cardJson.get("card_id").getAsString());
        this.upgraded = cardJson.get("upgraded").getAsBoolean();
        this.baseDamage = cardJson.get("base_damage").getAsInt();
        this.cost = cardJson.get("cost").getAsInt();
        this.costForTurn = cardJson.get("cost_for_turn").getAsInt();

        this.inBottleLightning = cardJson.get("in_bottle_lightning").getAsBoolean();
        this.inBottleTornado = cardJson.get("in_bottle_tornado").getAsBoolean();
        this.inBottleFlame = cardJson.get("in_bottle_flame").getAsBoolean();

        this.uuid = UUID.fromString(cardJson.get("uuid").getAsString());
        this.freeToPlayOnce = cardJson.get("free_to_play_once").getAsBoolean();
        this.isCostModifiedForTurn = cardJson.get("is_cost_modified_for_turn").getAsBoolean();
        this.isCostModified = cardJson.get("is_cost_modified").getAsBoolean();
        this.magicNumber = cardJson.get("magic_number").getAsInt();
        this.block = cardJson.get("block").getAsInt();
        this.baseMagicNumber = cardJson.get("base_magic_number").getAsInt();
        this.baseBlock = cardJson.get("base_block").getAsInt();
        this.timesUpgraded = cardJson.get("times_upgraded").getAsInt();
        this.exhaust = cardJson.get("exhaust").getAsBoolean();
        this.purgeOnUse = cardJson.get("purge_on_use").getAsBoolean();
        this.isEthereal = cardJson.get("is_ethereal").getAsBoolean();
        this.misc = cardJson.get("misc").getAsInt();
        this.damage = cardJson.get("damage").getAsInt();
        this.retain = cardJson.get("retain").getAsBoolean();
        this.selfRetain = cardJson.get("self_retain").getAsBoolean();
        this.shuffleBackIntoDrawPile = cardJson.get("shuffle_back_into_draw_pile").getAsBoolean();
        if (cardJson.has("target_enum_ordinal")) {
            this.targetEnumOrdinal = cardJson.get("target_enum_ordinal").getAsInt();
        } else {
            this.targetEnumOrdinal = -1;
        }

        JsonElement modsElement = cardJson.get("modifiers");
        if (modsElement.isJsonNull()) {
            this.cardModifiers = null;
        } else {
            this.cardModifiers = new ArrayList<>();
            cardJson.get("modifiers").getAsJsonArray().forEach(jsonElement -> {
                AbstractCardModifierState modState = AbstractCardModifierState
                        .forJsonObject(jsonElement.getAsJsonObject());
                if (modState != null) {
                    cardModifiers
                            .add(AbstractCardModifierState
                                    .forJsonObject(jsonElement.getAsJsonObject()));
                }
            });
        }

        this.dontTriggerOnUseCard = false;
    }

    public AbstractCard loadCard() {
        AbstractCard result = getCard(StateFactories.cardIds[cardIdIndex]);

        result.upgraded = upgraded;

        result.baseDamage = baseDamage;
        result.cost = cost;
        result.costForTurn = costForTurn;

        result.inBottleLightning = inBottleLightning;
        result.inBottleFlame = inBottleFlame;
        result.inBottleTornado = inBottleTornado;

        result.uuid = uuid;
        result.freeToPlayOnce = freeToPlayOnce;
        result.isCostModifiedForTurn = isCostModifiedForTurn;
        result.isCostModified = isCostModified;
        result.magicNumber = magicNumber;
        result.baseMagicNumber = baseMagicNumber;
        result.block = block;
        result.baseBlock = baseBlock;
        result.timesUpgraded = timesUpgraded;
        result.exhaust = exhaust;
        result.purgeOnUse = purgeOnUse;
        result.dontTriggerOnUseCard = dontTriggerOnUseCard;
        result.isEthereal = isEthereal;
        result.misc = misc;
        result.retain = retain;
        result.damage = damage;
        result.selfRetain = selfRetain;
        result.shuffleBackIntoDrawPile = shuffleBackIntoDrawPile;
        if (targetEnumOrdinal >= 0) {
            result.target = AbstractCard.CardTarget.values()[targetEnumOrdinal];
        }

        if (cardModifiers != null) {
            for (AbstractCardModifierState modifierState : cardModifiers) {
                CardModifierManager.addModifier(result, modifierState.loadModifier());
            }
        }

        return result;
    }

    public String getName() {
        return StateFactories.cardIds[cardIdIndex];
    }

    public String encode() {
        JsonObject cardStateJson = new JsonObject();

        cardStateJson.addProperty("card_id", StateFactories.cardIds[cardIdIndex]);
        cardStateJson.addProperty("upgraded", upgraded);
        cardStateJson.addProperty("base_damage", baseDamage);
        cardStateJson.addProperty("cost", cost);
        cardStateJson.addProperty("cost_for_turn", costForTurn);
        cardStateJson.addProperty("in_bottle_lightning", inBottleLightning);
        cardStateJson.addProperty("in_bottle_flame", inBottleFlame);
        cardStateJson.addProperty("in_bottle_tornado", inBottleTornado);
        cardStateJson.addProperty("free_to_play_once", freeToPlayOnce);
        cardStateJson.addProperty("uuid", uuid.toString());
        cardStateJson.addProperty("is_cost_modified_for_turn", isCostModifiedForTurn);
        cardStateJson.addProperty("is_cost_modified", isCostModified);
        cardStateJson.addProperty("magic_number", magicNumber);
        cardStateJson.addProperty("block", block);
        cardStateJson.addProperty("base_magic_number", baseMagicNumber);
        cardStateJson.addProperty("base_block", baseBlock);
        cardStateJson.addProperty("times_upgraded", timesUpgraded);
        cardStateJson.addProperty("exhaust", exhaust);
        cardStateJson.addProperty("is_ethereal", isEthereal);
        cardStateJson.addProperty("purge_on_use", purgeOnUse);
        cardStateJson.addProperty("misc", misc);
        cardStateJson.addProperty("damage", damage);
        cardStateJson.addProperty("retain", retain);
        cardStateJson.addProperty("self_retain", selfRetain);
        cardStateJson.addProperty("shuffle_back_into_draw_pile", shuffleBackIntoDrawPile);
        cardStateJson.addProperty("target_enum_ordinal", targetEnumOrdinal);

        if (cardModifiers == null) {
            cardStateJson.add("modifiers", null);
        } else {
            JsonArray modifierJsonArray = new JsonArray();
            for (AbstractCardModifierState state : cardModifiers) {
                modifierJsonArray.add(state.encode());
            }
            cardStateJson.add("modifiers", modifierJsonArray);
        }

        return cardStateJson.toString();
    }

    public JsonObject jsonEncode() {
        JsonObject cardStateJson = new JsonObject();

        cardStateJson.addProperty("card_id", StateFactories.cardIds[cardIdIndex]);
        cardStateJson.addProperty("upgraded", upgraded);
        cardStateJson.addProperty("base_damage", baseDamage);
        cardStateJson.addProperty("cost", cost);
        cardStateJson.addProperty("cost_for_turn", costForTurn);
        cardStateJson.addProperty("in_bottle_lightning", inBottleLightning);
        cardStateJson.addProperty("in_bottle_flame", inBottleFlame);
        cardStateJson.addProperty("in_bottle_tornado", inBottleTornado);
        cardStateJson.addProperty("free_to_play_once", freeToPlayOnce);
        cardStateJson.addProperty("uuid", uuid.toString());
        cardStateJson.addProperty("is_cost_modified_for_turn", isCostModifiedForTurn);
        cardStateJson.addProperty("is_cost_modified", isCostModified);
        cardStateJson.addProperty("magic_number", magicNumber);
        cardStateJson.addProperty("block", block);
        cardStateJson.addProperty("base_magic_number", baseMagicNumber);
        cardStateJson.addProperty("base_block", baseBlock);
        cardStateJson.addProperty("times_upgraded", timesUpgraded);
        cardStateJson.addProperty("exhaust", exhaust);
        cardStateJson.addProperty("is_ethereal", isEthereal);
        cardStateJson.addProperty("purge_on_use", purgeOnUse);
        cardStateJson.addProperty("misc", misc);
        cardStateJson.addProperty("damage", damage);
        cardStateJson.addProperty("retain", retain);
        cardStateJson.addProperty("self_retain", selfRetain);
        cardStateJson.addProperty("shuffle_back_into_draw_pile", shuffleBackIntoDrawPile);
        cardStateJson.addProperty("target_enum_ordinal", targetEnumOrdinal);

        if (cardModifiers == null) {
            cardStateJson.add("modifiers", null);
        } else {
            JsonArray modifierJsonArray = new JsonArray();
            for (AbstractCardModifierState state : cardModifiers) {
                modifierJsonArray.add(state.jsonEncode());
            }
            cardStateJson.add("modifiers", modifierJsonArray);
        }

        return cardStateJson;
    }

    public String diffEncode() {
        JsonObject cardStateJson = new JsonObject();

        cardStateJson.addProperty("card_id", StateFactories.cardIds[cardIdIndex]);
        cardStateJson.addProperty("cost", cost);
        cardStateJson.addProperty("cost_for_turn", costForTurn);
        cardStateJson.addProperty("upgraded", upgraded);
        cardStateJson.addProperty("base_magic_number", baseMagicNumber);

        return cardStateJson.toString();
    }

    public static void resetFreeCards() {
        freeCards = new HashMap<>();
    }

    public static void freeCardList(List<AbstractCard> cards) {
        cards.forEach(CardState::freeCard);
        cards.clear();
    }

    public static void freeCard(AbstractCard card) {
        if (card == null) {
            return;
        }

        CardModifierManager.modifiers(card).clear();

        if (freeCards == null) {
            freeCards = new HashMap<>();
        }

        String key = card.cardID;

        if (!freeCards.containsKey(key)) {
            freeCards.put(key, new LinkedList<>());
        }

        LinkedList<AbstractCard> set = freeCards.get(key);
        if (set.size() > 100) {
            return;
        }

        set.add(card);
    }

    public static AbstractCard getCard(String key) {
        Optional<AbstractCard> resultOptional = getCachedCard(key);

        AbstractCard result;
        if (resultOptional.isPresent() && shouldGoFast) {
            result = resultOptional.get();
        } else {
            result = getFreshCard(key);
        }

        return result;
    }

    private static Optional<AbstractCard> getCachedCard(String key) {
        if (freeCards == null || !freeCards.containsKey(key) || freeCards.get(key).isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(freeCards.get(key).poll());
    }

    private static AbstractCard getFreshCard(String key) {
        AbstractCard card = CardLibrary.getCard(key);

        if (card == null) {
            System.err.println("can't find " + key);
        }

        return card.makeCopy();
    }

    public static int indexForCard(AbstractCard card) {
        int testIndex = 0;

        for (AbstractCard candidate : AbstractDungeon.player.hand.group) {
            if (card == candidate) {
                return testIndex;
            }
            testIndex++;
        }

        for (AbstractCard candidate : AbstractDungeon.player.discardPile.group) {
            if (card == candidate) {
                return testIndex;
            }
            testIndex++;
        }

        for (AbstractCard candidate : AbstractDungeon.player.drawPile.group) {
            if (card == candidate) {
                return testIndex;
            }
            testIndex++;
        }

        if (card == AbstractDungeon.player.cardInUse) {
            return testIndex;
        }
        testIndex++;


        for (AbstractCard candidate : AbstractDungeon.player.exhaustPile.group) {
            if (card == candidate) {
                return testIndex;
            }
            testIndex++;
        }

        return -1;
    }

    public static AbstractCard cardForIndex(int index) {
        if (index == -1) {
            throw new IllegalStateException("No card found");
        }

        int testIndex = 0;

        for (AbstractCard candidate : AbstractDungeon.player.hand.group) {
            if (index == testIndex) {
                return candidate;
            }
            testIndex++;
        }

        for (AbstractCard candidate : AbstractDungeon.player.discardPile.group) {
            if (index == testIndex) {
                return candidate;
            }
            testIndex++;
        }

        for (AbstractCard candidate : AbstractDungeon.player.drawPile.group) {
            if (index == testIndex) {
                return candidate;
            }
            testIndex++;
        }

        if (index == testIndex) {
            return AbstractDungeon.player.cardInUse;
        } else {
            testIndex++;
        }

        for (AbstractCard candidate : AbstractDungeon.player.exhaustPile.group) {
            if (index == testIndex) {
                return candidate;
            }
            testIndex++;
        }

        return null;
    }

    public static class CardFactories {
        public final Function<AbstractCard, CardState> factory;
        public final Function<String, CardState> jsonFactory;
        public Function<JsonObject, CardState> jsonObjectFactory = null;

        public CardFactories(Function<AbstractCard, CardState> factory, Function<String, CardState> jsonFactory, Function<JsonObject, CardState> jsonObjectFactory) {
            this.jsonObjectFactory = jsonObjectFactory;
            this.factory = factory;
            this.jsonFactory = jsonFactory;
        }

        public CardFactories(Function<AbstractCard, CardState> factory) {
            this.factory = factory;
            this.jsonFactory = json -> new CardState(json);
        }
    }

    public static CardState forCard(AbstractCard card) {
        Class clazz = card.getClass();

        if (StateFactories.dynamicCardFactoriesByType.containsKey(clazz)) {
            return StateFactories.dynamicCardFactoriesByType.get(clazz).apply(card);
        }

        Function<AbstractCard, CardState> factory = null;
        while (clazz != AbstractCard.class) {
            if (StateFactories.cardFactoriesByType.containsKey(clazz)) {
                factory = StateFactories.cardFactoriesByType.get(clazz).factory;
                break;
            }

            clazz = clazz.getSuperclass();
        }

        if (factory == null) {
            factory = tempCard -> new CardState(tempCard);
        }

        StateFactories.dynamicCardFactoriesByType.put(card.getClass(), factory);

        return factory.apply(card);
    }

    public static CardState forString(String jsonString) {
        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();

        if (!IGNORE_MOD_SUBTYPES) {
            if (parsed.has("type")) {
                return StateFactories.cardFactoriesByTypeName
                        .get(parsed.get("type").getAsString()).jsonFactory
                        .apply(jsonString);
            }

            if (StateFactories.cardFactoriesByCardId.containsKey(parsed.get("card_id"))) {
                return StateFactories.cardFactoriesByCardId.get(parsed.get("card_id")).jsonFactory
                        .apply(jsonString);
            }
        }

        return new CardState(jsonString);
    }

    public static CardState forJson(JsonObject cardJson) {
        if (!IGNORE_MOD_SUBTYPES) {
            if (cardJson.has("type")) {
                return StateFactories.cardFactoriesByTypeName
                        .get(cardJson.get("type").getAsString()).jsonObjectFactory
                        .apply(cardJson);
            }

            if (StateFactories.cardFactoriesByCardId.containsKey(cardJson.get("card_id").getAsString())) {
                return StateFactories.cardFactoriesByCardId
                        .get(cardJson.get("card_id").getAsString()).jsonObjectFactory
                        .apply(cardJson);
            }
        }

        return new CardState(cardJson);
    }
}
