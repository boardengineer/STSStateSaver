package savestate;

import basemod.BaseMod;
import basemod.interfaces.OnStartBattleSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.PostUpdateSubscriber;
import basemod.interfaces.RenderSubscriber;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.io.IOException;
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


    public static void initialize() {
        BaseMod.subscribe(new SaveStateMod());
    }

    public static HashMap<String, Long> runTimes;

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
    }

    @Override
    public void receivePostUpdate() {
        saveStateController.update();
    }
}
