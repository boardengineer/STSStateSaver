package savestate;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.mod.stslib.patches.core.AbstractCreature.TempHPField;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.stances.AbstractStance;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import savestate.orbs.OrbState;
import savestate.relics.RelicState;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static savestate.SaveStateMod.shouldGoFast;

public class PlayerState extends CreatureState {
    private static final String CARD_DELIMETER = ";;;";
    private static final String RELIC_DELIMETER = "!;!";

    private final AbstractPlayer.PlayerClass chosenClass;
    private final int gameHandSize;
    private final int masterHandSize;
    private final int potionSLots;
    private final int startingMaxHP;
    private final int temporaryHp;

    private final int energyManagerEnergy;
    private final int energyManagerMaxMaster;
    private final int energyPanelTotalEnergy;

    private final boolean isEndingTurn;
    private final boolean viewingRelics;
    private final boolean inspectMode;
    private final HitboxState inspectHb;
    private final int damagedThisCombat;
    private final String title;
    private final String stance;

    private final boolean renderCorpse;

    public final CardState cardInUse;

    public final ArrayList<CardState> masterDeck;
    public final ArrayList<CardState> drawPile;
    public final ArrayList<CardState> hand;
    public final ArrayList<CardState> discardPile;
    public final ArrayList<CardState> exhaustPile;
    public final ArrayList<CardState> limbo;

    public int maxOrbs;
    public int masterMaxOrbs;

    public ArrayList<OrbState> orbs;
    public ArrayList<OrbState> orbsChanneledThisCombat;

    public final ArrayList<PotionState> potions;

    public final ArrayList<RelicState> relics;

    public PlayerState(AbstractPlayer player) {
        super(player);

        this.chosenClass = player.chosenClass;
        this.gameHandSize = player.gameHandSize;
        this.masterHandSize = player.masterHandSize;
        this.startingMaxHP = player.startingMaxHP;

        this.temporaryHp = TempHPField.tempHp.get(player);

        this.masterDeck = toCardStateArray(player.masterDeck.group);
        this.drawPile = toCardStateArray(player.drawPile.group);
        this.hand = toCardStateArray(player.hand.group);
        this.discardPile = toCardStateArray(player.discardPile.group);
        this.exhaustPile = toCardStateArray(player.exhaustPile.group);
        this.limbo = toCardStateArray(player.limbo.group);
        this.cardInUse = player.cardInUse == null ? null : new CardState(player.cardInUse);

        this.relics = player.relics.stream().map(RelicState::forRelic)
                                   .collect(Collectors.toCollection(ArrayList::new));

        this.orbs = player.orbs.stream().map(OrbState::forOrb)
                               .collect(Collectors.toCollection(ArrayList::new));
        this.orbsChanneledThisCombat = AbstractDungeon.actionManager.orbsChanneledThisCombat
                .stream()
                .map(OrbState::forOrb)
                .collect(Collectors.toCollection(ArrayList::new));

        this.potions = player.potions.stream().map(PotionState::new)
                                     .collect(Collectors.toCollection(ArrayList::new));

        this.energyManagerEnergy = player.energy.energy;
        this.energyPanelTotalEnergy = EnergyPanel.totalCount;

        this.energyManagerMaxMaster = player.energy.energyMaster;

        this.isEndingTurn = player.isEndingTurn;
        this.viewingRelics = player.viewingRelics;
        this.inspectMode = player.inspectMode;

        this.renderCorpse = ReflectionHacks
                .getPrivate(player, AbstractPlayer.class, "renderCorpse");

        this.inspectHb = player.inspectHb == null ? null : new HitboxState(player.inspectHb);
        this.damagedThisCombat = player.damagedThisCombat;
        this.maxOrbs = player.maxOrbs;
        this.masterMaxOrbs = player.masterMaxOrbs;
        this.potionSLots = player.potionSlots;
        this.stance = player.stance.ID;

        this.title = player.title;
    }

