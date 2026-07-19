package com.company.scopery.modules.ratecard.membercostrole.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.ratecard.membercostrole.application.query.SearchMemberCostRoleQuery;
import com.company.scopery.modules.ratecard.membercostrole.application.response.MemberCostRoleResponse;
import com.company.scopery.modules.ratecard.membercostrole.domain.enums.MemberCostRoleStatus;
import com.company.scopery.modules.ratecard.membercostrole.domain.model.WorkspaceMemberCostRoleRepository;
import com.company.scopery.modules.ratecard.shared.authorization.RateCardAuthorizationService;
import com.company.scopery.modules.ratecard.shared.error.RateCardExceptions;
import com.company.scopery.modules.ratecard.shared.util.RateCardEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class MemberCostRoleQueryService {
    private final WorkspaceMemberCostRoleRepository repository;
    private final RateCardAuthorizationService authorizationService;

    public MemberCostRoleQueryService(WorkspaceMemberCostRoleRepository repository,
                                      RateCardAuthorizationService authorizationService) {
        this.repository = repository; this.authorizationService = authorizationService;
    }

    @Transactional(readOnly = true)
    public MemberCostRoleResponse get(UUID id) {
        var a = repository.findById(id).orElseThrow(() -> RateCardExceptions.memberAssignmentNotFound(id));
        authorizationService.requireMemberCostRoleView(a.workspaceId());
        return MemberCostRoleResponse.from(a);
    }

    @Transactional(readOnly = true)
    public PageResult<MemberCostRoleResponse> search(SearchMemberCostRoleQuery query) {
        authorizationService.requireMemberCostRoleView(query.workspaceId());
        MemberCostRoleStatus status = RateCardEnumParser.parseOptional(MemberCostRoleStatus.class, query.status(),
                "VALIDATION_ERROR", "status");
        return repository.search(query.workspaceId(), query.workspaceMemberId(), query.userId(), query.costRoleId(),
                status, query.effectiveDate(), new PageQuery(query.page(), query.size(), "createdAt", false))
                .map(MemberCostRoleResponse::from);
    }
}
