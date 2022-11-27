package savestate;

import basemod.BaseMod;
import basemod.interfaces.OnStartBattleSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.PostUpdateSubscriber;
import basemod.interfaces.RenderSubscriber;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@SpireInitializer
public class SaveStateMod implements PostInitializeSubscriber, RenderSubscriber, OnStartBattleSubscriber, PostUpdateSubscriber {

    /**
     * If true, states will be saved and loaded in ways that prioritize speed and function at the
     * cost of graphical elements.
     * <p>
     * If you'd like to save states at rates of 10k+ per second set this to true.
     */
    public static boolean shouldGoFast = false;

    public static SpireConfig optionsConfig;
    public static SaveStateController saveStateController;
    public static boolean shouldResetDungeon = false;
    public static HashMap<String, AbstractCard> cardNameToCardMap;

    public static void initialize() {
        BaseMod.subscribe(new SaveStateMod());
    }

    public static HashMap<String, Long> runTimes;

    public static int lastFloorToDisplay = 0;
    public static int curFloorToDisplay = 1;

    public SaveStateMod() {
        try {
            optionsConfig = new SpireConfig("SaveStateMod", "options");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receivePostInitialize() {
        saveStateController = new SaveStateController();
        SaveStateController.numSaveStates = SaveStateController.getNumSaveStates();

        BaseMod.registerModBadge(ImageMaster
                .loadImage("Icon.png"), "SaveState Mod", "Board Engineer", null, new SaveStateModPanel());

        HashMap<String, AbstractCard> cards = CardLibrary.cards;
        StateFactories.cardIds = new String[cards.size()];
        int index = 0;
        for (String cardId : cards.keySet()) {
            StateFactories.cardIds[index] = cardId;
            StateFactories.cardIdToIndexMap.put(cardId, index);
            index++;
        }

        cardNameToCardMap = new HashMap<>();
        for (AbstractCard card : CardLibrary.getAllCards()) {
            cardNameToCardMap.put(prepCardName(card.cardID), card.makeCopy());
        }
    }

    @SpirePatch(clz = PotionHelper.class, method = "initialize")
    public static class RemoveUnpalayablePotionsPatch {
        @SpirePostfixPatch
        public static void removeUnplayables(AbstractPlayer.PlayerClass playerClass) {
            PotionHelper.potions.removeAll(PotionState.UNPLAYABLE_POTIONS);
        }
    }

    public static void addRuntime(String name, long amount) {
        if (runTimes == null) {
            runTimes = new HashMap<>();
        }

        if (!runTimes.containsKey(name)) {
            runTimes.put(name, amount);
        } else {
            runTimes.put(name, amount + runTimes.get(name));
        }
    }

    @Override
    public void receiveRender(SpriteBatch spriteBatch) {
        saveStateController.render(spriteBatch);
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom) {
        System.err.println("starting battle");
        saveStateController.initialize();

        for (HashMap map : CardCrawlGame.metricData.event_choices) {
            System.err.println(map);
        }

        System.err.println(CardCrawlGame.metricData.items_purchased);
        System.err.println(CardCrawlGame.metricData.item_purchase_floors);
    }

    @Override
    public void receivePostUpdate() {
        saveStateController.update();

        if (!isShowingCards()) {
            if (curFloorToDisplay <= lastFloorToDisplay) {
                int lookingForFloor = curFloorToDisplay;
                curFloorToDisplay++;

                for (HashMap map : CardCrawlGame.metricData.event_choices) {
                    int floor = 0;

                    try {
                        double choiceFloor = (double) map.get("floor");
                        floor = (int) choiceFloor;
                    } catch (ClassCastException e) {
                        floor = (int) map.get("floor");
                    }

                    if (floor == lookingForFloor) {
                        if (map.containsKey("cards_obtained")) {
                            ArrayList cardList = (ArrayList) map.get("cards_obtained");

                            for (Object name : cardList) {
                                String preppedName = SaveStateMod.prepCardName((String) name);
                                boolean canMakeCard = SaveStateMod.cardNameToCardMap
                                        .containsKey(preppedName);
                                if (canMakeCard) {
                                    AbstractCard card = SaveStateMod.cardNameToCardMap
                                            .get(preppedName).makeStatEquivalentCopy();

                                    card.beginGlowing();
                                    card.glowColor = Color.GREEN;

                                    AbstractDungeon.effectList
                                            .add(new ShowCardBrieflyEffect(card));
                                } else {
                                    System.err.println("Can't show card " + preppedName);
                                }
                            }
                        }
                    }
                }

                for (int i = 0; i < CardCrawlGame.metricData.item_purchase_floors.size(); i++) {
                    int floor = CardCrawlGame.metricData.item_purchase_floors.get(i);

                    if (floor == lookingForFloor) {
                        String name = CardCrawlGame.metricData.items_purchased.get(i);

                        String preppedName = SaveStateMod.prepCardName(name);
                        boolean canMakeCard = SaveStateMod.cardNameToCardMap
                                .containsKey(preppedName);
                        if (canMakeCard) {
                            AbstractCard card = SaveStateMod.cardNameToCardMap
                                    .get(preppedName).makeStatEquivalentCopy();

                            card.beginGlowing();
                            card.glowColor = Color.GREEN;

                            AbstractDungeon.effectList
                                    .add(new ShowCardBrieflyEffect(card));
                        } else {
                            System.err.println("Can't show card " + preppedName);
                        }
                    }
                }

                for (HashMap map : CardCrawlGame.metricData.card_choices) {
                    double choiceFloor = (double) map.get("floor");
                    //48.0 map: {not_picked=[Stack, Beam Cell+1, Double Energy], picked=Reprogram, floor=48.0}
                    int floor = (int) choiceFloor;

                    if (floor == lookingForFloor) {
                        // Show Picked Cards Highlighted In Green
                        if (map.containsKey("picked")) {

                            String pickedName = (String) map.get("picked");
                            String preppedName = SaveStateMod.prepCardName(pickedName);
                            boolean canMakeCard = SaveStateMod.cardNameToCardMap
                                    .containsKey(preppedName);
                            if (canMakeCard) {
                                AbstractCard card = SaveStateMod.cardNameToCardMap
                                        .get(preppedName).makeStatEquivalentCopy();

                                card.beginGlowing();
                                card.glowColor = Color.GREEN;

                                AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(card));
                            }
                        }

                        // Show Unpicked Cards Highlighted in Red
                        if (map.containsKey("not_picked")) {
                            ArrayList cardList = (ArrayList) map.get("not_picked");

                            for (Object name : cardList) {
                                String preppedName = SaveStateMod.prepCardName((String) name);
                                boolean canMakeCard = SaveStateMod.cardNameToCardMap
                                        .containsKey(preppedName);
                                if (canMakeCard) {
                                    AbstractCard card = SaveStateMod.cardNameToCardMap
                                            .get(preppedName).makeStatEquivalentCopy();

                                    card.beginGlowing();
                                    card.glowColor = Color.RED;

                                    AbstractDungeon.effectList
                                            .add(new ShowCardBrieflyEffect(card));
                                } else {
                                    System.err.println("Can't show card " + preppedName);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean isShowingCards() {
        for (AbstractGameEffect effect : AbstractDungeon.effectList) {
            if (effect instanceof ShowCardBrieflyEffect) {
                return true;
            }
        }
        return false;
    }

    public static String prepCardName(String cardName) {
        String name = cardName.toLowerCase();
        name = name.replace("+", "");
        name = name.replace(" ", "");

        name = name.replace("1", "");
        name = name.replace("2", "");
        name = name.replace("3", "");
        name = name.replace("4", "");
        name = name.replace("5", "");
        name = name.replace("6", "");
        name = name.replace("7", "");
        name = name.replace("8", "");
        name = name.replace("9", "");
        name = name.replace("0", "");

        return name;
    }
}
