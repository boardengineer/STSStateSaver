package savestate;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.unique.AddCardToDeckAction;
import com.megacrit.cardcrawl.actions.watcher.LessonLearnedAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.TheBombPower;
import com.megacrit.cardcrawl.rooms.EmptyRoom;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import savestate.selectscreen.CardRewardScreenState;
import savestate.selectscreen.GridCardSelectScreenState;
import savestate.selectscreen.HandSelectScreenState;

import java.util.ArrayList;
import java.util.HashMap;

import static savestate.SaveStateMod.addRuntime;
import static savestate.SaveStateMod.shouldGoFast;

public class SaveState {
    private final boolean isScreenUp;
    int floorNum;
    boolean previousScreenUp;
    boolean myTurn = false;
    public int turn;
    private int totalDiscardedThisTurn;


    private final ArrayList<Integer> cardsPlayedThisTurn;
    private final ArrayList<CardStateContainer> cardsPlayedThisCombat;
    private final ArrayList<CardStateContainer> gridSelectedCards;
    private final ArrayList<Integer> drawnCards;

    // Load cards from scratch if necessary, ideally they'll be released elsewhere
    private final ArrayList<CardState> cardsPlayedThisTurnBackup;

    AbstractDungeon.CurrentScreen screen;
    AbstractDungeon.CurrentScreen previousScreen;

    ListState listState;
    public PlayerState playerState;
    private HandSelectScreenState handSelectScreenState = null;
    private GridCardSelectScreenState gridCardSelectScreenState = null;
    private CardRewardScreenState cardRewardScreenState = null;
    RngState rngState;
    private final int ascensionLevel;
    private final int mantraGained;
    public final int lessonLearnedCount;
    public final int parasiteCount;

    private boolean endTurnQueued;
    private boolean isEndingTurn;

    public MapRoomNodeState curMapNodeState;

    public HashMap<String, StateElement> additionalElements = new HashMap<>();

    //TODO move this into something that always gets called
    private int gridCardSelectAmount = 0;

    private final int bombIdOffset;

    public SaveState() {
        if (AbstractDungeon.isScreenUp) {
            if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.HAND_SELECT) {
                handSelectScreenState = new HandSelectScreenState();
            }

            if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.GRID) {
                gridCardSelectScreenState = new GridCardSelectScreenState();
            }

