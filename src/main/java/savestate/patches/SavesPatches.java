package savestate.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.audio.MusicMaster;
import com.megacrit.cardcrawl.audio.Sfx;
import com.megacrit.cardcrawl.audio.SoundMaster;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.events.beyond.MindBloom;
import com.megacrit.cardcrawl.events.beyond.MysteriousSphere;
import com.megacrit.cardcrawl.events.city.Colosseum;
import com.megacrit.cardcrawl.events.city.MaskedBandits;
import com.megacrit.cardcrawl.events.exordium.DeadAdventurer;
import com.megacrit.cardcrawl.events.exordium.Mushrooms;
import com.megacrit.cardcrawl.monsters.exordium.SlimeBoss;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.saveAndContinue.SaveFileObfuscator;

import java.util.HashMap;

public class SavesPatches {
    public static String overridePath = null;
    public static CardCrawlGame cardCrawlGameInstance;

    private static String path = null;
    private static AbstractPlayer.PlayerClass playerClass = null;

    public static boolean recallMode = false;
    static boolean preventDouble = true;

    public static void load(String path, AbstractPlayer.PlayerClass playerClass) {
        preventDouble = false;
        SavesPatches.path = path;
        SavesPatches.playerClass = playerClass;
    }

    @SpirePatch(clz = CardCrawlGame.class, method = "update")
    public static class GetInstancePatch {
        @SpirePrefixPatch
        public static void saveThing(CardCrawlGame game) {
            cardCrawlGameInstance = game;
            if (path != null) {

                CardCrawlGame.music.dispose();

                Settings.AMBIANCE_ON = false;
                CardCrawlGame.music = new MusicMaster();

                HashMap<String, Sfx> map = ReflectionHacks
                        .getPrivate(CardCrawlGame.sound, SoundMaster.class, "map");

                map.values().forEach(sfx -> sfx.stop());
                CardCrawlGame.sound = new SoundMaster();


                System.err.println("effects list " + AbstractDungeon.effectList.size());
                System.err.println("effects queue " + AbstractDungeon.effectsQueue.size());
                System.err.println("actions queue " + AbstractDungeon.actionManager.actions.size());


                System.err.println("LOADING LOADING LOADING");
                CardCrawlGame.mode = CardCrawlGame.GameMode.CHAR_SELECT;
                CardCrawlGame.dungeon = null;
                CardCrawlGame.loadingSave = true;
                CardCrawlGame.chosenCharacter = playerClass;
//                AbstractPlayer ironclad = new CharacterManager()
//                        .recreateCharacter(AbstractPlayer.PlayerClass.IRONCLAD);
//                AbstractDungeon.player = ironclad;

                CardCrawlGame.playerName = "Twitch";

                SavesPatches.overridePath = path;
                SaveFile saveFile = SaveAndContinue.loadSaveFile(CardCrawlGame.chosenCharacter);

//                ReflectionHacks
//                        .privateMethod(CardCrawlGame.class, "getDungeon", String.class, AbstractPlayer.class, SaveFile.class)
//                        .invoke(cardCrawlGameInstance, saveFile.level_name, AbstractDungeon.player, saveFile);

                CardCrawlGame.mainMenuScreen.isFadingOut = true;
//                CardCrawlGame.mainMenuScreen.fadeOutMusic();


                Settings.isDailyRun = false;
                Settings.isTrial = false;

                path = null;
                playerClass = null;
            }
        }
    }

    @SpirePatch(clz = CardCrawlGame.class, method = "loadPlayerSave")
    public static class ClearSavePathPatch {
        @SpirePostfixPatch
        public static void saveThing(CardCrawlGame game, AbstractPlayer player) {
            overridePath = null;
        }
    }

