package com.company.scopery.modules.workspace.orgteam.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.workspace.orgteam.application.query.SearchOrgTeamQuery;
import com.company.scopery.modules.workspace.orgteam.application.response.OrgTeamMemberResponse;
import com.company.scopery.modules.workspace.orgteam.application.response.OrgTeamResponse;
import com.company.scopery.modules.workspace.orgteam.application.response.OrgTeamWorkspaceAssignmentResponse;
import com.company.scopery.modules.workspace.orgteam.domain.enums.OrgTeamStatus;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamMemberRepository;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamRepository;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamWorkspaceAssignmentRepository;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.shared.util.WorkspaceEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrgTeamQueryService {

    private final OrgTeamRepository orgTeamRepository;
    private final OrgTeamMemberRepository orgTeamMemberRepository;
    private final OrgTeamWorkspaceAssignmentRepository assignmentRepository;

    public OrgTeamQueryService(OrgTeamRepository orgTeamRepository,
                                OrgTeamMemberRepository orgTeamMemberRepository,
                                OrgTeamWorkspaceAssignmentRepository assignmentRepository) {
        this.orgTeamRepository = orgTeamRepository;
        this.orgTeamMemberRepository = orgTeamMemberRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @Transactional(readOnly = true)
    public OrgTeamResponse getTeam(UUID teamId) {
        return orgTeamRepository.findById(teamId)
                .map(OrgTeamResponse::from)
                .orElseThrow(() -> WorkspaceExceptions.orgTeamNotFound(teamId));
    }

    @Transactional(readOnly = true)
    public PageResult<OrgTeamResponse> searchTeams(SearchOrgTeamQuery query) {
        OrgTeamStatus status = WorkspaceEnumParser.parseOptional(OrgTeamStatus.class, query.status(),
                "INVALID_ORG_TEAM_STATUS", "status");
        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), "createdAt", false);
        return orgTeamRepository.findAll(query.organizationId(), query.keyword(), status, pageQuery)
                .map(OrgTeamResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResult<OrgTeamMemberResponse> listMembers(UUID teamId, int page, int size) {
        orgTeamRepository.findById(teamId)
                .orElseThrow(() -> WorkspaceExceptions.orgTeamNotFound(teamId));
        PageQuery pageQuery = PageQuery.of(page, size, "joinedAt", false);
        return orgTeamMemberRepository.findAllByTeamId(teamId, pageQuery)
                .map(OrgTeamMemberResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResult<OrgTeamWorkspaceAssignmentResponse> listWorkspaceAssignments(UUID teamId, int page, int size) {
        orgTeamRepository.findById(teamId)
                .orElseThrow(() -> WorkspaceExceptions.orgTeamNotFound(teamId));
        PageQuery pageQuery = PageQuery.of(page, size, "assignedAt", false);
        return assignmentRepository.findAllByTeamId(teamId, pageQuery)
                .map(OrgTeamWorkspaceAssignmentResponse::from);
    }
}
