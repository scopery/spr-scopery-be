package com.company.scopery.modules.workspace.member.application.service;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.workspace.member.application.query.WorkspaceDailySummaryQuery;
import com.company.scopery.modules.workspace.member.application.response.DailySummaryMemberEntry;
import com.company.scopery.modules.workspace.member.application.response.DailySummaryTaskItem;
import com.company.scopery.modules.workspace.member.application.response.WorkspaceDailySummaryResponse;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class WorkspaceTaskSummaryQueryService {

    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final WorkspaceMemberRepository memberRepository;
    private final TaskRepository taskRepository;

    public WorkspaceTaskSummaryQueryService(CurrentUserAuthorizationService currentUserService,
                                            WorkspaceIamIntegrationService iamIntegrationService,
                                            WorkspaceMemberRepository memberRepository,
                                            TaskRepository taskRepository) {
        this.currentUserService = currentUserService;
        this.iamIntegrationService = iamIntegrationService;
        this.memberRepository = memberRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public WorkspaceDailySummaryResponse getDailySummary(WorkspaceDailySummaryQuery q) {
        IamUser actor = currentUserService.resolveCurrentUser();
        iamIntegrationService.requireWorkspaceAccess(q.workspaceId(), actor.id(), IamAuthorities.WORKSPACE_MANAGE);

        LocalDate date = q.date() != null ? q.date() : LocalDate.now();

        List<WorkspaceMember> members = memberRepository.findAllActiveByWorkspaceId(q.workspaceId());
        if (members.isEmpty()) {
            return new WorkspaceDailySummaryResponse(q.workspaceId(), date, List.of());
        }

        List<UUID> userIds = members.stream().map(WorkspaceMember::userId).toList();
        List<Object[]> rows = taskRepository.findDailySummaryRows(q.workspaceId(), userIds, date);

        // Build per-user buckets preserving member order
        Map<UUID, List<DailySummaryTaskItem>> doneByUser = new LinkedHashMap<>();
        Map<UUID, List<DailySummaryTaskItem>> inProgressByUser = new LinkedHashMap<>();
        for (UUID uid : userIds) {
            doneByUser.put(uid, new ArrayList<>());
            inProgressByUser.put(uid, new ArrayList<>());
        }

        for (Object[] row : rows) {
            UUID taskId        = toUuid(row[0]);
            String code        = (String) row[1];
            String title       = (String) row[2];
            UUID projectId     = toUuid(row[3]);
            String projectName = (String) row[4];
            UUID phaseId       = toUuid(row[5]);
            String phaseName   = (String) row[6];
            UUID assigneeId    = toUuid(row[7]);
            Timestamp completedAt = row[8] instanceof Timestamp t ? t : null;
            Timestamp startedAt   = row[9] instanceof Timestamp t ? t : null;
            BigDecimal hours   = row[10] instanceof Number n ? new BigDecimal(n.toString()) : null;
            String status      = (String) row[11];

            DailySummaryTaskItem item = new DailySummaryTaskItem(
                    taskId, code, title, projectId, projectName, phaseId, phaseName,
                    hours, status,
                    completedAt != null ? completedAt.toInstant() : null,
                    startedAt   != null ? startedAt.toInstant()   : null);

            if ("DONE".equals(status)) {
                doneByUser.computeIfAbsent(assigneeId, k -> new ArrayList<>()).add(item);
            } else {
                inProgressByUser.computeIfAbsent(assigneeId, k -> new ArrayList<>()).add(item);
            }
        }

        List<DailySummaryMemberEntry> entries = userIds.stream()
                .map(uid -> new DailySummaryMemberEntry(
                        uid,
                        doneByUser.getOrDefault(uid, List.of()),
                        inProgressByUser.getOrDefault(uid, List.of())))
                .toList();

        return new WorkspaceDailySummaryResponse(q.workspaceId(), date, entries);
    }

    private UUID toUuid(Object val) {
        if (val == null) return null;
        if (val instanceof UUID u) return u;
        return UUID.fromString(val.toString());
    }
}
