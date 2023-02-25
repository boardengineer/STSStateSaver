//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package savestate.fastobjects.actions;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.utility.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.ReboundPower;

// The normal UserCardAction does a bunch of triggering during the constructor, this dupe
// bypasses the triggering constructor logic and only performs the action
public class UpdateOnlyUseCardAction extends AbstractGameAction {
    private final AbstractCard targetCard;
    public AbstractCreature target;
    public boolean exhaustCard;
    public boolean reboundCard;

    public UpdateOnlyUseCardAction(AbstractCard card, AbstractCreature target) {
        this.target = null;
        this.reboundCard = false;
        this.targetCard = card;
        this.target = target;
        if (card.exhaustOnUseOnce || card.exhaust) {
            this.exhaustCard = true;
        }

        this.setValues(AbstractDungeon.player, null, 1);
        this.duration = 0.15F;
        if (this.exhaustCard) {
            this.actionType = ActionType.EXHAUST;
        } else {
            this.actionType = ActionType.USE;
        }
    }

    public UpdateOnlyUseCardAction(AbstractCard targetCard) {
        this(targetCard, null);
    }

    public void update() {
        if (this.duration == 0.15F) {
            // I hope this null isn't a problem
            AbstractDungeon.player.powers.stream()
                                         .filter(power -> !this.targetCard.dontTriggerOnUseCard)
                                         .forEach(power -> {
                                             if (power instanceof ReboundPower) {
                                                 boolean justEvoked = ReflectionHacks
                                                         .getPrivate(power, ReboundPower.class, "justEvoked");
                                                 if (justEvoked) {
                                                     ReflectionHacks
                                                             .setPrivate(power, ReboundPower.class, "justEvoked", false);
                                                 } else {
                                                     if (this.targetCard.type != CardType.POWER) {
                                                         reboundCard = true;
                                                     }
                                                     this.addToBot(new ReducePowerAction(AbstractDungeon.player, AbstractDungeon.player, "Rebound", 1));
                                                 }
                                             } else {
                                                 power.onAfterUseCard(this.targetCard, null);
                                             }
                                         });

            AbstractDungeon.getMonsters().monsters.forEach(monster -> {
                monster.powers.stream()
                              .filter(power -> !this.targetCard.dontTriggerOnUseCard)
                              .forEach(power -> power
                                      .onAfterUseCard(this.targetCard, null));
            });


            this.targetCard.freeToPlayOnce = false;
            this.targetCard.isInAutoplay = false;
            if (this.targetCard.purgeOnUse) {
                this.addToTop(new ShowCardAndPoofAction(this.targetCard));
                this.isDone = true;
                AbstractDungeon.player.cardInUse = null;
                return;
            }

            if (this.targetCard.type == CardType.POWER) {
                this.addToTop(new ShowCardAction(this.targetCard));
                if (Settings.FAST_MODE) {
                    this.addToTop(new WaitAction(0.1F));
                } else {
                    this.addToTop(new WaitAction(0.7F));
                }

                AbstractDungeon.player.hand.empower(this.targetCard);
                this.isDone = true;
                AbstractDungeon.player.hand.applyPowers();
                AbstractDungeon.player.hand.glowCheck();
                AbstractDungeon.player.cardInUse = null;
                return;
            }

            AbstractDungeon.player.cardInUse = null;
            boolean spoonProc = false;
            if (this.exhaustCard && AbstractDungeon.player
                    .hasRelic("Strange Spoon") && this.targetCard.type != CardType.POWER) {
                spoonProc = AbstractDungeon.cardRandomRng.randomBoolean();
            }

            if (this.exhaustCard && !spoonProc) {
                AbstractDungeon.player.hand.moveToExhaustPile(this.targetCard);
                CardCrawlGame.dungeon.checkForPactAchievement();
            } else {
                if (spoonProc) {
                    AbstractDungeon.player.getRelic("Strange Spoon").flash();
                }
//                this.targetCard.resetAttributes();

                if (this.reboundCard) {
                    AbstractDungeon.player.hand.moveToDeck(this.targetCard, false);
                } else if (this.targetCard.shuffleBackIntoDrawPile) {
                    AbstractDungeon.player.hand.moveToDeck(this.targetCard, true);
                } else if (this.targetCard.returnToHand) {
                    AbstractDungeon.player.hand.moveToHand(this.targetCard);
                    AbstractDungeon.player.onCardDrawOrDiscard();
                } else {
                    boolean alreadyInDiscard =
                            AbstractDungeon.player.discardPile.group.stream()
                                                                    .anyMatch(card -> card.uuid
                                                                            .equals(this.targetCard.uuid));

                    if (!alreadyInDiscard) {
                        AbstractDungeon.player.hand.moveToDiscardPile(this.targetCard);
                    }
                }
            }

            this.targetCard.exhaustOnUseOnce = false;
            this.targetCard.dontTriggerOnUseCard = false;
            this.addToBot(new HandCheckAction());
        }

        this.isDone = true;
    }

    @SpirePatch(clz = ReboundPower.class, method = "onAfterUseCard")
    public static class FixReboundPowerForUpdateOnlyActionPatch {
        @SpirePrefixPatch
        public static SpireReturn allowNulls(ReboundPower reboundPower, AbstractCard card, UseCardAction action) {
            if (action == null) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}