    @SpirePatch(clz = SaveAndContinue.class, method = "getPlayerSavePath")
    public static class CustomPathPatch {
        @SpirePrefixPatch
        public static SpireReturn<String> returnCustomPath(AbstractPlayer.PlayerClass c) {
            if (overridePath != null) {
                String result = overridePath;
                return SpireReturn.Return(result);
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = SaveAndContinue.class, method = "loadSaveString", paramtypez = {String.class})
    public static class AbsolutePathsPatch {
        @SpirePrefixPatch
        public static SpireReturn<String> returnCustomPath(String path) {
            if (path.startsWith("C:\\")) {
                FileHandle file = Gdx.files.absolute(path);
                String data = file.readString();
                return SpireReturn.Return(SaveFileObfuscator.isObfuscated(data) ? SaveFileObfuscator
                        .decode(data, "key") : data);
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = SlimeBoss.class, method = "usePreBattleAction")
    public static class FixMusicPatch {
        @SpirePrefixPatch
        public static SpireReturn FixMusic(SlimeBoss slimeBoss) {
            System.err.println("slime boss patch");
            if (recallMode) {
                AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_BOTTOM");
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = MonsterRoomBoss.class, method = "onPlayerEntry")
    public static class FixBossMusicPatch {
        @SpirePrefixPatch
        public static SpireReturn FixMusic(MonsterRoomBoss monsterRoomBoss) {
            if (recallMode) {
                preventDouble = true;
                monsterRoomBoss.monsters = CardCrawlGame.dungeon.getBoss();

                if (monsterRoomBoss.monsters != null) {
                    monsterRoomBoss.monsters.init();
                }

                ReflectionHacks.setPrivate(monsterRoomBoss, AbstractRoom.class, "waitTimer", .1F);

                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = MindBloom.class, method = SpirePatch.CONSTRUCTOR)
    public static class FightMindBloomPatch {
        @SpirePostfixPatch
        public static void chooseWar(MindBloom mindBloom) {
            if (recallMode) {
                mindBloom.imageEventText.optionList.get(0).pressed = true;
            }
        }
    }

    @SpirePatch(clz = DeadAdventurer.class, method = SpirePatch.CONSTRUCTOR)
    public static class DeadAdventurerPatch {
        @SpirePostfixPatch
        public static void chooseWar(DeadAdventurer deadAdventurer) {
            if (recallMode) {
                RoomEventDialog.optionList.get(0).pressed = true;
            }
        }
    }

    @SpirePatch(clz = Mushrooms.class, method = SpirePatch.CONSTRUCTOR)
    public static class MushroomsPatch {
        @SpirePostfixPatch
        public static void chooseWar(Mushrooms mushrooms) {
            if (recallMode) {
                RoomEventDialog.optionList.get(0).pressed = true;
            }
        }
    }

    @SpirePatch(clz = Colosseum.class, method = SpirePatch.CONSTRUCTOR)
    public static class ColosseumPatch {
        @SpirePostfixPatch
        public static void chooseWar(Colosseum colosseum) {
            if (recallMode) {
                colosseum.imageEventText.optionList.get(0).pressed = true;
            }
        }
    }

    @SpirePatch(clz = MysteriousSphere.class, method = SpirePatch.CONSTRUCTOR)
    public static class MysteriousSpherePatch {
        @SpirePostfixPatch
        public static void chooseWar(MysteriousSphere mysteriousSphere) {
            if (recallMode) {
                RoomEventDialog.optionList.get(0).pressed = true;
            }
        }
    }

    @SpirePatch(clz = MaskedBandits.class, method = SpirePatch.CONSTRUCTOR)
    public static class MaskedBanditsPatch {
        @SpirePostfixPatch
        public static void chooseWar(MaskedBandits maskedBandits) {
            if (recallMode) {
                RoomEventDialog.optionList.get(1).pressed = true;
            }
        }
    }

    @SpirePatch(clz = Colosseum.class, method = "buttonEffect")
    public static class ColosseumUpdatePatch {
        @SpirePostfixPatch
        public static void chooseWar(Colosseum deadAdventurer, int buttonPressed) {
            if (recallMode) {
                try {
                    deadAdventurer.imageEventText.optionList.get(0).pressed = true;
                } catch (Exception e) {

                }
            }
        }
    }

    @SpirePatch(clz = DeadAdventurer.class, method = "buttonEffect")
    public static class DeadAdventurerUpdatePatch {
        @SpirePostfixPatch
        public static void chooseWar(DeadAdventurer deadAdventurer, int buttonPressed) {
            if (recallMode) {
                try {
                    RoomEventDialog.optionList.get(0).pressed = true;
                } catch (Exception e) {

                }
            }
        }
    }

    @SpirePatch(clz = MysteriousSphere.class, method = "buttonEffect")
    public static class MysteriousSphereUpdatePatch {
        @SpirePostfixPatch
        public static void chooseWar(MysteriousSphere mysteriousSphere, int buttonPressed) {
            if (recallMode) {
                try {
                    RoomEventDialog.optionList.get(0).pressed = true;
                } catch (Exception e) {

                }
            }
        }
    }

    @SpirePatch(clz = Mushrooms.class, method = "buttonEffect")
    public static class MushroomsUpdatePatch {
        @SpirePostfixPatch
        public static void chooseWar(Mushrooms mushrooms, int buttonPressed) {
            if (recallMode) {
                try {
                    RoomEventDialog.optionList.get(0).pressed = true;
                } catch (Exception e) {

                }
            }
        }
    }
}
