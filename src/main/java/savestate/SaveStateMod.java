package savestate;

import basemod.BaseMod;
import basemod.TopPanelItem;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.PreUpdateSubscriber;
import com.badlogic.gdx.graphics.Texture;
import com.codedisaster.steamworks.SteamException;
import com.codedisaster.steamworks.SteamRemoteStorage;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.integrations.steam.SRCallback;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

@SpireInitializer
public class SaveStateMod implements PostInitializeSubscriber, PreUpdateSubscriber {
    /**
     * If true, states will be saved and loaded in ways that prioritize speed and function at the
     * cost of graphical elements.
     * <p>
     * If you'd like to save states at rates of 10k+ per second set this to true.
     */
    public static boolean shouldGoFast = false;

    /**
     * The simple Proof-of-concept UI for saving state will be two buttons, one to load state and
     * one to save state.  To that end, store a single static save state.
     */
    public static SaveState saveState;

    public static boolean shouldResetDungeon = false;

    public static void initialize() {
        BaseMod.subscribe(new SaveStateMod());
    }

    public static HashMap<String, Long> runTimes;

    @Override
    public void receivePostInitialize() {
        BaseMod.addTopPanelItem(new SaveStateTopPanel());
        BaseMod.addTopPanelItem(new LoadStateTopPanel());
//        BaseMod.addTopPanelItem(new TestThingPanel());
    }

    public class SaveStateTopPanel extends TopPanelItem {
        public static final String ID = "savestatemod:savestate";

        public SaveStateTopPanel() {
            super(new Texture("savestate.png"), ID);
        }

        @Override
        protected void onClick() {
            saveState = new SaveState();
        }
    }

    public class LoadStateTopPanel extends TopPanelItem {
        public static final String ID = "savestatemod:loadstate";

        public LoadStateTopPanel() {
            super(new Texture("loadstate.png"), ID);
        }

        @Override
        protected void onClick() {
            if (saveState != null) {
                saveState.loadState();
            }
        }
    }

    public class TestThingPanel extends TopPanelItem {
        public static final String ID = "savestatemod:loadstate";

        public TestThingPanel() {
            super(new Texture("loadstate.png"), ID);
        }

        @Override
        protected void onClick() {
            SteamRemoteStorage remoteStorage = new SteamRemoteStorage(new SRCallback());

            try {
                remoteStorage.fileWrite("testfile", ByteBuffer.allocateDirect(50)
                                                              .put("hello".getBytes()), 50);
            } catch (SteamException e) {
                e.printStackTrace();
            }

            ByteBuffer buffer = ByteBuffer.allocateDirect(50);
            try {
                remoteStorage.fileRead("testfile", buffer, 50);
                while (buffer.hasRemaining()) {
                    System.err.println((char) buffer.get());
                }
                System.err.println("done reading");
            } catch (SteamException e) {
                e.printStackTrace();
            }

            System.err.println("done trying");

            System.err.println(remoteStorage.getFileCount());
        }
    }

    @Override
    public void receivePreUpdate() {
        if (shouldResetDungeon) {
            shouldResetDungeon = false;
            new Exordium(AbstractDungeon.player, new ArrayList<>());
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
}
