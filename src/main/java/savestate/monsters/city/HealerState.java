package savestate.monsters.city;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.google.gson.JsonObject;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.city.Healer;
import savestate.fastobjects.AnimationStateFast;
import savestate.monsters.Monster;
import savestate.monsters.MonsterState;

import static savestate.SaveStateMod.shouldGoFast;

public class HealerState extends MonsterState {
    public HealerState(AbstractMonster monster) {
        super(monster);

        monsterTypeNumber = Monster.HEALER.ordinal();
    }

    public HealerState(String jsonString) {
        super(jsonString);

        monsterTypeNumber = Monster.HEALER.ordinal();
    }

    public HealerState(JsonObject monsterJson) {
        super(monsterJson);

        monsterTypeNumber = Monster.HEALER.ordinal();
    }

    @Override
    public AbstractMonster loadMonster() {
        Healer result = new Healer(offsetX, offsetY);
        populateSharedFields(result);
        return result;
    }

    @SpirePatch(
            clz = Healer.class,
            paramtypez = {float.class, float.class},
            method = SpirePatch.CONSTRUCTOR
    )
    public static class NoAnimationsPatch {
        @SpireInsertPatch(loc = 64)
        public static SpireReturn Healer(Healer _instance, float x, float y) {
            if (shouldGoFast) {
                _instance.state = new AnimationStateFast();
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}