    public PlayerState(String jsonString) {
        super(new JsonParser().parse(jsonString).getAsJsonObject().get("creature").getAsString());

        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();

        this.chosenClass = AbstractPlayer.PlayerClass
                .valueOf(parsed.get("chosen_class_name").getAsString());

        // TODO This should ideally happen during load but only once per run
        AbstractDungeon.player = CardCrawlGame.characterManager.getCharacter(chosenClass);
        SaveStateMod.shouldResetDungeon = true;

        boolean cardsInitialized = false;

        while (!cardsInitialized) {
            try {
                CardCrawlGame.dungeon.initializeCardPools();
                cardsInitialized = true;
            } catch (RuntimeException e) {
                System.err.println("Exception trying to init card pools");
                e.printStackTrace();
            }
        }

        this.gameHandSize = parsed.get("game_hand_size").getAsInt();
        this.masterHandSize = parsed.get("master_hand_size").getAsInt();
        this.startingMaxHP = parsed.get("starting_max_hp").getAsInt();
        this.potionSLots = parsed.get("potion_slots").getAsInt();
        this.temporaryHp = parsed.get("temporary_hp").getAsInt();

        this.energyManagerEnergy = parsed.get("energy_manager_energy").getAsInt();
        this.energyPanelTotalEnergy = parsed.get("energy_panel_total_energy").getAsInt();
        this.energyManagerMaxMaster = parsed.get("energy_manager_max_master").getAsInt();

        this.isEndingTurn = parsed.get("is_ending_turn").getAsBoolean();
        this.viewingRelics = parsed.get("viewing_relics").getAsBoolean();
        this.inspectMode = parsed.get("inspect_mode").getAsBoolean();
        this.inspectHb = parsed.get("inspect_hb").isJsonNull() ? null : new HitboxState(parsed
                .get("inspect_hb").getAsString());
        this.damagedThisCombat = parsed.get("damaged_this_combat").getAsInt();

        this.title = parsed.get("title").getAsString();

        this.cardInUse = parsed.get("card_in_use").isJsonNull() ? null : new CardState(parsed
                .get("card_in_use").getAsString());

        this.masterDeck = decodeCardList(parsed.get("master_deck").getAsString());
        this.drawPile = decodeCardList(parsed.get("draw_pile").getAsString());
        this.hand = decodeCardList(parsed.get("hand").getAsString());
        this.discardPile = decodeCardList(parsed.get("discard_pile").getAsString());
        this.exhaustPile = decodeCardList(parsed.get("exhaust_pile").getAsString());
        this.limbo = decodeCardList(parsed.get("limbo").getAsString());


        this.relics = Stream.of(parsed.get("relics").getAsString().split(RELIC_DELIMETER))
                            .filter(s -> !s.isEmpty()).map(RelicState::forJsonString)
                            .collect(Collectors.toCollection(ArrayList::new));


        this.potions = new ArrayList<>();
        parsed.get("potions").getAsJsonArray().forEach(potionElement -> this.potions
                .add(new PotionState(potionElement.getAsString())));

        this.maxOrbs = parsed.get("max_orbs").getAsInt();
        this.masterMaxOrbs = parsed.get("master_max_orbs").getAsInt();

        this.orbs = new ArrayList<>();
        parsed.get("orbs").getAsJsonArray().forEach(orbElement -> this.orbs
                .add(OrbState.forJsonString(orbElement.getAsString())));

        this.orbsChanneledThisCombat = new ArrayList<>();
        parsed.get("orbs_channeled_this_combat").getAsJsonArray()
              .forEach(orbElement -> this.orbsChanneledThisCombat
                      .add(OrbState.forJsonString(orbElement.getAsString())));

        this.stance = parsed.get("stance").getAsString();

        //TODO
        this.renderCorpse = false;
    }

