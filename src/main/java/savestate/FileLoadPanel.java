package savestate;

import basemod.ModButton;
import basemod.ModLabel;
import basemod.ModPanel;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.CharacterManager;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import com.megacrit.cardcrawl.screens.DungeonTransitionScreen;
import com.megacrit.cardcrawl.screens.options.DropdownMenu;
import com.megacrit.cardcrawl.screens.options.DropdownMenuListener;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class FileLoadPanel extends ModPanel implements DropdownMenuListener {
    /**
     * Will be populated with the saves that can be loaded
     */
    public static HashMap<String, HashMap<Long, HashSet<Integer>>> classToSeedsMap;

    public static DropdownMenu characters;
    public static DropdownMenu seeds;
    public static DropdownMenu floors;

    public static String selectedCharacter = null;
    public static Long selectedSeed = null;
    public static Integer selectedFloor = null;

    private static CardCrawlGame game;

    public FileLoadPanel() {
        ModLabel helloWorldLabel = new ModLabel(
                "", 350, 600, Settings.CREAM_COLOR, FontHelper.charDescFont,
                this, modLabel -> {
            modLabel.text = "hello world";
        });
        this.addUIElement(helloWorldLabel);

        ModButton refreshSavesButton = new ModButton(350, 650, this, modButton -> refreshSaveFiles(this));
        this.addUIElement(refreshSavesButton);

        ModButton loadSelectedSave = new ModButton(550, 650, this, modButton -> loadSaveFile());
        this.addUIElement(loadSelectedSave);

        characters = new DropdownMenu(this, new String[]{}, FontHelper.tipBodyFont, Settings.CREAM_COLOR);
    }

    @Override
    public void update() {
        super.update();
        characters.update();

        if (seeds != null) {
            seeds.update();
        }

        if (floors != null) {
            floors.update();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);

        characters.render(sb, 500, 500);

        if (selectedCharacter != null) {
            seeds.render(sb, characters.approximateOverallWidth() + 500 + 50, 500);
        }

        if (selectedSeed != null) {
            floors.render(sb, characters.approximateOverallWidth() + seeds
                    .approximateOverallWidth() + 500 + 100, 500);
        }
    }

    @Override
    public void changedSelectionTo(DropdownMenu dropdownMenu, int i, String s) {
        if (dropdownMenu == characters) {
            selectedCharacter = s;

            ArrayList<String> seedStrings = classToSeedsMap.get(s).keySet().stream()
                                                           .map(l -> Long.toString(l))
                                                           .collect(Collectors
                                                                   .toCollection(ArrayList::new));

            seeds = new DropdownMenu(this, seedStrings, FontHelper.tipBodyFont, Settings.CREAM_COLOR);

            selectedSeed = null;
            selectedFloor = null;
        } else if (dropdownMenu == seeds) {
            selectedSeed = Long.parseLong(s);

            ArrayList<String> floorStrings = classToSeedsMap.get(selectedCharacter)
                                                            .get(selectedSeed).stream()
                                                            .map(integer -> Integer
                                                                    .toString(integer))
                                                            .collect(Collectors
                                                                    .toCollection(ArrayList::new));

            floors = new DropdownMenu(this, floorStrings, FontHelper.tipBodyFont, Settings.CREAM_COLOR);

            selectedFloor = null;
        } else if (dropdownMenu == floors) {
            selectedFloor = Integer.parseInt(s);
        }
    }

    private static void refreshSaveFiles(FileLoadPanel panel) {
        System.err.println("refreshing");
        File savealls = new File("savealls\\saves");

        File[] saveFiles = savealls.listFiles();
        ArrayList<String> saveNames = Arrays.stream(saveFiles)
                                            .sorted(Comparator.comparing(File::lastModified)
                                                              .reversed())
                                            .map(file -> file.getName())
                                            .collect(Collectors.toCollection(ArrayList::new));
        classToSeedsMap = new HashMap<>();

        for (String name : saveNames) {
            String[] tokens = name.split("\\.");

            if (tokens.length == 3) {
                // This is a backup file, ignore
                continue;
            }

            String[] floorTokens = tokens[1].split("_");

            String playerClass = tokens[0];
            int floorNum = Integer.parseInt(floorTokens[1]);
            long seed = Long.parseLong(floorTokens[2]);

            if (!classToSeedsMap.containsKey(playerClass)) {
                classToSeedsMap.put(playerClass, new HashMap<>());
            }

            HashMap<Long, HashSet<Integer>> seedToFloorsMap = classToSeedsMap
                    .get(playerClass);
            if (!seedToFloorsMap.containsKey(seed)) {
                seedToFloorsMap.put(seed, new HashSet<>());
            }
            seedToFloorsMap.get(seed).add(floorNum);
        }

        ArrayList<String> characters = new ArrayList<>();
        characters.addAll(classToSeedsMap.keySet());

        FileLoadPanel.characters = new DropdownMenu(panel, characters, FontHelper.tipBodyFont, Settings.CREAM_COLOR);

        FileLoadPanel.selectedSeed = null;
        FileLoadPanel.selectedCharacter = null;
        FileLoadPanel.selectedFloor = null;
    }

    private static void loadSaveFile() {
        AbstractDungeon.player = new CharacterManager()
                .getCharacter(AbstractPlayer.PlayerClass.valueOf(FileLoadPanel.selectedCharacter));

        CardCrawlGame.loadingSave = true;
        CardCrawlGame.chosenCharacter = AbstractDungeon.player.chosenClass;
        CardCrawlGame.mainMenuScreen.isFadingOut = true;
        CardCrawlGame.mainMenuScreen.fadeOutMusic();
        Settings.isDailyRun = false;
        Settings.isTrial = false;
        ModHelper.setModsFalse();
        if (CardCrawlGame.steelSeries.isEnabled) {
            CardCrawlGame.steelSeries.event_character_chosen(CardCrawlGame.chosenCharacter);
        }

        ReflectionHacks.privateMethod(CardCrawlGame.class, "loadPlayerSave", AbstractPlayer.class)
                       .invoke(game, AbstractDungeon.player);

        CardCrawlGame.mode = CardCrawlGame.GameMode.GAMEPLAY;
        CardCrawlGame.nextDungeon = "Exordium";
        CardCrawlGame.dungeonTransitionScreen = new DungeonTransitionScreen("Exordium");
        CardCrawlGame.dungeonTransitionScreen.isComplete = true;
    }

    @SpirePatch(clz = CardCrawlGame.class, method = SpirePatch.CONSTRUCTOR)
    public static class getCCGInstancePatch {
        @SpirePostfixPatch
        public static void getInstance(CardCrawlGame game, String prefDir) {
            FileLoadPanel.game = game;
        }
    }

    @SpirePatch(clz = SaveAndContinue.class, method = "getPlayerSavePath")
    public static class LoadSelectedSaveFilePatch {
        @SpirePrefixPatch
        public static SpireReturn<String> loadSpecificSave(AbstractPlayer.PlayerClass c) {
            if (FileLoadPanel.selectedFloor != null) {
                String fileName = String
                        .format("savealls\\saves\\%s.autosave_%02d_%d", FileLoadPanel.selectedCharacter, FileLoadPanel.selectedFloor, FileLoadPanel.selectedSeed);

                return SpireReturn.Return(fileName);
            }

            return SpireReturn.Continue();
        }
    }
}