            if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.CARD_REWARD) {
                cardRewardScreenState = new CardRewardScreenState();
            }
        }

        this.curMapNodeState = new MapRoomNodeState(AbstractDungeon.currMapNode);
        this.playerState = new PlayerState(AbstractDungeon.player);
        this.screen = AbstractDungeon.screen;
        this.previousScreen = AbstractDungeon.previousScreen;
        this.rngState = new RngState();
        this.listState = new ListState();
        this.floorNum = AbstractDungeon.floorNum;
        this.turn = GameActionManager.turn;
        this.isScreenUp = AbstractDungeon.isScreenUp;
        this.ascensionLevel = AbstractDungeon.ascensionLevel;
        this.totalDiscardedThisTurn = GameActionManager.totalDiscardedThisTurn;
        this.mantraGained = AbstractDungeon.actionManager.mantraGained;

        ArrayList<AbstractCard> allCards = new ArrayList<>();

        AbstractPlayer player = AbstractDungeon.player;

        allCards.addAll(player.masterDeck.group);
        allCards.addAll(player.drawPile.group);
        allCards.addAll(player.hand.group);
        allCards.addAll(player.discardPile.group);
        allCards.addAll(player.exhaustPile.group);
        allCards.addAll(player.limbo.group);

        this.cardsPlayedThisTurn = new ArrayList<>();
        this.cardsPlayedThisTurnBackup = new ArrayList<>();

        AbstractDungeon.actionManager.cardsPlayedThisTurn
                .forEach(card -> {
                    int index = allCards.indexOf(card);
                    if (index == -1) {
                        // Powers don't have indeces
                        this.cardsPlayedThisTurnBackup.add(CardState.forCard(card));
                    } else {
                        this.cardsPlayedThisTurn.add(allCards.indexOf(card));
                    }
                });

        this.cardsPlayedThisCombat = new ArrayList<>();
        this.lessonLearnedCount = CountLessonLearnedHitsPatch.count;
        this.parasiteCount = CountParasitesPatch.count;

        AbstractDungeon.actionManager.cardsPlayedThisCombat
                .forEach(card -> this.cardsPlayedThisCombat
                        .add(CardStateContainer.forCard(card, allCards)));

        this.gridSelectedCards = new ArrayList<>();

        AbstractDungeon.gridSelectScreen.selectedCards
                .forEach(card -> this.gridSelectedCards
                        .add(CardStateContainer.forCard(card, allCards)));

        this.drawnCards = new ArrayList<>();
        this.bombIdOffset = ReflectionHacks.getPrivateStatic(TheBombPower.class, "bombIdOffset");

        this.endTurnQueued = AbstractDungeon.player.endTurnQueued;
        this.isEndingTurn = AbstractDungeon.player.isEndingTurn;

        DrawCardAction.drawnCards.forEach(card -> this.drawnCards.add(allCards.indexOf(card)));
        this.gridCardSelectAmount = ReflectionHacks
                .getPrivate(AbstractDungeon.gridSelectScreen, GridCardSelectScreen.class, "cardSelectAmount");

        StateFactories.elementFactories.entrySet().stream().forEach(entry -> {
            additionalElements.put(entry.getKey(), entry.getValue().factory.get());
        });
    }

    public SaveState(String jsonString) {
        System.err.println("beginning parse....");

        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();

        this.floorNum = parsed.get("floor_num").getAsInt();
        this.previousScreenUp = parsed.get("previous_screen_up").getAsBoolean();
        this.myTurn = parsed.get("my_turn").getAsBoolean();
        this.turn = parsed.get("turn").getAsInt();
        this.mantraGained = parsed.get("mantra_gained").getAsInt();

        this.screen = AbstractDungeon.CurrentScreen
                .valueOf(parsed.get("screen_name").getAsString());
        JsonElement previousScreenName = parsed.get("previous_screen_name");
        this.previousScreen = previousScreenName.isJsonNull() ? null : AbstractDungeon.CurrentScreen
                .valueOf(previousScreenName.getAsString());
        this.listState = new ListState(parsed.get("list_state").getAsString());

        System.err.println("parsing player....");

        this.playerState = new PlayerState(parsed.get("player_state").getAsString());
        this.rngState = new RngState(parsed.get("rng_state").getAsString());

        System.err.println("parsing room....");

        this.curMapNodeState = new MapRoomNodeState(parsed.get("cur_map_node_state").getAsString());
        this.isScreenUp = parsed.get("is_screen_up").getAsBoolean();
        this.ascensionLevel = parsed.get("ascension_level").getAsInt();
        this.bombIdOffset = parsed.get("bomb_id_offset").getAsInt();

        // start counting from the json start
        this.lessonLearnedCount = 0;
        this.parasiteCount = 0;

        // TODO
        this.handSelectScreenState = null;
        this.cardsPlayedThisTurn = new ArrayList<>();
        this.cardsPlayedThisTurnBackup = new ArrayList<>();
        this.cardsPlayedThisCombat = new ArrayList<>();
        this.gridSelectedCards = new ArrayList<>();
        this.drawnCards = new ArrayList<>();

        StateFactories.elementFactories.entrySet().stream().forEach(entry -> {
            additionalElements.put(entry.getKey(), entry.getValue().jsonFactory
                    .apply(parsed.get(entry.getKey()).getAsString()));
        });
    }

    public void loadState() {
//        System.err.println("Client side?");
//        AbstractDungeon.gridSelectScreen.targetGroup = null;

        long startLoad = System.currentTimeMillis();

        // TODO: this is being cleared to prevent grid rewards from going back to a bad state,
        // the combat reward state might need to be set properly if I want to export savestate mod
        // as a fully functional state saver/recaller
        AbstractDungeon.combatRewardScreen.rewards.clear();

        if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.COMBAT_REWARD) {
            AbstractDungeon.closeCurrentScreen();
        }

        AbstractDungeon.actionManager.currentAction = null;
        AbstractDungeon.actionManager.actions.clear();

        AbstractDungeon.ascensionLevel = this.ascensionLevel;
        GameActionManager.turn = this.turn;

        AbstractDungeon.player = playerState.loadPlayer();

        addRuntime("load 0", System.currentTimeMillis() - startLoad);
        curMapNodeState.loadMapRoomNode(AbstractDungeon.currMapNode);
        addRuntime("load 1", System.currentTimeMillis() - startLoad);

        AbstractDungeon.isScreenUp = isScreenUp;
        AbstractDungeon.screen = screen;

        AbstractDungeon.previousScreen = previousScreen;

        listState.loadLists();

        AbstractDungeon.dungeonMapScreen.close();
        AbstractDungeon.floorNum = floorNum;
        AbstractDungeon.actionManager.mantraGained = mantraGained;

        GameActionManager.totalDiscardedThisTurn = totalDiscardedThisTurn;

        AbstractDungeon.gridSelectScreen.selectedCards.clear();
        AbstractDungeon.gridSelectScreen.confirmButton.hb.clicked = false;
        if (handSelectScreenState != null) {
            handSelectScreenState.loadHandSelectScreenState();
        } else if (gridCardSelectScreenState != null) {
            gridCardSelectScreenState.loadGridSelectScreen();
        } else if (cardRewardScreenState != null) {
            cardRewardScreenState.loadCardRewardScreen();
        }

        if (!shouldGoFast && !isScreenUp) {
            CombatRewardScreenState.loadCombatRewardScreen();
        }

        ArrayList<AbstractCard> allCards = new ArrayList<>();

        addRuntime("load 2", System.currentTimeMillis() - startLoad);
        AbstractPlayer player = AbstractDungeon.player;

        allCards.addAll(player.masterDeck.group);
        allCards.addAll(player.drawPile.group);
        allCards.addAll(player.hand.group);
        allCards.addAll(player.discardPile.group);
        allCards.addAll(player.exhaustPile.group);
        allCards.addAll(player.limbo.group);


        AbstractDungeon.actionManager.cardsPlayedThisTurn.clear();
