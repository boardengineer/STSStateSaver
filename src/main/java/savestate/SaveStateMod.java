package savestate;

import basemod.BaseMod;
import basemod.TopPanelItem;
import basemod.interfaces.PostInitializeSubscriber;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;

@SpireInitializer
public class SaveStateMod implements PostInitializeSubscriber {
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

    public static void initialize() {
        BaseMod.subscribe(new SaveStateMod());
    }

    @Override
    public void receivePostInitialize() {
        BaseMod.addTopPanelItem(new SaveStateTopPanel());
        BaseMod.addTopPanelItem(new LoadStateTopPanel());
    }

    public class SaveStateTopPanel extends TopPanelItem {
        public static final String ID = "savestatemod:savestate";

        public SaveStateTopPanel() {
            super(new Texture("save.png"), ID);
        }

        @Override
        protected void onClick() {
            saveState = new SaveState();
        }
    }

    public class LoadStateTopPanel extends TopPanelItem {
        public static final String ID = "savestatemod:loadstate";

        public LoadStateTopPanel() {
            super(new Texture("Icon.png"), ID);
        }

        @Override
        protected void onClick() {
            if (saveState != null) {
                saveState.loadState();
            }
        }
    }
}
