package savestate.orbs;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.Lightning;

import static savestate.SaveStateMod.shouldGoFast;

public class LightningOrbState extends OrbState {
    public LightningOrbState(AbstractOrb orb) {
        super(orb, Orb.LIGHTNING.ordinal());
    }

    public LightningOrbState(String jsonString) {
        super(jsonString, Orb.LIGHTNING.ordinal());
    }

    @Override
    public AbstractOrb loadOrb() {
        Lightning result = new Lightning();
        result.evokeAmount = this.evokeAmount;
        result.passiveAmount = this.passiveAmount;
        return result;
    }

    @SpirePatch(clz = Lightning.class, method = "onEndOfTurn")
    public static class LightningEOTPatch {
        @SpirePrefixPatch
        public static SpireReturn doEOT(Lightning lightning) {
            if (shouldGoFast) {
                AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if (AbstractDungeon.player.hasPower("Electro")) {
                            lightning.applyFocus();
                            AbstractDungeon.actionManager
                                    .addToTop(new DamageAllEnemiesAction(AbstractDungeon.player, DamageInfo
                                            .createDamageMatrix(lightning.passiveAmount, true, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.NONE));
                        } else {
                            AbstractCreature m = AbstractDungeon.getRandomMonster();
                            if (m != null) {
                                // let's say this means all the creatures are dead
                                lightning.applyFocus();
                                DamageInfo info = new DamageInfo(AbstractDungeon.player, AbstractOrb
                                        .applyLockOn(m, lightning.passiveAmount), DamageInfo.DamageType.THORNS);

                                AbstractDungeon.actionManager
                                        .addToTop(new DamageAction(m, info, AbstractGameAction.AttackEffect.NONE, true));
                            }
                        }

                        this.isDone = true;
                    }
                });
                return SpireReturn.Return(null);
            }

            return SpireReturn.Continue();
        }
    }
}
