package savestate;

import basemod.ClickableUIElement;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.io.IOException;

public class SaveStateController {
    private static final String OPTION_KEY = "num_save_states";

    private static final float X_POSITION = 10F * Settings.scale;
    private static final float Y_POSITION_TOP = (Settings.HEIGHT - Settings.HEIGHT / 5);

    private static final Texture CONTROLLER_BACKGROUND = new Texture("ui/confirm.png");

    private static final Texture SAVE_TEXTURE = new Texture("ui/savestate.png");
    private static final Texture LOAD_TEXTURE = new Texture("ui/loadstate.png");

    private static final float STATE_PANEL_HEIGHT = Math
            .max(SAVE_TEXTURE.getHeight(), LOAD_TEXTURE.getHeight()) * 1.25f * Settings.scale;

    private static final float SAVE_BUTTON_X = X_POSITION + SAVE_TEXTURE
            .getWidth() * .5F * Settings.scale;
    private static final float LOAD_BUTTON_X = SAVE_BUTTON_X + SAVE_TEXTURE
            .getWidth() * 1.2F * Settings.scale;

    private static final float PANEL_WIDTH = 350F * Settings.scale;

    private SaveState[] savedStates;
    private StatePanel[] statePanels;

    public static int numSaveStates = 0;

    public void initialize() {
        savedStates = new SaveState[numSaveStates];
        statePanels = new StatePanel[numSaveStates];
        for (int i = 0; i < statePanels.length; i++) {
            statePanels[i] = new StatePanel(i);
        }
    }

    public void update() {
        if (savedStates != null && statePanels != null) {
            for (int i = 0; i < statePanels.length; i++) {
                statePanels[i].update();
            }
        }
    }

    public void render(SpriteBatch sb) {
        if (inCombat() && !SaveStateMod.shouldGoFast && savedStates != null) {
            sb.setColor(Color.WHITE);
            // Render Panel Background
            float height = savedStates.length * STATE_PANEL_HEIGHT;

            sb.draw(CONTROLLER_BACKGROUND, X_POSITION, Y_POSITION_TOP - height, PANEL_WIDTH, height);

            for (int i = 0; i < statePanels.length; i++) {
                statePanels[i].render(sb);
            }
        }
    }

    public static void saveNumSaveStates(int numSaveStates) {
        SpireConfig config = SaveStateMod.optionsConfig;
        if (config != null) {
            SaveStateController.numSaveStates = numSaveStates;
            config.setInt(OPTION_KEY, numSaveStates);
            try {
                config.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getNumSaveStates() {
        SpireConfig config = SaveStateMod.optionsConfig;
        if (config != null && config.has(OPTION_KEY)) {
            return config.getInt(OPTION_KEY);
        }
        return 0;
    }

    private static boolean inCombat() {
        return CardCrawlGame.isInARun() && AbstractDungeon.currMapNode != null && AbstractDungeon
                .getCurrRoom() != null && AbstractDungeon
                .getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT;
    }

    class StatePanel {
        private final int saveIndex;
        private final SaveButton saveButton;
        private final LoadButton loadButton;

        public StatePanel(int saveIndex) {
            this.saveIndex = saveIndex;

            this.saveButton = new SaveButton(saveIndex);
            this.loadButton = new LoadButton(saveIndex);
        }

        public void render(SpriteBatch spriteBatch) {
            saveButton.render(spriteBatch);
            loadButton.render(spriteBatch);

            float textX = LOAD_BUTTON_X + LOAD_TEXTURE.getWidth() * 1.2F * Settings.scale;
            float textY = Y_POSITION_TOP - ((saveIndex + 1) * STATE_PANEL_HEIGHT) +
                    STATE_PANEL_HEIGHT * .70F;

            FontHelper
                    .renderFont(spriteBatch, FontHelper.tipBodyFont, stateString(savedStates[saveIndex]), textX, textY, Settings.GREEN_TEXT_COLOR);
        }

        public void update() {
            saveButton.update();
            loadButton.update();
        }

        public SaveState getState() {
            return savedStates[saveIndex];
        }

        class SaveButton extends ClickableUIElement {
            public SaveButton(int index) {
                super(SAVE_TEXTURE);

                x = SAVE_BUTTON_X;
                y = Y_POSITION_TOP - ((index + 1) * STATE_PANEL_HEIGHT) +
                        SAVE_TEXTURE.getHeight() / 8F;

                hitbox = new Hitbox(x, y, SAVE_TEXTURE.getWidth(), SAVE_TEXTURE.getHeight());
            }

            @Override
            protected void onHover() {
                this.angle = MathHelper.angleLerpSnap(this.angle, 15.0F);
                this.tint.a = 0.25F;
            }

            @Override
            protected void onUnhover() {
                this.angle = MathHelper.angleLerpSnap(this.angle, 0.0F);
                this.tint.a = 0.0F;
            }

            @Override
            protected void onClick() {
                savedStates[saveIndex] = new SaveState();
            }
        }

        class LoadButton extends ClickableUIElement {
            public LoadButton(int index) {
                super(LOAD_TEXTURE);

                x = LOAD_BUTTON_X;
                y = Y_POSITION_TOP - ((index + 1) * STATE_PANEL_HEIGHT) +
                        LOAD_TEXTURE.getHeight() / 8F;

                hitbox = new Hitbox(x, y, LOAD_TEXTURE.getWidth(), LOAD_TEXTURE.getHeight());
            }

            @Override
            protected void onHover() {
                this.angle = MathHelper.angleLerpSnap(this.angle, 15.0F);
                this.tint.a = 0.25F;
            }

            @Override
            protected void onUnhover() {
                this.angle = MathHelper.angleLerpSnap(this.angle, 0.0F);
                this.tint.a = 0.0F;
            }

            @Override
            protected void onClick() {
                if (savedStates[saveIndex] != null) {
                    savedStates[saveIndex].loadState();
                }
            }
        }
    }

    private static String stateString(SaveState saveState) {
        if (saveState == null) {
            return "(empty)";
        }
        return String
                .format("Turn %02d \t Energy %d/%d", saveState.turn, saveState.playerState.energyPanelTotalEnergy, saveState.playerState.energyManagerMaxMaster);
    }
}
