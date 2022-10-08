package savestate;

import basemod.ModLabel;
import basemod.ModPanel;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.options.DropdownMenu;
import com.megacrit.cardcrawl.screens.options.DropdownMenuListener;

public class SaveStateModPanel extends ModPanel implements DropdownMenuListener {
    private static final int MAX_SAVE_STATES = 5;

    private static final float LABEL_X_POS = Settings.WIDTH / 5.0F;
    private static final float LABEL_Y_POS = Settings.HEIGHT * 2.0F / 3.0F;

    public static DropdownMenu numSaveStates;

    public SaveStateModPanel() {
        ModLabel controllerModeLabel = new ModLabel(
                "", LABEL_X_POS / Settings.scale, LABEL_Y_POS / Settings.scale, Settings.CREAM_COLOR, FontHelper.charDescFont,
                this, modLabel -> {
            modLabel.text = "Save State Count: ";
        });
        this.addUIElement(controllerModeLabel);

        String[] values = new String[MAX_SAVE_STATES + 1];
        for (int i = 0; i < values.length; i++) {
            values[i] = Integer.toString(i);
        }

        numSaveStates = new DropdownMenu(this, values, FontHelper.tipBodyFont, Settings.CREAM_COLOR);
        numSaveStates.setSelectedIndex(SaveStateController.numSaveStates);
    }

    @Override
    public void update() {
        super.update();
        numSaveStates.update();
    }

    @Override
    public void changedSelectionTo(DropdownMenu dropdownMenu, int i, String s) {
        if (dropdownMenu == numSaveStates) {
            SaveStateController.saveNumSaveStates(i);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);

        numSaveStates
                .render(sb, LABEL_X_POS + 350 * Settings.scale, LABEL_Y_POS + 22 * Settings.scale);
    }
}