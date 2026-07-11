package com.company.scopery.modules.workspace.orgmember.application.service;

import com.company.scopery.modules.workspace.orgmember.application.query.SearchOrgMemberQuery;
import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.workspace.orgmember.application.response.OrgMemberResponse;
import com.company.scopery.modules.workspace.orgmember.application.response.OrgMembershipSummaryResponse;
import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMemberStatus;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMember;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMemberRepository;
import com.company.scopery.modules.workspace.organization.domain.model.Organization;
import com.company.scopery.modules.workspace.organization.domain.model.OrganizationRepository;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.shared.util.WorkspaceEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrgMemberQueryService {

    private final OrgMemberRepository orgMemberRepository;
    private final OrganizationRepository organizationRepository;

    public OrgMemberQueryService(OrgMemberRepository orgMemberRepository,
                                  OrganizationRepository organizationRepository) {
        this.orgMemberRepository = orgMemberRepository;
        this.organizationRepository = organizationRepository;
    }

    /**
     * Published cross-module read API — other modules (e.g. iam/me) should call this
     * instead of depending on OrgMemberRepository/OrganizationRepository directly.
     */
    @Transactional(readOnly = true)
    public List<OrgMembershipSummaryResponse> findMembershipSummariesByUserId(UUID userId) {
        List<OrgMember> memberships = orgMemberRepository.findAllByUserId(userId);
        if (memberships.isEmpty()) {
            return List.of();
        }
        List<UUID> orgIds = memberships.stream().map(OrgMember::organizationId).toList();
        Map<UUID, Organization> orgById = organizationRepository.findAllByIds(orgIds).stream()
                .collect(Collectors.toMap(Organization::id, o -> o));
        return memberships.stream()
                .map(m -> {
                    Organization org = orgById.get(m.organizationId());
                    String orgName = org != null ? org.name() : m.organizationId().toString();
                    return new OrgMembershipSummaryResponse(
                            m.organizationId(), orgName, m.membershipType().name(), m.status().name());
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public OrgMemberResponse getMember(UUID id) {
        return orgMemberRepository.findById(id)
                .map(OrgMemberResponse::from)
                .orElseThrow(() -> WorkspaceExceptions.orgMemberNotFound(id));
    }

    @Transactional(readOnly = true)
    public PageResult<OrgMemberResponse> searchMembers(SearchOrgMemberQuery query) {
        OrgMemberStatus status = WorkspaceEnumParser.parseOptional(
                OrgMemberStatus.class, query.status(),
                WorkspaceErrorCatalog.INVALID_ORG_MEMBER_STATUS.code(), "status");
        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), "joinedAt", true);
        return orgMemberRepository.findAll(query.organizationId(), query.userId(), status, pageQuery)
                .map(OrgMemberResponse::from);
    }
}
