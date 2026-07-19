package com.company.scopery.modules.ratecard.resolution.application.service;

import com.company.scopery.modules.ratecard.costrole.domain.model.CostRole;
import com.company.scopery.modules.ratecard.costrole.domain.model.CostRoleRepository;
import com.company.scopery.modules.ratecard.inflationpolicy.domain.enums.CompoundFrequency;
import com.company.scopery.modules.ratecard.inflationpolicy.domain.enums.InflationPolicyScope;
import com.company.scopery.modules.ratecard.inflationpolicy.domain.model.InflationPolicy;
import com.company.scopery.modules.ratecard.inflationpolicy.domain.model.InflationPolicyRepository;
import com.company.scopery.modules.ratecard.ratecard.domain.enums.RateCardScope;
import com.company.scopery.modules.ratecard.ratecard.domain.model.RateCard;
import com.company.scopery.modules.ratecard.ratecard.domain.model.RateCardRepository;
import com.company.scopery.modules.ratecard.ratecardline.domain.model.RateCardLine;
import com.company.scopery.modules.ratecard.ratecardline.domain.model.RateCardLineRepository;
import com.company.scopery.modules.ratecard.ratecardversion.domain.model.RateCardVersion;
import com.company.scopery.modules.ratecard.ratecardversion.domain.model.RateCardVersionRepository;
import com.company.scopery.modules.ratecard.resolution.application.query.ResolveRateQuery;
import com.company.scopery.modules.ratecard.resolution.domain.RateSnapshot;
import com.company.scopery.modules.ratecard.shared.currency.SupportedCurrency;
import com.company.scopery.modules.ratecard.shared.error.RateCardExceptions;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Deterministic rate resolution (RTE-008).
 * Precedence: WORKSPACE > ORGANIZATION > SYSTEM.
 * Prefer isDefault ACTIVE card at each level, else any ACTIVE with published version covering date.
 *
 * <p>YearsForward = ChronoUnit.DAYS.between(version.effectiveFrom, targetDate) / 365.25
 * (decimal years; documented Phase 15 choice). Inflation uses BigDecimal HALF_UP scale 4.
 */
@Service
public class RateResolutionService {

    private static final MathContext MC = new MathContext(16, RoundingMode.HALF_UP);
    private static final int RATE_SCALE = 4;
    private static final BigDecimal DAYS_PER_YEAR = new BigDecimal("365.25");

    private final RateCardRepository rateCardRepository;
    private final RateCardVersionRepository versionRepository;
    private final RateCardLineRepository lineRepository;
    private final CostRoleRepository costRoleRepository;
    private final InflationPolicyRepository inflationPolicyRepository;
    private final WorkspaceRepository workspaceRepository;

    public RateResolutionService(RateCardRepository rateCardRepository,
                                 RateCardVersionRepository versionRepository,
                                 RateCardLineRepository lineRepository,
                                 CostRoleRepository costRoleRepository,
                                 InflationPolicyRepository inflationPolicyRepository,
                                 WorkspaceRepository workspaceRepository) {
        this.rateCardRepository = rateCardRepository;
        this.versionRepository = versionRepository;
        this.lineRepository = lineRepository;
        this.costRoleRepository = costRoleRepository;
        this.inflationPolicyRepository = inflationPolicyRepository;
        this.workspaceRepository = workspaceRepository;
    }