    public AbstractPlayer loadPlayer() {
        AbstractPlayer player = CardCrawlGame.characterManager.getCharacter(chosenClass);

        // There are cases where the contents of the hand effect the creation of powers, we
        // want to force the state instead; the other free calls may need to be moved up here as
        // well.
        CardState.freeCardList(player.hand.group);

        player.relics = this.relics.parallelStream().map(RelicState::loadRelic)
                                   .collect(Collectors.toCollection(ArrayList::new));

        if (!shouldGoFast) {
            AbstractDungeon.topPanel.adjustRelicHbs();
            for (int i = 0; i < player.relics.size(); i++) {
                player.relics.get(i).instantObtain(player, i, false);
            }
        }

        super.loadCreature(player);

        player.chosenClass = this.chosenClass;
        player.gameHandSize = this.gameHandSize;
        player.masterHandSize = this.masterHandSize;
        player.startingMaxHP = this.startingMaxHP;
        player.potionSlots = this.potionSLots;

        CardState.freeCardList(player.masterDeck.group);
        CardState.freeCardList(player.drawPile.group);

        CardState.freeCardList(player.discardPile.group);
        CardState.freeCardList(player.exhaustPile.group);
        CardState.freeCardList(player.limbo.group);

        player.cardInUse = this.cardInUse == null ? null : this.cardInUse.loadCard();

        player.masterDeck.group = this.masterDeck.parallelStream().map(CardState::loadCard)
                                                 .collect(Collectors.toCollection(ArrayList::new));

        player.drawPile.group = this.drawPile.parallelStream().map(CardState::loadCard)
                                             .collect(Collectors.toCollection(ArrayList::new));

        player.hand.group = this.hand.parallelStream().map(CardState::loadCard)
                                     .collect(Collectors.toCollection(ArrayList::new));

        player.discardPile.group = this.discardPile.parallelStream().map(CardState::loadCard)
                                                   .collect(Collectors
                                                           .toCollection(ArrayList::new));

        player.exhaustPile.group = this.exhaustPile.parallelStream().map(CardState::loadCard)
                                                   .collect(Collectors
                                                           .toCollection(ArrayList::new));

        player.limbo.group = this.limbo.parallelStream().map(CardState::loadCard)
                                       .collect(Collectors.toCollection(ArrayList::new));

        player.potions = this.potions.parallelStream().map(PotionState::loadPotion)
                                     .collect(Collectors.toCollection(ArrayList::new));


        for (int i = 0; i < player.potions.size(); i++) {
            player.potions.get(i).setAsObtained(i);
        }

        player.orbs = this.orbs.parallelStream().map(OrbState::loadOrb)
                               .collect(Collectors.toCollection(ArrayList::new));

        AbstractDungeon.actionManager.orbsChanneledThisCombat = this.orbsChanneledThisCombat
                .parallelStream().map(OrbState::loadOrb)
                .collect(Collectors.toCollection(ArrayList::new));

        player.energy.energy = this.energyManagerEnergy;
        player.energy.energyMaster = this.energyManagerMaxMaster;
        EnergyPanel.setEnergy(this.energyManagerEnergy);
        EnergyPanel.totalCount = energyPanelTotalEnergy;

        player.isEndingTurn = this.isEndingTurn;
        player.viewingRelics = this.viewingRelics;
        player.inspectMode = this.inspectMode;
        player.inspectHb = this.inspectHb == null ? null : this.inspectHb.loadHitbox();
        player.damagedThisCombat = this.damagedThisCombat;
        player.title = this.title;
        player.maxOrbs = this.maxOrbs;
        player.masterMaxOrbs = this.masterMaxOrbs;

        player.stance = AbstractStance.getStanceFromName(stance);

        ReflectionHacks
                .setPrivate(player, AbstractPlayer.class, "renderCorpse", this.renderCorpse);


        if (!shouldGoFast) {
            for (int i = 0; i < player.orbs.size(); i++) {
                player.orbs.get(i).setSlot(i, player.maxOrbs);
            }
            player.update();
        }
        TempHPField.tempHp.set(player, temporaryHp);

        return player;
    }
/*
    @SpirePatch(clz = ChannelAction.class, method = "update")
    public static class tsSpy2 {
        @SpirePrefixPatch
        public static SpireReturn spy(ChannelAction action) {
            if (shouldGoFast) {
                AbstractOrb orbType = ReflectionHacks
                        .getPrivate(action, ChannelAction.class, "orbType");
                boolean autoEvoke = ReflectionHacks
                        .getPrivate(action, ChannelAction.class, "autoEvoke");

                if (autoEvoke) {
                    AbstractDungeon.player.channelOrb(orbType);
                } else {
                    Iterator var1 = AbstractDungeon.player.orbs.iterator();

                    while (var1.hasNext()) {
                        AbstractOrb o = (AbstractOrb) var1.next();
                        if (o instanceof EmptyOrbSlot) {
                            AbstractDungeon.player.channelOrb(orbType);
                            break;
                        }
                    }
                }

                action.isDone = true;
                return SpireReturn.Return(null);
            } else {
                return SpireReturn.Continue();
            }
        }
    }*/

    public int getDamagedThisCombat() {
        return damagedThisCombat;
    }

    public String getHandString() {
        return String.format("hand:%s discard:%s", hand.stream().map(CardState::getName).sorted()
                                                       .collect(Collectors.joining(" ")),
                discardPile.stream().map(CardState::getName).collect(Collectors.joining(" ")));
    }

    public int getNumSlimes() {
        return getNumInstance("Slimed");
    }

