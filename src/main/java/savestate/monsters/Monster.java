package savestate.monsters;

import com.google.gson.JsonObject;
import savestate.monsters.beyond.*;
import savestate.monsters.city.*;
import savestate.monsters.ending.CorrputHeartState;
import savestate.monsters.ending.SpireShieldState;
import savestate.monsters.ending.SpireSpearState;
import savestate.monsters.exordium.*;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.function.Function;

public enum Monster {
    ACID_SLIME_L("AcidSlime_L", monster -> new AcidSlime_LState(monster), json -> new AcidSlime_LState(json), jsonObject -> new AcidSlime_LState(jsonObject)),
    ACID_SLIME_M("AcidSlime_M", monster -> new AcidSlime_MState(monster), json -> new AcidSlime_MState(json), jsonObject -> new AcidSlime_MState(jsonObject)),
    ACID_SLIME_S("AcidSlime_S", monster -> new AcidSlime_SState(monster), json -> new AcidSlime_SState(json), jsonObject -> new AcidSlime_SState(jsonObject)),
    APOLOGY_SLIME("Apology Slime", monster -> new ApologySlimeState(monster), json -> new ApologySlimeState(json), jsonObject -> new ApologySlimeState(jsonObject)),
    CORRUPT_HEART("CorruptHeart", monster -> new CorrputHeartState(monster), json -> new CorrputHeartState(json), jsonObject -> new CorrputHeartState(jsonObject)),
    CULTIST("Cultist", monster -> new CultistState(monster), json -> new CultistState(json), jsonObject -> new CultistState(jsonObject)),
    FUNGI_BEAST("FungiBeast", monster -> new FungiBeastState(monster), json -> new FungiBeastState(json), jsonObject -> new FungiBeastState(jsonObject)),
    GREMLIN_FAT("GremlinFat", monster -> new GremlinFatState(monster), json -> new GremlinFatState(json), jsonObject -> new GremlinFatState(jsonObject)),
    GREMLIN_NOB("GremlinNob", monster -> new GremlinNobState(monster), json -> new GremlinNobState(json), jsonObject -> new GremlinNobState(jsonObject)),
    GREMLIN_THIEF("GremlinThief", monster -> new GremlinThiefState(monster), json -> new GremlinThiefState(json), jsonObject -> new GremlinThiefState(jsonObject)),
    GREMLIN_TSUNDERE("GremlinTsundere", monster -> new GremlinTsundereState(monster), json -> new GremlinTsundereState(json), jsonObject -> new GremlinTsundereState(jsonObject)),
    GREMLIN_WARRIOR("GremlinWarrior", monster -> new GremlinWarriorState(monster), json -> new GremlinWarriorState(json), jsonObject -> new GremlinWarriorState(jsonObject)),
    GREMLIN_WIZARD("GremlinWizard", monster -> new GremlinWizardState(monster), json -> new GremlinWizardState(json), jsonObject -> new GremlinWizardState(jsonObject)),
    HEXAGHOST("Hexaghost", monster -> new HexaghostState(monster), json -> new HexaghostState(json), jsonObject -> new HexaghostState(jsonObject)),
    JAWWORM("JawWorm", monster -> new JawWormState(monster), json -> new JawWormState(json), jsonObject -> new JawWormState(jsonObject)),
    LAGAVULIN("Lagavulin", monster -> new LagavulinState(monster), json -> new LagavulinState(json), jsonObject -> new LagavulinState(jsonObject)),
    LOOTER("Looter", monster -> new LooterState(monster), json -> new LooterState(json), jsonObject -> new LooterState(jsonObject)),
    FUZZY_LOUSE_DEFENSIVE("FuzzyLouseDefensive", monster -> new LouseDefensiveState(monster), json -> new LouseDefensiveState(json), jsonObject -> new LouseDefensiveState(jsonObject)),
    FUZZY_LOUSE_NORMAL("FuzzyLouseNormal", monster -> new LouseNormalState(monster), json -> new LouseNormalState(json), jsonObject -> new LouseNormalState(jsonObject)),
    SENTRY("Sentry", monster -> new SentryState(monster), json -> new SentryState(json), jsonObject -> new SentryState(jsonObject)),
    SLAVER_BLUE("SlaverBlue", monster -> new SlaverBlueState(monster), json -> new SlaverBlueState(json), jsonObject -> new SlaverBlueState(jsonObject)),
    SLAVER_RED("SlaverRed", monster -> new SlaverRedState(monster), json -> new SlaverRedState(json), jsonObject -> new SlaverRedState(jsonObject)),
    SLIME_BOSS("SlimeBoss", monster -> new SlimeBossState(monster), json -> new SlimeBossState(json), jsonObject -> new SlimeBossState(jsonObject)),
    SPIKE_SLIME_L("SpikeSlime_L", monster -> new SpikeSlime_LState(monster), json -> new SpikeSlime_LState(json), jsonObject -> new SpikeSlime_LState(jsonObject)),
    SPIKE_SLIME_M("SpikeSlime_M", monster -> new SpikeSlime_MState(monster), json -> new SpikeSlime_MState(json), jsonObject -> new SpikeSlime_MState(jsonObject)),
    SPIKE_SLIME_S("SpikeSlime_S", monster -> new SpikeSlime_SState(monster), json -> new SpikeSlime_SState(json), jsonObject -> new SpikeSlime_SState(jsonObject)),
    SPIRE_SPEAR("SpireSpear", monster -> new SpireSpearState(monster), json -> new SpireSpearState(json), jsonObject -> new SpireSpearState(jsonObject)),
    SPIRE_SHIELD("SpireShield", monster -> new SpireShieldState(monster), json -> new SpireShieldState(json), jsonObject -> new SpireShieldState(jsonObject)),
    THE_GUARDIAN("TheGuardian", monster -> new TheGuardianState(monster), json -> new TheGuardianState(json), jsonObject -> new TheGuardianState(jsonObject)),
    CHOSEN("Chosen", monster -> new ChosenState(monster), json -> new ChosenState(json), jsonObject -> new ChosenState(jsonObject)),
    MUGGER("Mugger", monster -> new MuggerState(monster), json -> new MuggerState(json), jsonObject -> new MuggerState(jsonObject)),
    SHELLED_PARASITE("Shelled Parasite", monster -> new ShelledParasiteState(monster), json -> new ShelledParasiteState(json), jsonObject -> new ShelledParasiteState(jsonObject)),
    SPHERIC_GUARDIAN("SphericGuardian", monster -> new SphericGuardianState(monster), json -> new SphericGuardianState(json), jsonObject -> new SphericGuardianState(jsonObject)),
    GREMLIN_LEADER("GremlinLeader", monster -> new GremlinLeaderState(monster), json -> new GremlinLeaderState(json), jsonObject -> new GremlinLeaderState(jsonObject)),
    BYRD("Byrd", monster -> new ByrdState(monster), json -> new ByrdState(json), jsonObject -> new ByrdState(jsonObject)),
    SNAKE_PLANT("SnakePlant", monster -> new SnakePlantState(monster), json -> new SnakePlantState(json), jsonObject -> new SnakePlantState(jsonObject)),
    BOOK_OF_STABBING("BookOfStabbing", monster -> new BookOfStabbingState(monster), json -> new BookOfStabbingState(json), jsonObject -> new BookOfStabbingState(jsonObject)),
    BANDIT_CHILD("BanditChild", monster -> new BanditPointyState(monster), json -> new BanditPointyState(json), jsonObject -> new BanditPointyState(jsonObject)),
    BANDIT_LEADER("BanditLeader", monster -> new BanditLeaderState(monster), json -> new BanditLeaderState(json), jsonObject -> new BanditLeaderState(jsonObject)),
    BANDIT_BEAR("BanditBear", monster -> new BanditBearState(monster), json -> new BanditBearState(json), jsonObject -> new BanditBearState(jsonObject)),
    SLAVER_BOSS("SlaverBoss", monster -> new TaskmasterState(monster), json -> new TaskmasterState(json), jsonObject -> new TaskmasterState(jsonObject)),
    CENTURION("Centurion", monster -> new CenturionState(monster), json -> new CenturionState(json), jsonObject -> new CenturionState(jsonObject)),
    HEALER("Healer", monster -> new HealerState(monster), json -> new HealerState(json), jsonObject -> new HealerState(jsonObject)),
    SNECKO("Snecko", monster -> new SneckoState(monster), json -> new SneckoState(json), jsonObject -> new SneckoState(jsonObject)),
    CHAMP("Champ", monster -> new ChampState(monster), json -> new ChampState(json), jsonObject -> new ChampState(jsonObject)),
    BRONZE_AUTOMATON("BronzeAutomaton", monster -> new BronzeAutomatonState(monster), json -> new BronzeAutomatonState(json), jsonObject -> new BronzeAutomatonState(jsonObject)),
    BRONZE_ORB("BronzeOrb", monster -> new BronzeOrbState(monster), json -> new BronzeOrbState(json), jsonObject -> new BronzeOrbState(jsonObject)),
    THE_COLLECTOR("TheCollector", monster -> new TheCollectorState(monster), json -> new TheCollectorState(json), jsonObject -> new TheCollectorState(jsonObject)),
    TORCH_HEAD("TorchHead", monster -> new TorchHeadState(monster), json -> new TorchHeadState(json), jsonObject -> new TorchHeadState(jsonObject)),

