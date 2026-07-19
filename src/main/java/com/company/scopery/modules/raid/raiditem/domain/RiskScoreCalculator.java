package com.company.scopery.modules.raid.raiditem.domain;
import com.company.scopery.modules.raid.raiditem.domain.enums.RaidImpact;
import com.company.scopery.modules.raid.raiditem.domain.enums.RaidProbability;
public final class RiskScoreCalculator {
    public static final String FORMULA_VERSION = "v1";
    private RiskScoreCalculator() {}
    public static int weightProbability(RaidProbability p) {
        return switch (p) { case LOW -> 1; case MEDIUM -> 2; case HIGH -> 3; case VERY_HIGH -> 4; };
    }
    public static int weightImpact(RaidImpact i) {
        return switch (i) { case LOW -> 1; case MEDIUM -> 2; case HIGH -> 3; case CRITICAL -> 4; };
    }
    public static Integer score(RaidProbability p, RaidImpact i) {
        if (p == null || i == null) return null;
        return weightProbability(p) * weightImpact(i);
    }
}