    @Transactional(readOnly = true)
    public RateSnapshot resolve(ResolveRateQuery query) {
        if (query.targetDate() == null) {
            throw RateCardExceptions.noApplicableVersion();
        }
        CostRole role = resolveRole(query);
        UUID organizationId = query.organizationId();
        UUID workspaceId = query.workspaceId();
        if (workspaceId != null && organizationId == null) {
            organizationId = workspaceRepository.findById(workspaceId).map(w -> w.organizationId()).orElse(null);
        }

        String currency = query.currencyCode() != null && !query.currencyCode().isBlank()
                ? SupportedCurrency.requireValid(query.currencyCode())
                : null;

        Optional<ResolvedCardVersionLine> match = findMatch(RateCardScope.WORKSPACE, organizationId, workspaceId,
                role, query.targetDate(), currency);
        if (match.isEmpty()) {
            match = findMatch(RateCardScope.ORGANIZATION, organizationId, null, role, query.targetDate(), currency);
        }
        if (match.isEmpty()) {
            match = findMatch(RateCardScope.SYSTEM, null, null, role, query.targetDate(), currency);
        }
        if (match.isEmpty()) {
            throw RateCardExceptions.noApplicableCard();
        }

        ResolvedCardVersionLine r = match.get();
        InflationPolicy inflation = findInflation(workspaceId, organizationId, query.targetDate()).orElse(null);

        BigDecimal yearsForward = computeYearsForward(r.version().effectiveFrom(), query.targetDate());
        BigDecimal inflationPercent = inflation != null ? inflation.inflationPercent() : BigDecimal.ZERO;
        CompoundFrequency frequency = inflation != null ? inflation.compoundFrequency() : CompoundFrequency.NONE;

        BigDecimal adjustedCost = applyInflation(r.line().costRatePerHour(), inflationPercent, yearsForward, frequency);
        BigDecimal adjustedBilling = r.line().billingRatePerHour() == null
                ? null
                : applyInflation(r.line().billingRatePerHour(), inflationPercent, yearsForward, frequency);

        return new RateSnapshot(
                r.card().id(),
                r.version().id(),
                r.line().id(),
                role.id(),
                role.code(),
                r.line().costRatePerHour(),
                adjustedCost,
                r.line().billingRatePerHour(),
                adjustedBilling,
                r.line().currencyCode(),
                inflation != null ? inflation.id() : null,
                inflationPercent,
                yearsForward,
                query.targetDate(),
                Instant.now()
        );
    }

    private CostRole resolveRole(ResolveRateQuery query) {
        if (query.costRoleId() != null) {
            return costRoleRepository.findById(query.costRoleId())
                    .orElseThrow(() -> RateCardExceptions.resolutionRoleNotFound(query.costRoleId().toString()));
        }
        if (query.costRoleCode() != null && !query.costRoleCode().isBlank()) {
            String code = CostRole.normalizeCode(query.costRoleCode());
            return costRoleRepository.findByCode(code)
                    .orElseThrow(() -> RateCardExceptions.resolutionRoleNotFound(code));
        }
        throw RateCardExceptions.resolutionRoleNotFound("missing");
    }

    private Optional<ResolvedCardVersionLine> findMatch(RateCardScope scope, UUID organizationId, UUID workspaceId,
                                                        CostRole role, LocalDate targetDate, String currency) {
        List<RateCard> cards = rateCardRepository.findActiveByScope(scope, organizationId, workspaceId);
        if (cards.isEmpty()) {
            return Optional.empty();
        }
        List<RateCard> ordered = cards.stream()
                .sorted(Comparator.comparing(RateCard::isDefault).reversed()
                        .thenComparing(RateCard::code))
                .toList();

        Optional<ResolvedCardVersionLine> firstAny = Optional.empty();
        for (RateCard card : ordered) {
            List<RateCardVersion> versions = versionRepository.findPublishedCoveringDate(card.id(), targetDate);
            if (versions.isEmpty()) {
                continue;
            }
            RateCardVersion version = versions.stream()
                    .sorted(Comparator.comparing(RateCardVersion::versionNumber).reversed())
                    .findFirst()
                    .orElseThrow();
            Optional<RateCardLine> line = matchLine(version.id(), role.id(), currency);
            if (line.isEmpty()) {
                if (firstAny.isEmpty()) {
                    // keep searching; if later we find card with version but no line, still noApplicableLine
                }
                continue;
            }
            ResolvedCardVersionLine resolved = new ResolvedCardVersionLine(card, version, line.get());
            if (card.isDefault()) {
                return Optional.of(resolved);
            }
            if (firstAny.isEmpty()) {
                firstAny = Optional.of(resolved);
            }
        }
        return firstAny;
    }

