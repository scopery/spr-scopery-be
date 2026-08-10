package com.company.scopery.modules.iam.me.application.service;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.me.application.query.GetMeQuery;
import com.company.scopery.modules.iam.me.application.response.EffectivePermissionsResponse;
import com.company.scopery.modules.iam.me.application.response.MeResponse;
import com.company.scopery.modules.iam.me.domain.model.MeProfileRepository;
import com.company.scopery.modules.iam.roleassignment.domain.model.IamRoleAssignmentRepository;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamMemberRepository;
import com.company.scopery.modules.workspace.team.domain.model.TeamMemberRepository;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static java.util.Map.entry;

@Service
public class MeQueryService {

    private static final Map<String, List<String>> FE_ALIAS_MAP = Map.ofEntries(
            // Workspace
            entry("VIEW_WORKSPACE",                List.of("workspace.view")),
            entry("UPDATE_WORKSPACE",              List.of("workspace.update")),
            entry("MANAGE_MEMBER",                 List.of("workspace.manage_members")),
            entry("MANAGE_ROLE",                   List.of("workspace.manage_roles")),
            entry("MANAGE_WORKSPACE_SETTING",      List.of("workspace.manage_settings")),
            entry("MANAGE_PROJECT",                List.of("workspace.manage_templates")),
            // Project
            entry("VIEW_PROJECT",    List.of("project.view", "project_section.view")),
            entry("CREATE_PROJECT",  List.of("project.create")),
            entry("UPDATE_PROJECT",  List.of("project.update", "project.manage_members",
                    "project.manage_documents", "project.manage_sections", "project.view_activity",
                    "project_section.create", "project_section.update", "project_section.archive",
                    "project_section.reorder", "project_section.add_document", "project_section.remove_document")),
            entry("ARCHIVE_PROJECT", List.of("project.archive")),
            // Document
            entry("VIEW_DOCUMENT_BY_TYPE",    List.of("document.view", "document.link.view",
                    "document.comment.view", "document.suggestion.view", "document.activity.view",
                    "document.export")),
            entry("CREATE_DOCUMENT_BY_TYPE",  List.of("document.create", "document.attach_to_project",
                    "document.create_from_template", "document.link.create",
                    "document.comment.create", "document.suggestion.create")),
            entry("UPDATE_DOCUMENT_BY_TYPE",  List.of("document.update", "document.pin",
                    "document.move_section", "document.view_ai_metadata", "document.mark_ai_generated",
                    "document.manage_visibility", "document.share_internal", "document.manage_collaborators",
                    "document.comment.update", "document.comment.resolve",
                    "document.suggestion.accept", "document.suggestion.reject")),
            entry("DELETE_DOCUMENT_BY_TYPE",  List.of("document.delete", "document.archive",
                    "document.detach_from_project", "document.link.delete",
                    "document.comment.delete", "document.suggestion.delete")),
            entry("DOCUMENT_HUB_VIEW",   List.of("document_hub.view", "document_hub.search",
                    "document_hub.filter", "document_hub.view_workspace",
                    "document_hub.view_project", "document_hub.manage")),
            entry("DOCUMENT_HUB_CREATE", List.of("document_hub.view", "document.create")),
            entry("DOCUMENT_HUB_UPDATE", List.of("document.update")),
            // Templates
            entry("VIEW_PROJECT_TEMPLATE",    List.of("template.view", "template.variable.view",
                    "template.variable.preview")),
            entry("CREATE_PROJECT_TEMPLATE",  List.of("template.create", "template.create_personal",
                    "template.variable.insert")),
            entry("UPDATE_PROJECT_TEMPLATE",  List.of("template.update", "template.duplicate")),
            entry("PUBLISH_PROJECT_TEMPLATE", List.of("template.publish_system")),
            entry("ARCHIVE_PROJECT_TEMPLATE", List.of("template.archive")),
            // User mention
            entry("VIEW_WORKSPACE_MEMBER",    List.of("user.mention")),
            // AI document features
            entry("AI_ASSISTANT_USE",             List.of(
                    "ai.generate_questions", "ai.assess_clarity", "ai.view_readiness",
                    "ai.generate_document", "ai.summarize_document", "ai.summarize_project_documents",
                    "ai.find_related_documents", "ai.generate_project_brief",
                    "ai.generate_readiness_report_document", "ai.generate_clarity_report_document",
                    "ai.generate_qa_summary_document")),
            entry("AI_PROJECT_PLAN_DRAFT",        List.of("ai.generate_project_brief", "ai.generate_document")),
            entry("AI_PROJECT_PLANNING_RUN",      List.of("ai.generate_questions", "ai.assess_clarity",
                    "ai.view_readiness")),
            // Governance
            entry("GOVERNANCE_POLICY_VIEW",   List.of("governance.view")),
            entry("GOVERNANCE_POLICY_UPDATE", List.of("governance.manage", "governance.view")),
            entry("OBJECT_OWNERSHIP_VIEW",    List.of("governance.view")),
            entry("SYSTEM_VIEW_GOVERNANCE",   List.of("governance.view")),
            entry("SYSTEM_MANAGE_GOVERNANCE", List.of("governance.evaluate", "governance.manage", "governance.view")),
            // AI Agent Admin — broad platform manage right implies all config rights
            entry("AI_PLATFORM_MANAGE",       List.of(
                    "AI_AGENT_CONFIG_VIEW", "AI_AGENT_CONFIG_MANAGE",
                    "AI_PROVIDER_SECRET_VIEW", "AI_PROVIDER_SECRET_MANAGE",
                    "AI_EXECUTION_RUN", "AI_EXECUTION_LOG_VIEW",
                    "AI_PLAYGROUND_USE", "AI_TOOL_VIEW", "AI_TOOL_MANAGE",
                    "agent_control.view", "agent_control.manage",
                    "prompt_registry.view", "prompt_registry.manage",
                    "agent_runtime.view", "ai_usage.view")),
            // Fine-grained AI Agent rights → dot-notation aliases
            entry("AI_AGENT_CONFIG_VIEW",     List.of("agent_control.view", "prompt_registry.view")),
            entry("AI_AGENT_CONFIG_MANAGE",   List.of("AI_AGENT_CONFIG_VIEW",
                    "agent_control.view", "agent_control.manage",
                    "prompt_registry.view", "prompt_registry.manage")),
            entry("AI_EXECUTION_VIEW_OR_RUN", List.of("AI_EXECUTION_RUN", "AI_EXECUTION_LOG_VIEW",
                    "agent_runtime.view")),
            entry("AI_EXECUTION_RUN",         List.of("agent_runtime.view")),
            entry("AI_EXECUTION_LOG_VIEW",    List.of("agent_runtime.view", "ai_usage.view")),
            entry("AI_PLAYGROUND_RUN",        List.of("AI_PLAYGROUND_USE")),
            entry("AI_PROVIDER_SECRET_MANAGE",List.of("AI_PROVIDER_SECRET_VIEW")),
            entry("AI_TOOL_MANAGE",           List.of("AI_TOOL_VIEW")),
            entry("AI_TOOL_EXECUTE",          List.of("AI_TOOL_VIEW"))
    );

    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final MeProfileRepository meProfileRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final OrgTeamMemberRepository orgTeamMemberRepository;
    private final IamRoleAssignmentRepository roleAssignmentRepository;
    private final NamedParameterJdbcTemplate namedJdbc;

