package savestate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import savestate.selectscreen.GridCardSelectScreenState;
import savestate.selectscreen.HandSelectScreenState;

import java.util.ArrayList;

import static savestate.SaveStateMod.shouldGoFast;

public class SaveState {
    private final boolean isScreenUp;
    int floorNum;
    boolean previousScreenUp;
    boolean myTurn = false;
    public int turn;
    private int totalDiscardedThisTurn;


    private final ArrayList<Integer> cardsPlayedThisTurn;
    private final ArrayList<Integer> gridSelectedCards;
    private final ArrayList<Integer> drawnCards;

    // Load cards from scratch if necessary, ideally they'll be released elsewhere
    private final ArrayList<CardState> cardsPlayedThisTurnBackup;

    AbstractDungeon.CurrentScreen screen;

    ListState listState;
    public PlayerState playerState;
    private HandSelectScreenState handSelectScreenState = null;
    private GridCardSelectScreenState gridCardSelectScreenState = null;
    RngState rngState;
    private final int ascensionLevel;

    public MapRoomNodeState curMapNodeState;

    public SaveState() {
        if (AbstractDungeon.isScreenUp) {
            if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.HAND_SELECT) {
                handSelectScreenState = new HandSelectScreenState();
            }

            if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.GRID) {
                gridCardSelectScreenState = new GridCardSelectScreenState();
            }
        }

        this.curMapNodeState = new MapRoomNodeState(AbstractDungeon.currMapNode);
        this.playerState = new PlayerState(AbstractDungeon.player);
        this.screen = AbstractDungeon.screen;
        this.rngState = new RngState();
        this.listState = new ListState();
        this.floorNum = AbstractDungeon.floorNum;
        this.turn = GameActionManager.turn;
        this.isScreenUp = AbstractDungeon.isScreenUp;
        this.ascensionLevel = AbstractDungeon.ascensionLevel;
        this.totalDiscardedThisTurn = GameActionManager.totalDiscardedThisTurn;

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
                        this.cardsPlayedThisTurnBackup.add(new CardState(card));
                    } else {
                        this.cardsPlayedThisTurn.add(allCards.indexOf(card));
                    }
                });

        this.gridSelectedCards = new ArrayList<>();

        AbstractDungeon.gridSelectScreen.selectedCards
                .forEach(card -> this.gridSelectedCards.add(allCards.indexOf(card)));

        this.drawnCards = new ArrayList<>();
        DrawCardAction.drawnCards.forEach(card -> this.drawnCards.add(allCards.indexOf(card)));
    }

    public SaveState(String jsonString) {
        System.err.println("beginning parse....");

        JsonObject parsed = new JsonParser().parse(jsonString).getAsJsonObject();

        this.floorNum = parsed.get("floor_num").getAsInt();
        this.previousScreenUp = parsed.get("previous_screen_up").getAsBoolean();
        this.myTurn = parsed.get("my_turn").getAsBoolean();
        this.turn = parsed.get("turn").getAsInt();

        this.screen = AbstractDungeon.CurrentScreen
                .valueOf(parsed.get("screen_name").getAsString());
        this.listState = new ListState(parsed.get("list_state").getAsString());

        System.err.println("parsing player....");

        this.playerState = new PlayerState(parsed.get("player_state").getAsString());
        this.rngState = new RngState(parsed.get("rng_state").getAsString());

        System.err.println("parsing room....");

        this.curMapNodeState = new MapRoomNodeState(parsed.get("cur_map_node_state").getAsString());
        this.isScreenUp = parsed.get("is_screen_up").getAsBoolean();
        this.ascensionLevel = parsed.get("ascension_level").getAsInt();

        // TODO
        this.handSelectScreenState = null;
        this.cardsPlayedThisTurn = new ArrayList<>();
        this.cardsPlayedThisTurnBackup = new ArrayList<>();
        this.gridSelectedCards = new ArrayList<>();
        this.drawnCards = new ArrayList<>();
    }

    public void loadState() {
        AbstractDungeon.actionManager.currentAction = null;
        AbstractDungeon.actionManager.actions.clear();

        AbstractDungeon.ascensionLevel = this.ascensionLevel;
        GameActionManager.turn = this.turn;

        AbstractDungeon.player = playerState.loadPlayer();
        curMapNodeState.loadMapRoomNode(AbstractDungeon.currMapNode);

        AbstractDungeon.isScreenUp = isScreenUp;
        AbstractDungeon.screen = screen;

        listState.loadLists();

        AbstractDungeon.dungeonMapScreen.close();
        AbstractDungeon.floorNum = floorNum;

        GameActionManager.totalDiscardedThisTurn = totalDiscardedThisTurn;

        if (handSelectScreenState != null) {
            handSelectScreenState.loadHandSelectScreenState();
        } else if (gridCardSelectScreenState != null) {
            gridCardSelectScreenState.loadGridSelectScreen();
        }

        if (!shouldGoFast && !isScreenUp) {
            CombatRewardScreenState.loadCombatRewardScreen();
        }

        ArrayList<AbstractCard> allCards = new ArrayList<>();

        AbstractPlayer player = AbstractDungeon.player;

        allCards.addAll(player.masterDeck.group);
        allCards.addAll(player.drawPile.group);
        allCards.addAll(player.hand.group);
        allCards.addAll(player.discardPile.group);
        allCards.addAll(player.exhaustPile.group);
        allCards.addAll(player.limbo.group);

        AbstractDungeon.actionManager.cardsPlayedThisTurn.clear();
        AbstractDungeon.gridSelectScreen.selectedCards.clear();

        this.cardsPlayedThisTurn.forEach(index -> AbstractDungeon.actionManager.cardsPlayedThisTurn
                .add(allCards.get(index)));
        this.cardsPlayedThisTurnBackup
                .forEach(card -> AbstractDungeon.actionManager.cardsPlayedThisTurn
                        .add(card.loadCard()));
        AbstractDungeon.gridSelectScreen.selectedCards.clear();
        this.gridSelectedCards.forEach(index -> AbstractDungeon.gridSelectScreen.selectedCards
                .add(allCards.get(index)));

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

        saveStateJson.addProperty("list_state", listState.encode());
        saveStateJson.addProperty("player_state", playerState.encode());
        saveStateJson.addProperty("rng_state", rngState.encode());

        saveStateJson.addProperty("cur_map_node_state", curMapNodeState.encode());
        saveStateJson.addProperty("is_screen_up", isScreenUp);
        saveStateJson.addProperty("ascension_level", ascensionLevel);

        System.err.println("completed encoding");
        return saveStateJson.toString();
    }

    public String diffEncode() {
        JsonObject saveStateJson = new JsonObject();

        saveStateJson.addProperty("rng_state", rngState.encode());
        saveStateJson.addProperty("cur_map_node_state", curMapNodeState.diffEncode());
        saveStateJson.addProperty("player_state", playerState.diffEncode());

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

        boolean rngMatch = one.get("rng_state").getAsString().equals(two.get("rng_state").getAsString());
        if(!rngMatch) {
            allMatch = false;
            System.err.println(one.get("rng_state").getAsString());
            System.err.println("----------------------------------------------");
            System.err.println(two.get("rng_state").getAsString());
        }

        return allMatch;
    }
}