    private Optional<RateCardLine> matchLine(UUID versionId, UUID costRoleId, String currency) {
        List<RateCardLine> lines = lineRepository.findByVersionId(versionId).stream()
                .filter(l -> l.costRoleId().equals(costRoleId))
                .filter(l -> currency == null || currency.equalsIgnoreCase(l.currencyCode()))
                .sorted(Comparator.comparing(RateCardLine::currencyCode))
                .toList();
        if (lines.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(lines.getFirst());
    }

    private Optional<InflationPolicy> findInflation(UUID workspaceId, UUID organizationId, LocalDate targetDate) {
        if (workspaceId != null) {
            List<InflationPolicy> ws = inflationPolicyRepository.findActiveCovering(
                    InflationPolicyScope.WORKSPACE, organizationId, workspaceId, targetDate);
            if (!ws.isEmpty()) return Optional.of(ws.getFirst());
        }
        if (organizationId != null) {
            List<InflationPolicy> org = inflationPolicyRepository.findActiveCovering(
                    InflationPolicyScope.ORGANIZATION, organizationId, null, targetDate);
            if (!org.isEmpty()) return Optional.of(org.getFirst());
        }
        List<InflationPolicy> system = inflationPolicyRepository.findActiveCovering(
                InflationPolicyScope.SYSTEM, null, null, targetDate);
        return system.stream().findFirst();
    }

    /**
     * YearsForward = days between effectiveFrom and targetDate / 365.25 as decimal.
     * Negative (target before effective) is clamped to zero.
     */
    static BigDecimal computeYearsForward(LocalDate effectiveFrom, LocalDate targetDate) {
        long days = ChronoUnit.DAYS.between(effectiveFrom, targetDate);
        if (days <= 0) {
            return BigDecimal.ZERO.setScale(RATE_SCALE, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(days).divide(DAYS_PER_YEAR, RATE_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Adjusted = Base × (1 + percent/100) ^ yearsForward when frequency is ANNUAL;
     * otherwise returns base (NONE). MONTHLY uses the same annual-compound path in Phase 15.
     *
     * <p>Pure BigDecimal: whole years via repeated multiply; fractional year via linear accrual
     * of the annual increment ({@code 1 + (factor - 1) × fraction}). No floating-point math.
     */
    static BigDecimal applyInflation(BigDecimal base, BigDecimal inflationPercent,
                                     BigDecimal yearsForward, CompoundFrequency frequency) {
        if (base == null) {
            return null;
        }
        if (frequency == null || frequency == CompoundFrequency.NONE
                || inflationPercent == null || inflationPercent.compareTo(BigDecimal.ZERO) == 0
                || yearsForward == null || yearsForward.compareTo(BigDecimal.ZERO) == 0) {
            return base.setScale(RATE_SCALE, RoundingMode.HALF_UP);
        }
        BigDecimal factor = BigDecimal.ONE.add(
                inflationPercent.divide(BigDecimal.valueOf(100), MC), MC);
        int wholeYears = yearsForward.setScale(0, RoundingMode.DOWN).intValue();
        BigDecimal fraction = yearsForward.subtract(BigDecimal.valueOf(wholeYears));

        BigDecimal result = base;
        for (int i = 0; i < wholeYears; i++) {
            result = result.multiply(factor, MC);
        }
        if (fraction.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal fractionalFactor = BigDecimal.ONE.add(
                    factor.subtract(BigDecimal.ONE).multiply(fraction, MC), MC);
            result = result.multiply(fractionalFactor, MC);
        }
        return result.setScale(RATE_SCALE, RoundingMode.HALF_UP);
    }

    /** Integer-year exact compound (spec example path). */
    public static BigDecimal applyInflationExact(BigDecimal base, BigDecimal inflationPercent, int wholeYears) {
        return applyInflation(base, inflationPercent, BigDecimal.valueOf(wholeYears), CompoundFrequency.ANNUAL);
    }

    private record ResolvedCardVersionLine(RateCard card, RateCardVersion version, RateCardLine line) {}
}