    public int getNumBurns() {
        return getNumInstance("Burn");
    }


    public int getNumInstance(String cardId) {
        long numInstances = discardPile.stream().filter(card -> card.getName().equals(cardId))
                                       .count();

        numInstances += hand.stream().filter(card -> card.getName().equals(cardId))
                            .count();

        numInstances += drawPile.stream().filter(card -> card.getName().equals(cardId))
                                .count();

        return (int) numInstances;
    }

    public String encode() {
        JsonObject playerStateJson = new JsonObject();

        playerStateJson.addProperty("creature", super.encode());

        playerStateJson.addProperty("chosen_class_name", chosenClass.name());
        playerStateJson.addProperty("game_hand_size", gameHandSize);
        playerStateJson.addProperty("master_hand_size", masterHandSize);
        playerStateJson.addProperty("starting_max_hp", startingMaxHP);
        playerStateJson.addProperty("energy_manager_energy", energyManagerEnergy);
        playerStateJson.addProperty("energy_manager_max_master", energyManagerMaxMaster);
        playerStateJson.addProperty("energy_panel_total_energy", energyPanelTotalEnergy);
        playerStateJson.addProperty("is_ending_turn", isEndingTurn);
        playerStateJson.addProperty("viewing_relics", viewingRelics);
        playerStateJson.addProperty("inspect_mode", inspectMode);
        playerStateJson.addProperty("inspect_hb", inspectHb == null ? null : inspectHb.encode());
        playerStateJson.addProperty("damaged_this_combat", damagedThisCombat);
        playerStateJson.addProperty("title", title);

        playerStateJson
                .add("card_in_use", this.cardInUse != null ? new JsonParser().parse(this.cardInUse
                        .encode()) : JsonNull.INSTANCE);
        playerStateJson.addProperty("master_deck", encodeCardList(masterDeck));
        playerStateJson.addProperty("draw_pile", encodeCardList(drawPile));
        playerStateJson.addProperty("hand", encodeCardList(hand));
        playerStateJson.addProperty("discard_pile", encodeCardList(discardPile));
        playerStateJson.addProperty("exhaust_pile", encodeCardList(exhaustPile));
        playerStateJson.addProperty("limbo", encodeCardList(limbo));
        playerStateJson.addProperty("potion_slots", potionSLots);
        playerStateJson.addProperty("stance", stance);
        playerStateJson.addProperty("temporary_hp", temporaryHp);

        playerStateJson.addProperty("relics", relics.stream().map(RelicState::encode)
                                                    .collect(Collectors.joining(RELIC_DELIMETER)));
        playerStateJson.addProperty("max_orbs", maxOrbs);
        playerStateJson.addProperty("master_max_orbs", masterMaxOrbs);

        JsonArray potionArray = new JsonArray();
        for (PotionState potion : potions) {
            potionArray.add(potion.encode());
        }
        playerStateJson.add("potions", potionArray);

        JsonArray orbArray = new JsonArray();
        for (OrbState orb : orbs) {
            orbArray.add(orb.encode());
        }
        playerStateJson.add("orbs", orbArray);

        JsonArray orbChanneledThisCombatArray = new JsonArray();
        for (OrbState orb : orbsChanneledThisCombat) {
            orbChanneledThisCombatArray.add(orb.encode());
        }
        playerStateJson.add("orbs_channeled_this_combat", orbChanneledThisCombatArray);

        return playerStateJson.toString();
    }

    public static String encodeCardList(ArrayList<CardState> cardList) {
        return cardList.parallelStream().map(CardState::encode).collect(Collectors.joining(CARD_DELIMETER));
    }

    private static ArrayList<CardState> decodeCardList(String cardListString) {
        return Stream.of(cardListString.split(CARD_DELIMETER)).parallel().filter(s -> !s.isEmpty())
                     .map(CardState::forString).collect(Collectors.toCollection(ArrayList::new));
    }

    public static ArrayList<CardState> toCardStateArray(ArrayList<AbstractCard> cards) {
        ArrayList<CardState> result = new ArrayList<>();

        for (AbstractCard card : cards) {
            result.add(CardState.forCard(card));
        }

        return result;
    }

