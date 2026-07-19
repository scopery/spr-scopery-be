package com.company.scopery.modules.estimation.costrole;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleScope;
import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleStatus;
import com.company.scopery.modules.ratecard.costrole.domain.model.CostRole;
import com.company.scopery.modules.ratecard.costrole.domain.model.CostRoleRepository;
import com.company.scopery.modules.ratecard.membercostrole.domain.enums.MemberCostRoleStatus;
import com.company.scopery.modules.ratecard.membercostrole.domain.model.WorkspaceMemberCostRoleAssignment;
import com.company.scopery.modules.ratecard.membercostrole.domain.model.WorkspaceMemberCostRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Deterministic cost role resolution for estimation.
 * Order: Task.plannedRoleCode (ACTIVE CostRole accessible to workspace) then
 * WorkspaceMemberCostRoleAssignment default for inChargeUserId effective on date.
 * Does not invent defaults or use IAM roles.
 */
@Service
public class CostRoleResolutionService {

    private final CostRoleRepository costRoleRepository;
    private final WorkspaceMemberCostRoleRepository memberCostRoleRepository;

    public CostRoleResolutionService(CostRoleRepository costRoleRepository,
                                     WorkspaceMemberCostRoleRepository memberCostRoleRepository) {
        this.costRoleRepository = costRoleRepository;
        this.memberCostRoleRepository = memberCostRoleRepository;
    }

    @Transactional(readOnly = true)
    public Optional<ResolvedCostRole> resolve(UUID workspaceId,
                                              UUID organizationId,
                                              String plannedRoleCode,
                                              UUID inChargeUserId,
                                              LocalDate targetDate) {
        Optional<ResolvedCostRole> fromPlanned = resolveByPlannedCode(workspaceId, organizationId, plannedRoleCode);
        if (fromPlanned.isPresent()) {
            return fromPlanned;
        }
        return resolveByMemberAssignment(workspaceId, inChargeUserId, targetDate);
    }

    private Optional<ResolvedCostRole> resolveByPlannedCode(UUID workspaceId,
                                                            UUID organizationId,
                                                            String plannedRoleCode) {
        if (plannedRoleCode == null || plannedRoleCode.isBlank()) {
            return Optional.empty();
        }
        String code = CostRole.normalizeCode(plannedRoleCode);
        Optional<CostRole> workspace = findActiveByScope(CostRoleScope.WORKSPACE, null, workspaceId, code);
        if (workspace.isPresent()) {
            return workspace.map(r -> new ResolvedCostRole(r.id(), r.code()));
        }
        Optional<CostRole> org = findActiveByScope(CostRoleScope.ORGANIZATION, organizationId, null, code);
        if (org.isPresent()) {
            return org.map(r -> new ResolvedCostRole(r.id(), r.code()));
        }
        return findActiveByScope(CostRoleScope.SYSTEM, null, null, code)
                .map(r -> new ResolvedCostRole(r.id(), r.code()));
    }

    private Optional<CostRole> findActiveByScope(CostRoleScope scope,
                                                 UUID organizationId,
                                                 UUID workspaceId,
                                                 String code) {
        return costRoleRepository.search(
                        scope, organizationId, workspaceId, CostRoleStatus.ACTIVE, null, code,
                        PageQuery.of(0, 20))
                .content()
                .stream()
                .filter(r -> code.equals(r.code()))
                .findFirst();
    }

    private Optional<ResolvedCostRole> resolveByMemberAssignment(UUID workspaceId,
                                                                 UUID inChargeUserId,
                                                                 LocalDate targetDate) {
        if (inChargeUserId == null || workspaceId == null) {
            return Optional.empty();
        }
        LocalDate date = targetDate != null ? targetDate : LocalDate.now();
        List<WorkspaceMemberCostRoleAssignment> assignments = memberCostRoleRepository.search(
                        workspaceId, null, inChargeUserId, null, MemberCostRoleStatus.ACTIVE,
                        date, PageQuery.of(0, 50))
                .content()
                .stream()
                .filter(WorkspaceMemberCostRoleAssignment::isDefault)
                .sorted(Comparator.comparing(WorkspaceMemberCostRoleAssignment::effectiveFrom).reversed())
                .toList();
        if (assignments.isEmpty()) {
            return Optional.empty();
        }
        WorkspaceMemberCostRoleAssignment assignment = assignments.getFirst();
        return costRoleRepository.findById(assignment.costRoleId())
                .filter(r -> r.status() == CostRoleStatus.ACTIVE)
                .map(r -> new ResolvedCostRole(r.id(), r.code()));
    }
}
