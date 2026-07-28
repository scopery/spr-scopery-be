package com.company.scopery.modules.iam.grant.application.listeners;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.modules.iam.grant.application.action.EnsureWorkspaceMemberBaselineAccessAction;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceStatus;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.workspace.member.domain.enums.WorkspaceMemberStatus;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Backfills workspace-member baseline grants (productivity + project view, etc.)
 * for every active workspace member on startup.
 */
@Component
@Order(40)
public class WorkspaceMemberBaselineAccessBackfillInitializer
        implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log =
            LoggerFactory.getLogger(WorkspaceMemberBaselineAccessBackfillInitializer.class);

    private final IamAuthResourceRepository authResourceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final EnsureWorkspaceMemberBaselineAccessAction ensureBaselineAccess;

    public WorkspaceMemberBaselineAccessBackfillInitializer(
            IamAuthResourceRepository authResourceRepository,
            WorkspaceMemberRepository workspaceMemberRepository,
            EnsureWorkspaceMemberBaselineAccessAction ensureBaselineAccess) {
        this.authResourceRepository = authResourceRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.ensureBaselineAccess = ensureBaselineAccess;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        int ensured = 0;
        int failed = 0;
        for (IamAuthResource workspaceResource : authResourceRepository
                .findAllByResourceTypeAndStatus(IamResourceType.WORKSPACE, IamResourceStatus.ACTIVE)) {
            UUID workspaceId = workspaceResource.refId();
            int page = 0;
            while (true) {
                var result = workspaceMemberRepository.findAll(
                        workspaceId, null, WorkspaceMemberStatus.ACTIVE, PageQuery.of(page, 100));
                for (WorkspaceMember member : result.content()) {
                    try {
                        ensureBaselineAccess.execute(workspaceId, member.userId());
                        ensured++;
                    } catch (Exception ex) {
                        failed++;
                        log.warn("Baseline productivity grant failed for user {} on workspace {}: {}",
                                member.userId(), workspaceId, ex.getMessage());
                    }
                }
                if (result.content().size() < 100 || result.last()) break;
                page++;
            }
        }
        if (ensured > 0 || failed > 0) {
            log.info("[WorkspaceMemberBaselineAccess] Backfill complete: ensured={}, failed={}",
                    ensured, failed);
        }
    }
}
