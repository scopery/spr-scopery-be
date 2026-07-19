#!/usr/bin/env python3
"""Bulk-generate remaining Phase 26 quality module Java sources."""
from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]
JAVA = ROOT / "src/main/java/com/company/scopery/modules/quality"
TEST = ROOT / "src/test/java/com/company/scopery/modules/quality"
PKG = "com.company.scopery.modules.quality"

def w(rel, content, test=False):
    base = TEST if test else JAVA
    p = base / rel
    p.parent.mkdir(parents=True, exist_ok=True)
    p.write_text(content.strip() + "\n")

# ── QualityPlan lifecycle actions + controller + update ────────────────────
w("qualityplan/application/command/UpdateQualityPlanCommand.java", f"""
package {PKG}.qualityplan.application.command;
import java.util.UUID;
public record UpdateQualityPlanCommand(UUID projectId, UUID qualityPlanId, String name, String description,
        String qualityObjectives, String testStrategy, String entryCriteria, String exitCriteria) {{}}
""")
w("qualityplan/http/request/UpdateQualityPlanRequest.java", f"""
package {PKG}.qualityplan.http.request;
import jakarta.validation.constraints.NotBlank;
public record UpdateQualityPlanRequest(@NotBlank String name, String description, String qualityObjectives,
        String testStrategy, String entryCriteria, String exitCriteria) {{}}
""")

for action, body in [
("UpdateQualityPlanAction", """
package {PKG}.qualityplan.application.action;
import {PKG}.qualityplan.application.command.UpdateQualityPlanCommand;
import {PKG}.qualityplan.application.response.QualityPlanResponse;
import {PKG}.qualityplan.domain.model.QualityPlanRepository;
import {PKG}.shared.activity.QualityActivityLogger;
import {PKG}.shared.authorization.QualityAuthorizationService;
import {PKG}.shared.constant.QualityEntityTypes;
import {PKG}.shared.error.QualityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UpdateQualityPlanAction {{
    private final QualityPlanRepository repo; private final QualityAuthorizationService authorization; private final QualityActivityLogger activityLogger;
    public UpdateQualityPlanAction(QualityPlanRepository repo, QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {{
        this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }}
    @Transactional
    public QualityPlanResponse execute(UpdateQualityPlanCommand c) {{
        authorization.requireQualityUpdate(c.projectId());
        var plan = repo.findByIdAndProjectId(c.qualityPlanId(), c.projectId()).orElseThrow(() -> QualityExceptions.qualityPlanNotFound(c.qualityPlanId()));
        try {{
            var saved = repo.save(plan.update(c.name().trim(), c.description(), c.qualityObjectives(), c.testStrategy(), c.entryCriteria(), c.exitCriteria()));
            return QualityPlanResponse.from(saved);
        }} catch (IllegalStateException ex) {{ throw QualityExceptions.qualityPlanImmutable(c.qualityPlanId()); }}
    }}
}}
"""),
("ApproveQualityPlanAction", """
package {PKG}.qualityplan.application.action;
import com.company.scopery.platform.security.RequestActorResolver;
import {PKG}.qualityplan.application.response.QualityPlanResponse;
import {PKG}.qualityplan.domain.model.QualityPlanRepository;
import {PKG}.shared.activity.QualityActivityLogger;
import {PKG}.shared.authorization.QualityAuthorizationService;
import {PKG}.shared.constant.QualityActivityActions;
import {PKG}.shared.constant.QualityEntityTypes;
import {PKG}.shared.error.QualityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ApproveQualityPlanAction {{
    private final QualityPlanRepository repo; private final QualityAuthorizationService authorization; private final QualityActivityLogger activityLogger;
    public ApproveQualityPlanAction(QualityPlanRepository repo, QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {{
        this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }}
    @Transactional
    public QualityPlanResponse execute(UUID projectId, UUID qualityPlanId) {{
        authorization.requireQualityApprove(projectId);
        var plan = repo.findByIdAndProjectId(qualityPlanId, projectId).orElseThrow(() -> QualityExceptions.qualityPlanNotFound(qualityPlanId));
        try {{
            var saved = repo.save(plan.approve(RequestActorResolver.currentUserIdOrNull()));
            activityLogger.logSuccess(QualityEntityTypes.QUALITY_PLAN, saved.id(), QualityActivityActions.QUALITY_PLAN_APPROVED, "Quality plan approved");
            return QualityPlanResponse.from(saved);
        }} catch (IllegalStateException ex) {{ throw QualityExceptions.qualityPlanImmutable(qualityPlanId); }}
    }}
}}
"""),
("MarkCurrentQualityPlanAction", """
package {PKG}.qualityplan.application.action;
import {PKG}.qualityplan.application.response.QualityPlanResponse;
import {PKG}.qualityplan.domain.model.QualityPlanRepository;
import {PKG}.shared.activity.QualityActivityLogger;
import {PKG}.shared.authorization.QualityAuthorizationService;
import {PKG}.shared.constant.QualityActivityActions;
import {PKG}.shared.constant.QualityEntityTypes;
import {PKG}.shared.error.QualityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class MarkCurrentQualityPlanAction {{
    private final QualityPlanRepository repo; private final QualityAuthorizationService authorization; private final QualityActivityLogger activityLogger;
    public MarkCurrentQualityPlanAction(QualityPlanRepository repo, QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {{
        this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }}
    @Transactional
    public QualityPlanResponse execute(UUID projectId, UUID qualityPlanId) {{
        authorization.requireQualityApprove(projectId);
        var plan = repo.findByIdAndProjectId(qualityPlanId, projectId).orElseThrow(() -> QualityExceptions.qualityPlanNotFound(qualityPlanId));
        for (var other : repo.findByProjectId(projectId)) {{
            if (other.currentFlag() && !other.id().equals(qualityPlanId)) repo.save(other.clearCurrent());
        }}
        var saved = repo.save(plan.markCurrent());
        activityLogger.logSuccess(QualityEntityTypes.QUALITY_PLAN, saved.id(), QualityActivityActions.QUALITY_PLAN_MARKED_CURRENT, "Quality plan marked current");
        return QualityPlanResponse.from(saved);
    }}
}}
"""),
("ArchiveQualityPlanAction", """
package {PKG}.qualityplan.application.action;
import com.company.scopery.platform.security.RequestActorResolver;
import {PKG}.qualityplan.application.response.QualityPlanResponse;
import {PKG}.qualityplan.domain.model.QualityPlanRepository;
import {PKG}.shared.authorization.QualityAuthorizationService;
import {PKG}.shared.error.QualityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ArchiveQualityPlanAction {{
    private final QualityPlanRepository repo; private final QualityAuthorizationService authorization;
    public ArchiveQualityPlanAction(QualityPlanRepository repo, QualityAuthorizationService authorization) {{
        this.repo=repo; this.authorization=authorization;
    }}
    @Transactional
    public QualityPlanResponse execute(UUID projectId, UUID qualityPlanId) {{
        authorization.requireQualityUpdate(projectId);
        var plan = repo.findByIdAndProjectId(qualityPlanId, projectId).orElseThrow(() -> QualityExceptions.qualityPlanNotFound(qualityPlanId));
        return QualityPlanResponse.from(repo.save(plan.archive(RequestActorResolver.currentUserIdOrNull())));
    }}
}}
"""),
]:
    w(f"qualityplan/application/action/{action}.java", body.format(PKG=PKG))

# Check RequestActorResolver exists
print("Checking RequestActorResolver...")