//        AbstractDungeon.gridSelectScreen.selectedCards.clear();
        AbstractDungeon.actionManager.cardsPlayedThisCombat.clear();

        CountLessonLearnedHitsPatch.count = lessonLearnedCount;
        CountParasitesPatch.count = parasiteCount;

        this.cardsPlayedThisTurn.forEach(index -> AbstractDungeon.actionManager.cardsPlayedThisTurn
                .add(allCards.get(index)));
        this.cardsPlayedThisTurnBackup
                .forEach(card -> AbstractDungeon.actionManager.cardsPlayedThisTurn
                        .add(card.loadCard()));

        this.cardsPlayedThisCombat
                .forEach(container -> AbstractDungeon.actionManager.cardsPlayedThisCombat
                        .add(container.loadCard(allCards)));
        AbstractDungeon.gridSelectScreen.selectedCards.clear();
        this.gridSelectedCards.forEach(container -> AbstractDungeon.gridSelectScreen.selectedCards
                .add(container.loadCard(allCards)));
        addRuntime("load 3", System.currentTimeMillis() - startLoad);

        if (!this.gridSelectedCards.isEmpty()) {
//            System.err
//                    .println("there were selected cards " + this.gridSelectedCards + " " + allCards
//                            .get(this.gridSelectedCards.get(0)));
        }

        DrawCardAction.drawnCards.clear();
        this.drawnCards.stream().filter(index -> index != -1)
                       .forEach(index -> DrawCardAction.drawnCards.add(allCards.get(index)));


        AbstractDungeon.getCurrRoom().monsters.monsters.forEach(AbstractMonster::applyPowers);
        AbstractDungeon.player.hand.applyPowers();

        rngState.loadRng(floorNum);

        addRuntime("load total", System.currentTimeMillis() - startLoad);
        AbstractDungeon.player.isEndingTurn = false;
        AbstractDungeon.player.endTurnQueued = false;
        AbstractDungeon.overlayMenu.endTurnButton.enable();

        AbstractDungeon.player.endTurnQueued = endTurnQueued;
        AbstractDungeon.player.isEndingTurn = isEndingTurn;

        ReflectionHacks
                .setPrivate(AbstractDungeon.gridSelectScreen, GridCardSelectScreen.class, "hoveredCard", null);
        ReflectionHacks.setPrivateStatic(TheBombPower.class, "bombIdOffset", bombIdOffset);

        StateFactories.elementFactories.entrySet().stream().forEach(entry -> {
            additionalElements.get(entry.getKey()).restore();
        });
    }

    public void loadInitialState() {
        if (shouldGoFast) {
            System.err.println("Skipping Splash Screen for Char Select");

            // Sets the current dungeon
            Settings.seed = 123L;
            AbstractDungeon.generateSeeds();
            AbstractDungeon.screen = null;
            AbstractDungeon.previousScreen = null;

            // TODO this needs to be the actual character class or bad things happen
            new Exordium(CardCrawlGame.characterManager
                    .getCharacter(AbstractPlayer.PlayerClass.IRONCLAD), new ArrayList<>());

            AbstractDungeon.currMapNode.room = new EmptyRoom();

            CardCrawlGame.mode = CardCrawlGame.GameMode.GAMEPLAY;

            AbstractDungeon.gridSelectScreen = new GridCardSelectScreen();

        }

        loadState();
    }

    public int getPlayerHealth() {
        return playerState.getCurrentHealth();
    }

    public int getNumSlimes() {
        return playerState.getNumSlimes();
    }

    public String encode() {
        JsonObject saveStateJson = new JsonObject();

        saveStateJson.addProperty("floor_num", floorNum);

        saveStateJson.addProperty("previous_screen_up", previousScreenUp);
        saveStateJson.addProperty("my_turn", myTurn);
        saveStateJson.addProperty("turn", turn);

        saveStateJson.addProperty("screen_name", screen.name());
        saveStateJson.addProperty("previous_screen_name", previousScreen != null ? previousScreen
                .name() : null);

        saveStateJson.addProperty("list_state", listState.encode());
        saveStateJson.addProperty("player_state", playerState.encode());
        saveStateJson.addProperty("rng_state", rngState.encode());

        saveStateJson.addProperty("cur_map_node_state", curMapNodeState.encode());
        saveStateJson.addProperty("is_screen_up", isScreenUp);
        saveStateJson.addProperty("ascension_level", ascensionLevel);

        saveStateJson.addProperty("mantra_gained", mantraGained);

        saveStateJson.addProperty("bomb_id_offset", bombIdOffset);

        StateFactories.elementFactories.entrySet().stream().forEach(entry -> {
            saveStateJson
                    .addProperty(entry.getKey(), additionalElements.get(entry.getKey()).encode());
        });

        System.err.println("completed encoding");
        return saveStateJson.toString();
    }

    public String diffEncode() {
        JsonObject saveStateJson = new JsonObject();

        saveStateJson.addProperty("rng_state", rngState.encode());
        saveStateJson.addProperty("cur_map_node_state", curMapNodeState.diffEncode());
        saveStateJson.addProperty("player_state", playerState.diffEncode());
        saveStateJson.addProperty("total_discarded_this_turn", totalDiscardedThisTurn);

        return saveStateJson.toString();
    }

    public static boolean diff(String diffString1, String diffString2) {
        JsonObject one = new JsonParser().parse(diffString1).getAsJsonObject();
        JsonObject two = new JsonParser().parse(diffString2).getAsJsonObject();

        boolean allMatch = true;

        boolean playerMatch = PlayerState
                .diff(one.get("player_state").getAsString(), two.get("player_state").getAsString());
        if (!playerMatch) {
            allMatch = false;
            System.err.println("player state mismatch");
        }

        boolean roomMatch = MapRoomNodeState
                .diff(one.get("cur_map_node_state").getAsString(), two.get("cur_map_node_state")
                                                                      .getAsString());
        if (!roomMatch) {
            allMatch = false;
            System.err.println("room state mismatch");
        }

        boolean rngMatch = one.get("rng_state").getAsString()
                              .equals(two.get("rng_state").getAsString());
        if (!rngMatch) {
            allMatch = false;
            System.err.println(one.get("rng_state").getAsString());
            System.err.println("----------------------------------------------");
            System.err.println(two.get("rng_state").getAsString());
        }

        boolean discardCountMatch = one.get("total_discarded_this_turn").getAsInt() ==
                two.get("total_discarded_this_turn").getAsInt();
        if (!discardCountMatch) {
            allMatch = false;
            System.err.println(one.get("total_discarded_this_turn").getAsInt());
            System.err.println("----------------------------------------------");
            System.err.println(two.get("total_discarded_this_turn").getAsInt());
        }


        return allMatch;
    }

    public static class CardStateContainer {
        private int cardIndex = -1;
        private CardState cardState = null;

        public static CardStateContainer forCard(AbstractCard card, ArrayList<AbstractCard> allCards) {
            CardStateContainer result = new CardStateContainer();
            int index = allCards.indexOf(card);
            if (index == -1) {
                // Powers don't have indeces
                result.cardState = CardState.forCard(card);
            } else {
                result.cardIndex = index;
            }

            return result;
        }

        public AbstractCard loadCard(ArrayList<AbstractCard> allCards) {
            if (cardIndex == -1) {
                return cardState.loadCard();
            } else {
                return allCards.get(cardIndex);
            }
        }
    }

    @SpirePatch(clz = LessonLearnedAction.class, method = "update")
    public static class CountLessonLearnedHitsPatch {
        public static int count = 0;

        @SpireInsertPatch(loc = 47)
        public static void incrementNumber(LessonLearnedAction action) {
            count++;
        }
    }

    @SpirePatch(clz = AddCardToDeckAction.class, method = "update")
    public static class CountParasitesPatch {
        public static int count = 0;

        @SpirePrefixPatch
        public static void incrementNumber(AddCardToDeckAction action) {
            count++;
        }
    }
}
