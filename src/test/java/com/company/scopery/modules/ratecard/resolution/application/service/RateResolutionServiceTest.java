package com.company.scopery.modules.ratecard.resolution.application.service;

import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleScope;
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
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateResolutionServiceTest {

    @Mock RateCardRepository rateCardRepository;
    @Mock RateCardVersionRepository versionRepository;
    @Mock RateCardLineRepository lineRepository;
    @Mock CostRoleRepository costRoleRepository;
    @Mock InflationPolicyRepository inflationPolicyRepository;
    @Mock WorkspaceRepository workspaceRepository;

    RateResolutionService service;

    @BeforeEach
    void setUp() {
        service = new RateResolutionService(rateCardRepository, versionRepository, lineRepository,
                costRoleRepository, inflationPolicyRepository, workspaceRepository);
    }

    @Test
    void inflationExact_twoYearsAt5Percent() {
        BigDecimal adjusted = RateResolutionService.applyInflationExact(
                new BigDecimal("500000"), new BigDecimal("5"), 2);
        assertThat(adjusted).isEqualByComparingTo(new BigDecimal("551250.0000"));
    }

    @Test
    void yearsForward_uses365_25() {
        BigDecimal years = RateResolutionService.computeYearsForward(
                LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1));
        // 365 / 365.25 ≈ 0.9993
        assertThat(years).isEqualByComparingTo(new BigDecimal("0.9993"));
    }

    @Test
    void resolve_workspacePrecedenceWithInflation() {
        UUID workspaceId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        CostRole role = CostRole.create("BACKEND_DEVELOPER", "Backend", null, CostRoleScope.SYSTEM, null, null, null, true);
        RateCard card = RateCard.create("WS_CARD", "WS", null, RateCardScope.WORKSPACE, orgId, workspaceId, "VND", true);
        RateCardVersion version = RateCardVersion.create(card.id(), 1, null, null, LocalDate.of(2025, 1, 1), null)
                .publish(UUID.randomUUID());
        RateCardLine line = RateCardLine.create(version.id(), role.id(), null, null, "VND",
                new BigDecimal("500000.0000"), new BigDecimal("800000.0000"), null);
        InflationPolicy inflation = InflationPolicy.create("INF", "Inf", null, InflationPolicyScope.SYSTEM,
                null, null, new BigDecimal("5"), CompoundFrequency.ANNUAL, LocalDate.of(2000, 1, 1), null);

        when(costRoleRepository.findByCode("BACKEND_DEVELOPER")).thenReturn(Optional.of(role));
        when(rateCardRepository.findActiveByScope(eq(RateCardScope.WORKSPACE), any(), eq(workspaceId)))
                .thenReturn(List.of(card));
        when(versionRepository.findPublishedCoveringDate(eq(card.id()), any())).thenReturn(List.of(version));
        when(lineRepository.findByVersionId(version.id())).thenReturn(List.of(line));
        when(inflationPolicyRepository.findActiveCovering(any(), any(), any(), any())).thenReturn(List.of(inflation));

        RateSnapshot snapshot = service.resolve(new ResolveRateQuery(
                workspaceId, orgId, null, null, "BACKEND_DEVELOPER",
                LocalDate.of(2027, 1, 1), "VND", "BOTH"));

        assertThat(snapshot.rateCardId()).isEqualTo(card.id());
        assertThat(snapshot.costRoleCode()).isEqualTo("BACKEND_DEVELOPER");
        assertThat(snapshot.baseCostRate()).isEqualByComparingTo("500000.0000");
        assertThat(snapshot.adjustedCostRate()).isNotNull();
        assertThat(snapshot.yearsForward()).isEqualByComparingTo("1.9986");
        // salary never present — snapshot fields only rates
        assertThat(snapshot.toString()).doesNotContainIgnoringCase("salary");
    }
}