    public MeQueryService(CurrentUserAuthorizationService currentUserAuthorizationService,
                          MeProfileRepository meProfileRepository,
                          TeamMemberRepository teamMemberRepository,
                          OrgTeamMemberRepository orgTeamMemberRepository,
                          IamRoleAssignmentRepository roleAssignmentRepository,
                          NamedParameterJdbcTemplate namedJdbc) {
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.meProfileRepository = meProfileRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.orgTeamMemberRepository = orgTeamMemberRepository;
        this.roleAssignmentRepository = roleAssignmentRepository;
        this.namedJdbc = namedJdbc;
    }

    @Transactional(readOnly = true)
    public MeResponse getMe(GetMeQuery query) {
        var userId = currentUserAuthorizationService.resolveCurrentUser().id();
        var profile = meProfileRepository.findByUserId(userId)
                .orElseThrow(() -> IamExceptions.iamUserNotFound(userId));
        return MeResponse.from(profile);
    }

    @Transactional(readOnly = true)
    public EffectivePermissionsResponse getEffectivePermissions() {
        IamUser user = currentUserAuthorizationService.resolveCurrentUser();
        List<UUID> subjectIds = collectSubjectIds(user.id());
        Set<String> rightCodes = fetchRightCodes(subjectIds);
        expandAliases(rightCodes);
        List<String> sorted = new ArrayList<>(rightCodes);
        sorted.sort(String::compareTo);
        return EffectivePermissionsResponse.of(sorted);
    }

    private List<UUID> collectSubjectIds(UUID userId) {
        List<UUID> ids = new ArrayList<>();
        ids.add(userId);
        teamMemberRepository.findByUserId(userId).forEach(m -> ids.add(m.teamId()));
        orgTeamMemberRepository.findAllByUserId(userId).forEach(m -> ids.add(m.teamId()));
        roleAssignmentRepository.findActiveByAssigneeId(userId).forEach(ra -> ids.add(ra.roleId()));
        return ids;
    }

    private Set<String> fetchRightCodes(List<UUID> subjectIds) {
        if (subjectIds.isEmpty()) return new HashSet<>();

        MapSqlParameterSource params = new MapSqlParameterSource("subjectIds", subjectIds);

        String sql = """
                SELECT DISTINCT r.code
                FROM iam_access_grant g
                JOIN iam_access_grant_right agr ON agr.grant_id = g.id
                JOIN iam_right r ON r.id = agr.right_id
                WHERE g.status = 'ACTIVE'
                  AND g.subject_id IN (:subjectIds)
                UNION
                SELECT DISTINCT r.code
                FROM iam_access_grant g
                JOIN iam_access_grant_permission_action agpa ON agpa.grant_id = g.id
                JOIN iam_permission_action pad ON pad.id = agpa.permission_action_id
                JOIN iam_right r ON r.id = pad.right_id
                WHERE g.status = 'ACTIVE'
                  AND g.subject_id IN (:subjectIds)
                  AND pad.right_id IS NOT NULL
                  AND pad.status = 'ACTIVE'
                """;

        return new HashSet<>(namedJdbc.queryForList(sql, params, String.class));
    }

    private void expandAliases(Set<String> codes) {
        Set<String> toAdd = new HashSet<>();
        for (String code : codes) {
            List<String> aliases = FE_ALIAS_MAP.get(code);
            if (aliases != null) {
                toAdd.addAll(aliases);
            }
        }
        codes.addAll(toAdd);
    }
}
