package savestate.actions;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.DiscoveryAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;

import java.util.ArrayList;
import java.util.Iterator;

public class DiscoveryActionState implements CurrentActionState {
    private final boolean retrieveCard;
    private final boolean returnColorless;
    private final AbstractCard.CardType cardType;
    private final int amount;

    public DiscoveryActionState(AbstractGameAction action) {
        this((DiscoveryAction) action);
    }

    public DiscoveryActionState(DiscoveryAction action) {
        retrieveCard = ReflectionHacks
                .getPrivate(action, DiscoveryAction.class, "retrieveCard");
        returnColorless = ReflectionHacks
                .getPrivate(action, DiscoveryAction.class, "returnColorless");
        cardType = ReflectionHacks
                .getPrivate(action, DiscoveryAction.class, "cardType");
        amount = action.amount;
    }

    @Override
    public AbstractGameAction loadCurrentAction() {
        DiscoveryAction result = new DiscoveryAction();

        ReflectionHacks
                .setPrivate(result, DiscoveryAction.class, "retrieveCard", retrieveCard);
        ReflectionHacks
                .setPrivate(result, DiscoveryAction.class, "returnColorless", returnColorless);
        ReflectionHacks
                .setPrivate(result, DiscoveryAction.class, "cardType", cardType);

        // This should make the action only trigger the second half of the update
        ReflectionHacks
                .setPrivate(result, AbstractGameAction.class, "duration", 0);

        result.amount = amount;

        return result;
    }

    @SpirePatch(
            clz = DiscoveryAction.class,
            paramtypez = {},
            method = "update"
    )
    public static class NoDoubleTriggerActionPatch {
        @SpirePrefixPatch
        public static SpireReturn Postfix(DiscoveryAction action) {

            boolean retrieveCard = ReflectionHacks
                    .getPrivate(action, DiscoveryAction.class, "retrieveCard");
            boolean returnColorless = ReflectionHacks
                    .getPrivate(action, DiscoveryAction.class, "returnColorless");
            AbstractCard.CardType cardType = ReflectionHacks
                    .getPrivate(action, DiscoveryAction.class, "cardType");
            float duration = ReflectionHacks
                    .getPrivate(action, AbstractGameAction.class, "duration");

            if (duration == Settings.ACTION_DUR_FAST) {
                ArrayList generatedCards;
                if (returnColorless) {
                    generatedCards = generateColorlessCardChoices();
                } else {
                    generatedCards = generateCardChoices(cardType);
                }
                AbstractDungeon.cardRewardScreen
                        .customCombatOpen(generatedCards, CardRewardScreen.TEXT[1], cardType != null);

                ReflectionHacks.privateMethod(AbstractGameAction.class, "tickDuration")
                               .invoke(action);
            } else {
                if (!retrieveCard) {
                    if (AbstractDungeon.cardRewardScreen.discoveryCard != null) {
                        AbstractCard disCard = AbstractDungeon.cardRewardScreen.discoveryCard
                                .makeStatEquivalentCopy();
                        AbstractCard disCard2 = AbstractDungeon.cardRewardScreen.discoveryCard
                                .makeStatEquivalentCopy();
                        if (AbstractDungeon.player.hasPower("MasterRealityPower")) {
                            disCard.upgrade();
                            disCard2.upgrade();
                        }

                        disCard.setCostForTurn(0);
                        disCard2.setCostForTurn(0);
                        disCard.current_x = -1000.0F * Settings.xScale;
                        disCard2.current_x = -1000.0F * Settings.xScale + AbstractCard.IMG_HEIGHT_S;
                        if (action.amount == 1) {
                            if (AbstractDungeon.player.hand.size() < 10) {
                                AbstractDungeon.effectList
                                        .add(new ShowCardAndAddToHandEffect(disCard, (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
                            } else {
                                AbstractDungeon.effectList
                                        .add(new ShowCardAndAddToDiscardEffect(disCard, (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
                            }

                            disCard2 = null;
                        } else if (AbstractDungeon.player.hand.size() + action.amount <= 10) {
                            AbstractDungeon.effectList
                                    .add(new ShowCardAndAddToHandEffect(disCard, (float) Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
                            AbstractDungeon.effectList
                                    .add(new ShowCardAndAddToHandEffect(disCard2, (float) Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
                        } else if (AbstractDungeon.player.hand.size() == 9) {
                            AbstractDungeon.effectList
                                    .add(new ShowCardAndAddToHandEffect(disCard, (float) Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
                            AbstractDungeon.effectList
                                    .add(new ShowCardAndAddToDiscardEffect(disCard2, (float) Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
                        } else {
                            AbstractDungeon.effectList
                                    .add(new ShowCardAndAddToDiscardEffect(disCard, (float) Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
                            AbstractDungeon.effectList
                                    .add(new ShowCardAndAddToDiscardEffect(disCard2, (float) Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
                        }

                        AbstractDungeon.cardRewardScreen.discoveryCard = null;
                    }

                    ReflectionHacks.setPrivate(action, DiscoveryAction.class, "retrieveCard", true);
                }

                ReflectionHacks.privateMethod(AbstractGameAction.class, "tickDuration")
                               .invoke(action);
            }


            // Force the action to stay in the the manager until cards are selected
            if (AbstractDungeon.isScreenUp) {
                action.isDone = false;
            }

            return SpireReturn.Return(null);
        }
    }

    private static ArrayList<AbstractCard> generateColorlessCardChoices() {
        ArrayList derp = new ArrayList();

        while (derp.size() != 3) {
            boolean dupe = false;
            AbstractCard tmp = AbstractDungeon.returnTrulyRandomColorlessCardInCombat();
            Iterator var4 = derp.iterator();

            while (var4.hasNext()) {
                AbstractCard c = (AbstractCard) var4.next();
                if (c.cardID.equals(tmp.cardID)) {
                    dupe = true;
                    break;
                }
            }

            if (!dupe) {
                derp.add(tmp.makeCopy());
            }
        }

        return derp;
    }

    private static ArrayList<AbstractCard> generateCardChoices(AbstractCard.CardType type) {
        ArrayList derp = new ArrayList();

        while (derp.size() != 3) {
            boolean dupe = false;
            AbstractCard tmp = null;
            if (type == null) {
                tmp = AbstractDungeon.returnTrulyRandomCardInCombat();
            } else {
                tmp = AbstractDungeon.returnTrulyRandomCardInCombat(type);
            }

            Iterator var5 = derp.iterator();

            while (var5.hasNext()) {
                AbstractCard c = (AbstractCard) var5.next();
                if (c.cardID.equals(tmp.cardID)) {
                    dupe = true;
                    break;
                }
            }

            if (!dupe) {
                derp.add(tmp.makeCopy());
            }
        }

        return derp;
    }
}