    // Beyond
    EXPLODER("Exploder", monster -> new ExploderState(monster), json -> new ExploderState(json), jsonObject -> new ExploderState(jsonObject)),
    REPULSOR("Repulsor", monster -> new RepulsorState(monster), json -> new RepulsorState(json), jsonObject -> new RepulsorState(jsonObject)),
    SPIKER("Spiker", monster -> new SpikerState(monster), json -> new SpikerState(json), jsonObject -> new SpikerState(jsonObject)),
    DARKLING("Darkling", monster -> new DarklingState(monster), json -> new DarklingState(json), jsonObject -> new DarklingState(jsonObject)),
    ORG_WALKER("Orb Walker", monster -> new OrbWalkerState(monster), json -> new OrbWalkerState(json), jsonObject -> new OrbWalkerState(jsonObject)),
    TRANSIENT("Transient", monster -> new TransientState(monster), json -> new TransientState(json), jsonObject -> new TransientState(jsonObject)),
    NEMESIS("Nemesis", monster -> new NemesisState(monster), json -> new NemesisState(json), jsonObject -> new NemesisState(jsonObject)),
    GIANT_HEAD("GiantHead", monster -> new GiantHeadState(monster), json -> new GiantHeadState(json), jsonObject -> new GiantHeadState(jsonObject)),
    MAW("Maw", monster -> new MawState(monster), json -> new MawState(json), jsonObject -> new MawState(jsonObject)),
    AWAKENED_ONE("AwakenedOne", monster -> new AwakenedOneState(monster), json -> new AwakenedOneState(json), jsonObject -> new AwakenedOneState(jsonObject)),
    WRITHING_MASS("WrithingMass", monster -> new WrithingMassState(monster), json -> new WrithingMassState(json), jsonObject -> new WrithingMassState(jsonObject)),
    SPIRE_GROWTH("Serpent", monster -> new SpireGrowthState(monster), json -> new SpireGrowthState(json), jsonObject -> new SpireGrowthState(jsonObject)),
    DONU("Donu", monster -> new DonuState(monster), json -> new DonuState(json), jsonObject -> new DonuState(jsonObject)),
    DECA("Deca", monster -> new DecaState(monster), json -> new DecaState(json), jsonObject -> new DecaState(jsonObject)),
    REPTOMANCER("Reptomancer", monster -> new ReptomancerState(monster), json -> new ReptomancerState(json), jsonObject -> new ReptomancerState(jsonObject)),
    SNAKE_DAGGER("Dagger", monster -> new SnakerDaggerState(monster), json -> new SnakerDaggerState(json), jsonObject -> new SnakerDaggerState(jsonObject)),
    TIME_EATER("TimeEater", monster -> new TimeEaterState(monster), json -> new TimeEaterState(json), jsonObject -> new TimeEaterState(jsonObject));

    public final String monsterId;
    public final Function<AbstractMonster, MonsterState> factory;
    public final Function<String, MonsterState> jsonFactory;
    public Function<JsonObject, MonsterState> jsonObjectFactory = null;

    Monster(String monsterId) {
        this.monsterId = monsterId;
        this.factory = moot -> null;
        this.jsonFactory = moot -> null;
    }

    Monster(String monsterId, Function<AbstractMonster, MonsterState> factory) {
        this.monsterId = monsterId;
        this.factory = factory;
        this.jsonFactory = moot -> null;
    }

    Monster(String monsterId, Function<AbstractMonster, MonsterState> factory, Function<String, MonsterState> jsonFactory, Function<JsonObject, MonsterState> jsonObjectFactory) {
        this.jsonObjectFactory = jsonObjectFactory;
        this.monsterId = monsterId;
        this.factory = factory;
        this.jsonFactory = jsonFactory;
    }

//    Monster(String monsterId, Function<AbstractMonster, MonsterState> factory, Function<String, MonsterState> jsonFactory) {
//        this.monsterId = monsterId;
//        this.factory = factory;
//        this.jsonFactory = jsonFactory;
//    }
}