    public String diffEncode() {
        JsonObject playerStateJson = new JsonObject();

//        playerStateJson.addProperty("draw_pile", encodeCardList(drawPile));
        playerStateJson.addProperty("hand", diffEncodeCardList(hand));
        playerStateJson.addProperty("discard_pile", diffEncodeCardList(discardPile));
        playerStateJson.addProperty("draw_pile", diffEncodeCardList(drawPile));

        playerStateJson.addProperty("energy_manager_energy", energyManagerEnergy);
        playerStateJson.addProperty("energy_manager_max_master", energyManagerMaxMaster);
        playerStateJson.addProperty("energy_panel_total_energy", energyPanelTotalEnergy);
        playerStateJson.addProperty("stance", stance);
        playerStateJson.addProperty("max_orbs", maxOrbs);

        playerStateJson.addProperty("creature", super.diffEncode());

        JsonArray orbChanneledThisCombatArray = new JsonArray();
        for (OrbState orb : orbsChanneledThisCombat) {
            orbChanneledThisCombatArray.add(orb.encode());
        }
        playerStateJson.add("orbs_channeled_this_combat", orbChanneledThisCombatArray);

        JsonArray orbArray = new JsonArray();
        for (OrbState orb : orbs) {
            orbArray.add(orb.encode());
        }
        playerStateJson.add("orbs", orbArray);

        return playerStateJson.toString();
    }

    public static boolean diff(String diffString1, String diffString2) {
        JsonObject one = new JsonParser().parse(diffString1).getAsJsonObject();
        JsonObject two = new JsonParser().parse(diffString2).getAsJsonObject();

        boolean allMatch = true;

        boolean maxOrbsMatch = one.get("max_orbs").toString()
                                  .equals(two.get("max_orbs").toString());
        if (!maxOrbsMatch) {
            allMatch = false;
            System.err.println(String.format("max orbs mismatch actual: %s expected: %s",
                    one.get("max_orbs").toString(), two.get("max_orbs").toString()));
        }

        boolean orbsMatch = one.get("orbs").toString()
                               .equals(two.get("orbs").toString());

        if (!orbsMatch) {
            allMatch = false;
            System.err.println("player orbs mismatch");
            System.err.println(one.get("orbs").toString());
            System.err.println("-----------------------------------");
            System.err.println(two.get("orbs").toString());
        }


        boolean discardMatch = one.get("discard_pile").getAsString()
                                  .equals(two.get("discard_pile").getAsString());
        /*
        if (!discardMatch) {
            allMatch = false;
            System.err.println("player discard mismatch");
            System.err.println(one.get("discard_pile").getAsString());
            System.err.println("-----------------------------------");
            System.err.println(two.get("discard_pile").getAsString());
        }

        */

//        boolean drawMismatch = one.get("draw_pile").getAsString()
//                                  .equals(two.get("draw_pile").getAsString());
//        if (!drawMismatch) {
//            allMatch = false;
//            System.err.println("player draw mismatch");
//            System.err.println(one.get("draw_pile").getAsString());
//            System.err.println("-----------------------------------");
//            System.err.println(two.get("draw_pile").getAsString());
//        }


        boolean handsMatch = one.get("hand").getAsString().equals(two.get("hand").getAsString());
        if (!handsMatch) {
            allMatch = false;
            System.err.println("player hand mismatch");
            System.err.println(one.get("hand").getAsString());
            System.err.println("-----------------------------------");
            System.err.println(two.get("hand").getAsString());
        }

        boolean stanceMatch = one.get("stance").getAsString()
                                 .equals(two.get("stance").getAsString());
        if (!stanceMatch) {
            allMatch = false;
            System.err.println("player stance mismatch");
            System.err.println(one.get("stance").getAsString());
            System.err.println("-----------------------------------");
            System.err.println(two.get("stance").getAsString());
        }

        boolean statsMatch = CreatureState
                .diff(one.get("creature").getAsString(), two.get("creature").getAsString());
        if (!statsMatch) {
            allMatch = false;
        }

        boolean energyMatch = one.get("energy_panel_total_energy").getAsInt() == two
                .get("energy_panel_total_energy").getAsInt();
        if (!energyMatch) {
            allMatch = false;
            System.err.println("energy_panel_total_energy energy mismatch");

            System.err.println(one.get("energy_panel_total_energy").getAsInt());
            System.err.println("-----------------------------------");
            System.err.println(two.get("energy_panel_total_energy").getAsInt());
        }

        return allMatch;
    }

    public static String diffEncodeCardList(ArrayList<CardState> cardList) {
        return cardList.parallelStream().map(CardState::diffEncode)
                       .collect(Collectors.joining(CARD_DELIMETER));
    }
}
