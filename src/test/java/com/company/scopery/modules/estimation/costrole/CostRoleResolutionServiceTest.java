package com.company.scopery.modules.estimation.costrole;

import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleScope;
import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleStatus;
import com.company.scopery.modules.ratecard.costrole.domain.model.CostRole;
import com.company.scopery.modules.ratecard.costrole.domain.model.CostRoleRepository;
import com.company.scopery.modules.ratecard.membercostrole.domain.model.WorkspaceMemberCostRoleAssignment;
import com.company.scopery.modules.ratecard.membercostrole.domain.model.WorkspaceMemberCostRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CostRoleResolutionServiceTest {

    @Mock CostRoleRepository costRoleRepository;
    @Mock WorkspaceMemberCostRoleRepository memberCostRoleRepository;

    private CostRoleResolutionService service;
    private final UUID workspaceId = UUID.randomUUID();
    private final UUID orgId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final UUID plannedRoleId = UUID.randomUUID();
    private final UUID memberRoleId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new CostRoleResolutionService(costRoleRepository, memberCostRoleRepository);
    }

    @Test
    void plannedRoleWinsOverMemberAssignment() {
        CostRole planned = CostRole.create("DEV", "Developer", null, CostRoleScope.WORKSPACE,
                orgId, workspaceId, "ENG", false);
        planned = new CostRole(plannedRoleId, planned.code(), planned.name(), planned.description(),
                planned.scope(), planned.organizationId(), planned.workspaceId(), planned.category(),
                planned.builtIn(), CostRoleStatus.ACTIVE, null, null, 0, null, null);
        when(costRoleRepository.search(eq(CostRoleScope.WORKSPACE), isNull(), eq(workspaceId),
                eq(CostRoleStatus.ACTIVE), isNull(), eq("DEV"), any()))
                .thenReturn(page(List.of(planned)));

        Optional<ResolvedCostRole> result = service.resolve(workspaceId, orgId, "DEV", userId, LocalDate.now());

        assertThat(result).contains(new ResolvedCostRole(plannedRoleId, "DEV"));
    }

    @Test
    void memberFallbackWhenNoPlannedRole() {
        WorkspaceMemberCostRoleAssignment assignment = WorkspaceMemberCostRoleAssignment.create(
                workspaceId, UUID.randomUUID(), userId, memberRoleId, true,
                LocalDate.of(2026, 1, 1), null);
        when(memberCostRoleRepository.search(eq(workspaceId), isNull(), eq(userId), isNull(), any(), any(), any()))
                .thenReturn(page(List.of(assignment)));
        CostRole memberRole = CostRole.create("QA", "QA", null, CostRoleScope.WORKSPACE,
                orgId, workspaceId, "ENG", false);
        memberRole = new CostRole(memberRoleId, memberRole.code(), memberRole.name(), memberRole.description(),
                memberRole.scope(), memberRole.organizationId(), memberRole.workspaceId(), memberRole.category(),
                memberRole.builtIn(), CostRoleStatus.ACTIVE, null, null, 0, null, null);
        when(costRoleRepository.findById(memberRoleId)).thenReturn(Optional.of(memberRole));

        Optional<ResolvedCostRole> result = service.resolve(workspaceId, orgId, null, userId, LocalDate.now());

        assertThat(result).contains(new ResolvedCostRole(memberRoleId, "QA"));
    }

    @Test
    void missingRoleReturnsEmpty() {
        when(costRoleRepository.search(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(page(List.of()));
        when(memberCostRoleRepository.search(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(page(List.of()));

        assertThat(service.resolve(workspaceId, orgId, "MISSING", userId, LocalDate.now())).isEmpty();
    }

    private static <T> PageResult<T> page(List<T> items) {
        return new PageResult<>(items, 0, 20, items.size(), 1, true, true);
    }
}
