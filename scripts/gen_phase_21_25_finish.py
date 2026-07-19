#!/usr/bin/env python3
"""Generate remaining Phase 24/25 application + HTTP layers and helpers."""
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def W(rel: str, content: str) -> None:
    p = ROOT / rel
    p.parent.mkdir(parents=True, exist_ok=True)
    p.write_text(content.strip() + "\n", encoding="utf-8")
    print("W", rel)


def main() -> None:
    # ---- RAID status helpers ----
    W(
        "src/main/java/com/company/scopery/modules/raid/raiditem/application/action/UpdateRaidItemAction.java",
        """
package com.company.scopery.modules.raid.raiditem.application.action;
import com.company.scopery.modules.raid.raiditem.application.response.RaidItemResponse;
import com.company.scopery.modules.raid.raiditem.domain.enums.*;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItemRepository;
import com.company.scopery.modules.raid.shared.activity.RaidActivityLogger;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.constant.RaidActivityActions;
import com.company.scopery.modules.raid.shared.constant.RaidEntityTypes;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import com.company.scopery.modules.raid.shared.util.RaidEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map; import java.util.UUID;
@Component
public class UpdateRaidItemAction {
    private final RaidItemRepository items; private final RaidAuthorizationService authorization; private final RaidActivityLogger activityLogger;
    public UpdateRaidItemAction(RaidItemRepository items, RaidAuthorizationService authorization, RaidActivityLogger activityLogger) {
        this.items=items; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public RaidItemResponse execute(UUID projectId, UUID itemId, Map<String, Object> body) {
        authorization.requireUpdate(projectId);
        var item = items.findByIdAndProjectId(itemId, projectId).orElseThrow(() -> RaidExceptions.itemNotFound(itemId));
        String title = body.get("title") == null ? item.title() : String.valueOf(body.get("title"));
        if (title == null || title.isBlank()) throw RaidExceptions.titleRequired();
        String description = body.containsKey("description") ? str(body.get("description")) : item.description();
        UUID owner = body.containsKey("ownerUserId") ? uuid(body.get("ownerUserId")) : item.ownerUserId();
        item = item.update(title.trim(), description, owner,
                body.containsKey("severity") ? str(body.get("severity")) : item.severity(),
                body.containsKey("issueCategory") ? str(body.get("issueCategory")) : item.issueCategory(),
                body.containsKey("impactSummary") ? str(body.get("impactSummary")) : item.impactSummary(),
                body.containsKey("rootCause") ? str(body.get("rootCause")) : item.rootCause(),
                body.containsKey("resolutionPlan") ? str(body.get("resolutionPlan")) : item.resolutionPlan(),
                body.containsKey("dependencyType") ? str(body.get("dependencyType")) : item.dependencyType(),
                body.containsKey("assumptionStatement") ? str(body.get("assumptionStatement")) : item.assumptionStatement(),
                body.containsKey("validationStatus") ? str(body.get("validationStatus")) : item.validationStatus());
        if (item.type() == RaidItemType.RISK && (body.containsKey("probability") || body.containsKey("impact") || body.containsKey("riskResponseStrategy"))) {
            RaidProbability p = body.get("probability")==null ? item.probability() : RaidEnumParser.parseRequired(RaidProbability.class, str(body.get("probability")), "probability");
            RaidImpact i = body.get("impact")==null ? item.impact() : RaidEnumParser.parseRequired(RaidImpact.class, str(body.get("impact")), "impact");
            RiskResponseStrategy s = body.get("riskResponseStrategy")==null ? item.riskResponseStrategy() :
                    RaidEnumParser.parseRequired(RiskResponseStrategy.class, str(body.get("riskResponseStrategy")), "riskResponseStrategy");
            item = item.withRiskFields(p, i, s, body.containsKey("riskTrigger") ? str(body.get("riskTrigger")) : item.riskTrigger());
        }
        item = items.save(item);
        activityLogger.logSuccess(RaidEntityTypes.ITEM, item.id(), RaidActivityActions.ITEM_UPDATED, "RAID item updated");
        return RaidItemResponse.from(item);
    }
    private static String str(Object o){ return o==null?null:String.valueOf(o); }
    private static UUID uuid(Object o){ return o==null||String.valueOf(o).isBlank()?null:UUID.fromString(String.valueOf(o)); }
}
""",
    )

    W(
        "src/main/java/com/company/scopery/modules/raid/raiditem/application/action/ChangeRaidItemStatusAction.java",
        """
package com.company.scopery.modules.raid.raiditem.application.action;
import com.company.scopery.modules.raid.raiditem.application.response.RaidItemResponse;
import com.company.scopery.modules.raid.raiditem.domain.enums.RaidItemStatus;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItemRepository;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import com.company.scopery.modules.raid.shared.util.RaidEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map; import java.util.UUID;
@Component
public class ChangeRaidItemStatusAction {
    private final RaidItemRepository items; private final RaidAuthorizationService authorization;
    public ChangeRaidItemStatusAction(RaidItemRepository items, RaidAuthorizationService authorization) {
        this.items=items; this.authorization=authorization;
    }
    @Transactional
    public RaidItemResponse execute(UUID projectId, UUID itemId, Map<String, Object> body) {
        authorization.requireUpdate(projectId);
        var item = items.findByIdAndProjectId(itemId, projectId).orElseThrow(() -> RaidExceptions.itemNotFound(itemId));
        RaidItemStatus next = RaidEnumParser.parseRequired(RaidItemStatus.class, String.valueOf(body.get("status")), "status");
        return RaidItemResponse.from(items.save(item.withStatus(next)));
    }
}
""",
    )

    W(
        "src/main/java/com/company/scopery/modules/raid/raiditem/application/action/CloseRaidItemAction.java",
        """
package com.company.scopery.modules.raid.raiditem.application.action;
import com.company.scopery.modules.raid.raiditem.application.response.RaidItemResponse;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItemRepository;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CloseRaidItemAction {
    private final RaidItemRepository items; private final RaidAuthorizationService authorization;
    public CloseRaidItemAction(RaidItemRepository items, RaidAuthorizationService authorization) {
        this.items=items; this.authorization=authorization;
    }
    @Transactional
    public RaidItemResponse execute(UUID projectId, UUID itemId) {
        authorization.requireUpdate(projectId);
        var item = items.findByIdAndProjectId(itemId, projectId).orElseThrow(() -> RaidExceptions.itemNotFound(itemId));
        return RaidItemResponse.from(items.save(item.close()));
    }
}
""",
    )

    W(
        "src/main/java/com/company/scopery/modules/raid/raiditem/application/action/ReopenRaidItemAction.java",
        """
package com.company.scopery.modules.raid.raiditem.application.action;
import com.company.scopery.modules.raid.raiditem.application.response.RaidItemResponse;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItemRepository;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ReopenRaidItemAction {
    private final RaidItemRepository items; private final RaidAuthorizationService authorization;
    public ReopenRaidItemAction(RaidItemRepository items, RaidAuthorizationService authorization) {
        this.items=items; this.authorization=authorization;
    }
    @Transactional
    public RaidItemResponse execute(UUID projectId, UUID itemId) {
        authorization.requireUpdate(projectId);
        var item = items.findByIdAndProjectId(itemId, projectId).orElseThrow(() -> RaidExceptions.itemNotFound(itemId));
        return RaidItemResponse.from(items.save(item.reopen()));
    }
}
""",
    )

    W(
        "src/main/java/com/company/scopery/modules/raid/raiditem/application/action/ArchiveRaidItemAction.java",
        """
package com.company.scopery.modules.raid.raiditem.application.action;
import com.company.scopery.modules.raid.raiditem.application.response.RaidItemResponse;
import com.company.scopery.modules.raid.raiditem.domain.enums.RaidItemStatus;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItemRepository;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ArchiveRaidItemAction {
    private final RaidItemRepository items; private final RaidAuthorizationService authorization;
    public ArchiveRaidItemAction(RaidItemRepository items, RaidAuthorizationService authorization) {
        this.items=items; this.authorization=authorization;
    }
    @Transactional
    public RaidItemResponse execute(UUID projectId, UUID itemId) {
        authorization.requireArchive(projectId);
        var item = items.findByIdAndProjectId(itemId, projectId).orElseThrow(() -> RaidExceptions.itemNotFound(itemId));
        // CLOSED is the terminal archived-equivalent status in v1 domain model
        return RaidItemResponse.from(items.save(item.withStatus(RaidItemStatus.CLOSED)));
    }
}
""",
    )

    # ---- RAID actions ----
    W(
        "src/main/java/com/company/scopery/modules/raid/raidaction/application/action/CreateRaidActionAction.java",
        """
package com.company.scopery.modules.raid.raidaction.application.action;
import com.company.scopery.modules.raid.raidaction.application.response.RaidActionResponse;
import com.company.scopery.modules.raid.raidaction.domain.model.RaidAction;
import com.company.scopery.modules.raid.raidaction.domain.model.RaidActionRepository;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItemRepository;
import com.company.scopery.modules.raid.shared.activity.RaidActivityLogger;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.constant.RaidActivityActions;
import com.company.scopery.modules.raid.shared.constant.RaidEntityTypes;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate; import java.util.Map; import java.util.UUID;
@Component
public class CreateRaidActionAction {
    private final RaidItemRepository items; private final RaidActionRepository actions;
    private final RaidAuthorizationService authorization; private final RaidActivityLogger activityLogger;
    public CreateRaidActionAction(RaidItemRepository items, RaidActionRepository actions,
                                  RaidAuthorizationService authorization, RaidActivityLogger activityLogger) {
        this.items=items; this.actions=actions; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public RaidActionResponse execute(UUID projectId, UUID raidItemId, Map<String, Object> body) {
        authorization.requireCreate(projectId);
        items.findByIdAndProjectId(raidItemId, projectId).orElseThrow(() -> RaidExceptions.itemNotFound(raidItemId));
        String title = body.get("title") == null ? null : String.valueOf(body.get("title"));
        if (title == null || title.isBlank()) throw RaidExceptions.titleRequired();
        LocalDate due = body.get("dueDate") == null || String.valueOf(body.get("dueDate")).isBlank()
                ? null : LocalDate.parse(String.valueOf(body.get("dueDate")));
        UUID owner = body.get("ownerUserId") == null || String.valueOf(body.get("ownerUserId")).isBlank()
                ? null : UUID.fromString(String.valueOf(body.get("ownerUserId")));
        String description = body.get("description") == null ? null : String.valueOf(body.get("description"));
        RaidAction action = actions.save(RaidAction.create(raidItemId, projectId, title.trim(), description, owner, due));
        activityLogger.logSuccess(RaidEntityTypes.ACTION, action.id(), RaidActivityActions.ACTION_CREATED, "RAID action created");
        return RaidActionResponse.from(action);
    }
}
""",
    )

    W(
        "src/main/java/com/company/scopery/modules/raid/raidaction/application/action/CompleteRaidActionAction.java",
        """
package com.company.scopery.modules.raid.raidaction.application.action;
import com.company.scopery.modules.raid.raidaction.application.response.RaidActionResponse;
import com.company.scopery.modules.raid.raidaction.domain.model.RaidActionRepository;
import com.company.scopery.modules.raid.shared.activity.RaidActivityLogger;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.constant.RaidActivityActions;
import com.company.scopery.modules.raid.shared.constant.RaidEntityTypes;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map; import java.util.UUID;
@Component
public class CompleteRaidActionAction {
    private final RaidActionRepository actions; private final RaidAuthorizationService authorization; private final RaidActivityLogger activityLogger;
    public CompleteRaidActionAction(RaidActionRepository actions, RaidAuthorizationService authorization, RaidActivityLogger activityLogger) {
        this.actions=actions; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public RaidActionResponse execute(UUID projectId, UUID actionId, Map<String, Object> body) {
        authorization.requireUpdate(projectId);
        var action = actions.findByIdAndProjectId(actionId, projectId).orElseThrow(() -> RaidExceptions.actionNotFound(actionId));
        String note = body == null || body.get("completionNote") == null ? null : String.valueOf(body.get("completionNote"));
        action = actions.save(action.complete(note));
        activityLogger.logSuccess(RaidEntityTypes.ACTION, action.id(), RaidActivityActions.ACTION_COMPLETED, "RAID action completed");
        return RaidActionResponse.from(action);
    }
}
""",
    )

    W(
        "src/main/java/com/company/scopery/modules/raid/raidaction/application/service/RaidActionQueryService.java",
        """
package com.company.scopery.modules.raid.raidaction.application.service;
import com.company.scopery.modules.raid.raidaction.application.response.RaidActionResponse;
import com.company.scopery.modules.raid.raidaction.domain.model.RaidActionRepository;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItemRepository;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class RaidActionQueryService {
    private final RaidActionRepository actions; private final RaidItemRepository items; private final RaidAuthorizationService authorization;
    public RaidActionQueryService(RaidActionRepository actions, RaidItemRepository items, RaidAuthorizationService authorization) {
        this.actions=actions; this.items=items; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<RaidActionResponse> listByItem(UUID projectId, UUID raidItemId) {
        authorization.requireView(projectId);
        items.findByIdAndProjectId(raidItemId, projectId).orElseThrow(() -> RaidExceptions.itemNotFound(raidItemId));
        return actions.findByRaidItemId(raidItemId).stream().map(RaidActionResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public RaidActionResponse get(UUID projectId, UUID actionId) {
        authorization.requireView(projectId);
        return actions.findByIdAndProjectId(actionId, projectId).map(RaidActionResponse::from)
                .orElseThrow(() -> RaidExceptions.actionNotFound(actionId));
    }
}
""",
    )

    # ---- Decision actions ----
    W(
        "src/main/java/com/company/scopery/modules/raid/decision/application/action/CreateDecisionAction.java",
        """
package com.company.scopery.modules.raid.decision.application.action;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.raid.decision.application.response.DecisionRecordResponse;
import com.company.scopery.modules.raid.decision.domain.enums.DecisionCategory;
import com.company.scopery.modules.raid.decision.domain.model.DecisionRecord;
import com.company.scopery.modules.raid.decision.domain.model.DecisionRecordRepository;
import com.company.scopery.modules.raid.shared.activity.RaidActivityLogger;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.constant.RaidActivityActions;
import com.company.scopery.modules.raid.shared.constant.RaidEntityTypes;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import com.company.scopery.modules.raid.shared.util.RaidEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map; import java.util.UUID;
@Component
public class CreateDecisionAction {
    private final ProjectRepository projects; private final DecisionRecordRepository decisions;
    private final RaidAuthorizationService authorization; private final RaidActivityLogger activityLogger;
    public CreateDecisionAction(ProjectRepository projects, DecisionRecordRepository decisions,
                                RaidAuthorizationService authorization, RaidActivityLogger activityLogger) {
        this.projects=projects; this.decisions=decisions; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public DecisionRecordResponse execute(UUID projectId, Map<String, Object> body) {
        authorization.requireDecisionCreate(projectId);
        var project = projects.findById(projectId).orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        if (project.status() == ProjectStatus.ARCHIVED) throw RaidExceptions.projectArchived(projectId);
        String title = body.get("title") == null ? null : String.valueOf(body.get("title"));
        if (title == null || title.isBlank()) throw RaidExceptions.titleRequired();
        String rationale = body.get("rationale") == null ? null : String.valueOf(body.get("rationale"));
        if (rationale == null || rationale.isBlank()) throw RaidExceptions.rationaleRequired();
        DecisionCategory category = RaidEnumParser.parseRequired(DecisionCategory.class, String.valueOf(body.get("category")), "category");
        String code = body.get("code") == null ? null : String.valueOf(body.get("code"));
        DecisionRecord d = decisions.save(DecisionRecord.create(project.id(), project.workspaceId(), code, title.trim(), category, rationale.trim()));
        activityLogger.logSuccess(RaidEntityTypes.DECISION, d.id(), RaidActivityActions.DECISION_CREATED, "Decision created");
        return DecisionRecordResponse.from(d);
    }
}
""",
    )

    W(
        "src/main/java/com/company/scopery/modules/raid/decision/application/action/DecideDecisionAction.java",
        """
package com.company.scopery.modules.raid.decision.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.raid.decision.application.response.DecisionRecordResponse;
import com.company.scopery.modules.raid.decision.domain.model.DecisionRecordRepository;
import com.company.scopery.modules.raid.shared.activity.RaidActivityLogger;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.constant.RaidActivityActions;
import com.company.scopery.modules.raid.shared.constant.RaidEntityTypes;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map; import java.util.UUID;
@Component
public class DecideDecisionAction {
    private final DecisionRecordRepository decisions; private final RaidAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser; private final RaidActivityLogger activityLogger;
    public DecideDecisionAction(DecisionRecordRepository decisions, RaidAuthorizationService authorization,
                                CurrentUserAuthorizationService currentUser, RaidActivityLogger activityLogger) {
        this.decisions=decisions; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public DecisionRecordResponse execute(UUID projectId, UUID decisionId, Map<String, Object> body) {
        authorization.requireDecide(projectId);
        String outcome = body == null || body.get("outcome") == null ? null : String.valueOf(body.get("outcome"));
        if (outcome == null || outcome.isBlank()) throw RaidExceptions.outcomeRequired();
        var actor = currentUser.resolveCurrentUser();
        var d = decisions.findByIdAndProjectId(decisionId, projectId).orElseThrow(() -> RaidExceptions.decisionNotFound(decisionId));
        d = decisions.save(d.decide(actor.id(), outcome.trim()));
        activityLogger.logSuccess(RaidEntityTypes.DECISION, d.id(), RaidActivityActions.DECISION_DECIDED, "Decision decided");
        return DecisionRecordResponse.from(d);
    }
}
""",
    )

    W(
        "src/main/java/com/company/scopery/modules/raid/decision/application/action/RejectDecisionAction.java",
        """
package com.company.scopery.modules.raid.decision.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.raid.decision.application.response.DecisionRecordResponse;
import com.company.scopery.modules.raid.decision.domain.model.DecisionRecordRepository;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map; import java.util.UUID;
@Component
public class RejectDecisionAction {
    private final DecisionRecordRepository decisions; private final RaidAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    public RejectDecisionAction(DecisionRecordRepository decisions, RaidAuthorizationService authorization,
                                CurrentUserAuthorizationService currentUser) {
        this.decisions=decisions; this.authorization=authorization; this.currentUser=currentUser;
    }
    @Transactional
    public DecisionRecordResponse execute(UUID projectId, UUID decisionId, Map<String, Object> body) {
        authorization.requireDecide(projectId);
        String reason = body == null || body.get("reason") == null ? null : String.valueOf(body.get("reason"));
        if (reason == null || reason.isBlank()) throw RaidExceptions.outcomeRequired();
        var actor = currentUser.resolveCurrentUser();
        var d = decisions.findByIdAndProjectId(decisionId, projectId).orElseThrow(() -> RaidExceptions.decisionNotFound(decisionId));
        return DecisionRecordResponse.from(decisions.save(d.reject(actor.id(), reason.trim())));
    }
}
""",
    )

    W(
        "src/main/java/com/company/scopery/modules/raid/decision/application/action/ArchiveDecisionAction.java",
        """
package com.company.scopery.modules.raid.decision.application.action;
import com.company.scopery.modules.raid.decision.application.response.DecisionRecordResponse;
import com.company.scopery.modules.raid.decision.domain.model.DecisionRecordRepository;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ArchiveDecisionAction {
    private final DecisionRecordRepository decisions; private final RaidAuthorizationService authorization;
    public ArchiveDecisionAction(DecisionRecordRepository decisions, RaidAuthorizationService authorization) {
        this.decisions=decisions; this.authorization=authorization;
    }
    @Transactional
    public DecisionRecordResponse execute(UUID projectId, UUID decisionId) {
        authorization.requireDecisionUpdate(projectId);
        var d = decisions.findByIdAndProjectId(decisionId, projectId).orElseThrow(() -> RaidExceptions.decisionNotFound(decisionId));
        return DecisionRecordResponse.from(decisions.save(d.archive()));
    }
}
""",
    )

    W(
        "src/main/java/com/company/scopery/modules/raid/decision/application/action/CreateDecisionOptionAction.java",
        """
package com.company.scopery.modules.raid.decision.application.action;
import com.company.scopery.modules.raid.decision.domain.model.DecisionOption;
import com.company.scopery.modules.raid.decision.domain.model.DecisionOptionRepository;
import com.company.scopery.modules.raid.decision.domain.model.DecisionRecordRepository;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.LinkedHashMap; import java.util.Map; import java.util.UUID;
@Component
public class CreateDecisionOptionAction {
    private final DecisionRecordRepository decisions; private final DecisionOptionRepository options;
    private final RaidAuthorizationService authorization;
    public CreateDecisionOptionAction(DecisionRecordRepository decisions, DecisionOptionRepository options,
                                      RaidAuthorizationService authorization) {
        this.decisions=decisions; this.options=options; this.authorization=authorization;
    }
    @Transactional
    public Map<String, Object> execute(UUID projectId, UUID decisionId, Map<String, Object> body) {
        authorization.requireDecisionUpdate(projectId);
        decisions.findByIdAndProjectId(decisionId, projectId).orElseThrow(() -> RaidExceptions.decisionNotFound(decisionId));
        String title = body.get("optionTitle") == null ? null : String.valueOf(body.get("optionTitle"));
        if (title == null || title.isBlank()) throw RaidExceptions.titleRequired();
        DecisionOption o = options.save(DecisionOption.create(decisionId, projectId, title.trim(),
                str(body.get("optionDescription")), str(body.get("pros")), str(body.get("cons")), str(body.get("estimatedImpact"))));
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("id", o.id()); out.put("decisionId", o.decisionId()); out.put("optionTitle", o.optionTitle());
        out.put("selectedFlag", o.selectedFlag());
        return out;
    }
    private static String str(Object o){ return o==null?null:String.valueOf(o); }
}
""",
    )

    W(
        "src/main/java/com/company/scopery/modules/raid/decision/application/action/UpsertDecisionImpactAction.java",
        """
package com.company.scopery.modules.raid.decision.application.action;
import com.company.scopery.modules.raid.decision.domain.model.DecisionImpact;
import com.company.scopery.modules.raid.decision.domain.model.DecisionImpactRepository;
import com.company.scopery.modules.raid.decision.domain.model.DecisionRecordRepository;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal; import java.util.LinkedHashMap; import java.util.Map; import java.util.UUID;
@Component
public class UpsertDecisionImpactAction {
    private final DecisionRecordRepository decisions; private final DecisionImpactRepository impacts;
    private final RaidAuthorizationService authorization;
    public UpsertDecisionImpactAction(DecisionRecordRepository decisions, DecisionImpactRepository impacts,
                                      RaidAuthorizationService authorization) {
        this.decisions=decisions; this.impacts=impacts; this.authorization=authorization;
    }
    @Transactional
    public Map<String, Object> execute(UUID projectId, UUID decisionId, Map<String, Object> body) {
        authorization.requireDecisionUpdate(projectId);
        decisions.findByIdAndProjectId(decisionId, projectId).orElseThrow(() -> RaidExceptions.decisionNotFound(decisionId));
        DecisionImpact existing = impacts.findByDecisionId(decisionId).orElse(null);
        DecisionImpact impact = DecisionImpact.create(decisionId, projectId,
                str(body.get("scopeImpact")),
                body.get("scheduleImpactDays") == null ? null : Integer.valueOf(String.valueOf(body.get("scheduleImpactDays"))),
                dec(body.get("estimateHoursImpact")), dec(body.get("costImpact")),
                dec(body.get("revenueImpact")), dec(body.get("marginImpact")),
                str(body.get("riskImpact")), str(body.get("deliverableImpact")), str(body.get("acceptanceImpact")));
        if (existing != null) {
            impact = new DecisionImpact(existing.id(), decisionId, projectId, impact.scopeImpact(), impact.scheduleImpactDays(),
                    impact.estimateHoursImpact(), impact.costImpact(), impact.revenueImpact(), impact.marginImpact(),
                    impact.riskImpact(), impact.deliverableImpact(), impact.acceptanceImpact(), existing.version(), existing.createdAt());
        }
        impact = impacts.save(impact);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("id", impact.id()); out.put("decisionId", impact.decisionId());
        out.put("scopeImpact", impact.scopeImpact()); out.put("scheduleImpactDays", impact.scheduleImpactDays());
        out.put("costImpact", impact.costImpact());
        return out;
    }
    private static String str(Object o){ return o==null?null:String.valueOf(o); }
    private static BigDecimal dec(Object o){ return o==null||String.valueOf(o).isBlank()?null:new BigDecimal(String.valueOf(o)); }
}
""",
    )

    W(
        "src/main/java/com/company/scopery/modules/raid/decision/application/service/DecisionQueryService.java",
        """
package com.company.scopery.modules.raid.decision.application.service;
import com.company.scopery.modules.raid.decision.application.response.DecisionRecordResponse;
import com.company.scopery.modules.raid.decision.domain.model.DecisionImpactRepository;
import com.company.scopery.modules.raid.decision.domain.model.DecisionOptionRepository;
import com.company.scopery.modules.raid.decision.domain.model.DecisionRecordRepository;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.LinkedHashMap; import java.util.List; import java.util.Map; import java.util.UUID;
@Service
public class DecisionQueryService {
    private final DecisionRecordRepository decisions; private final DecisionOptionRepository options;
    private final DecisionImpactRepository impacts; private final RaidAuthorizationService authorization;
    public DecisionQueryService(DecisionRecordRepository decisions, DecisionOptionRepository options,
                                DecisionImpactRepository impacts, RaidAuthorizationService authorization) {
        this.decisions=decisions; this.options=options; this.impacts=impacts; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<DecisionRecordResponse> list(UUID projectId) {
        authorization.requireDecisionView(projectId);
        return decisions.findByProjectId(projectId).stream().map(DecisionRecordResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public DecisionRecordResponse get(UUID projectId, UUID id) {
        authorization.requireDecisionView(projectId);
        return decisions.findByIdAndProjectId(id, projectId).map(DecisionRecordResponse::from)
                .orElseThrow(() -> RaidExceptions.decisionNotFound(id));
    }
    @Transactional(readOnly=true)
    public List<Map<String, Object>> listOptions(UUID projectId, UUID decisionId) {
        authorization.requireDecisionView(projectId);
        decisions.findByIdAndProjectId(decisionId, projectId).orElseThrow(() -> RaidExceptions.decisionNotFound(decisionId));
        return options.findByDecisionId(decisionId).stream().map(o -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", o.id()); m.put("optionTitle", o.optionTitle()); m.put("selectedFlag", o.selectedFlag());
            m.put("pros", o.pros()); m.put("cons", o.cons());
            return m;
        }).toList();
    }
    @Transactional(readOnly=true)
    public Map<String, Object> getImpact(UUID projectId, UUID decisionId) {
        authorization.requireDecisionView(projectId);
        decisions.findByIdAndProjectId(decisionId, projectId).orElseThrow(() -> RaidExceptions.decisionNotFound(decisionId));
        return impacts.findByDecisionId(decisionId).map(i -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", i.id()); m.put("scopeImpact", i.scopeImpact()); m.put("scheduleImpactDays", i.scheduleImpactDays());
            m.put("costImpact", i.costImpact()); m.put("revenueImpact", i.revenueImpact()); m.put("marginImpact", i.marginImpact());
            if (!authorization.canViewFinance(projectId)) {
                m.put("costImpact", null); m.put("revenueImpact", null); m.put("marginImpact", null);
                m.put("financeMasked", true);
            }
            return m;
        }).orElseGet(LinkedHashMap::new);
    }
}
""",
    )

    # ---- Controllers ----
    W(
        "src/main/java/com/company/scopery/modules/raid/raiditem/http/controller/RaidItemController.java",
        """
package com.company.scopery.modules.raid.raiditem.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.raid.raidaction.application.action.CreateRaidActionAction;
import com.company.scopery.modules.raid.raidaction.application.response.RaidActionResponse;
import com.company.scopery.modules.raid.raidaction.application.service.RaidActionQueryService;
import com.company.scopery.modules.raid.raiditem.application.action.*;
import com.company.scopery.modules.raid.raiditem.application.response.RaidItemResponse;
import com.company.scopery.modules.raid.raiditem.application.service.RaidItemQueryService;
import com.company.scopery.modules.raid.shared.constant.RaidApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.Map; import java.util.UUID;
@RestController @RequestMapping(RaidApiPaths.ITEMS) @Tag(name="RAID - Items")
public class RaidItemController {
    private final CreateRaidItemAction create; private final UpdateRaidItemAction update;
    private final ChangeRaidItemStatusAction changeStatus; private final ResolveRaidItemAction resolve;
    private final CloseRaidItemAction close; private final ReopenRaidItemAction reopen;
    private final EscalateRaidItemAction escalate; private final ArchiveRaidItemAction archive;
    private final ConvertRiskToIssueAction convert; private final CreateChangeRequestFromRaidAction createCr;
    private final CreateRaidActionAction createAction; private final RaidItemQueryService query;
    private final RaidActionQueryService actionQuery;
    public RaidItemController(CreateRaidItemAction create, UpdateRaidItemAction update, ChangeRaidItemStatusAction changeStatus,
                              ResolveRaidItemAction resolve, CloseRaidItemAction close, ReopenRaidItemAction reopen,
                              EscalateRaidItemAction escalate, ArchiveRaidItemAction archive, ConvertRiskToIssueAction convert,
                              CreateChangeRequestFromRaidAction createCr, CreateRaidActionAction createAction,
                              RaidItemQueryService query, RaidActionQueryService actionQuery) {
        this.create=create; this.update=update; this.changeStatus=changeStatus; this.resolve=resolve; this.close=close;
        this.reopen=reopen; this.escalate=escalate; this.archive=archive; this.convert=convert; this.createCr=createCr;
        this.createAction=createAction; this.query=query; this.actionQuery=actionQuery;
    }
    @PostMapping @Operation(summary="Create RAID item")
    public ApiResponse<RaidItemResponse> create(@PathVariable UUID projectId, @RequestBody Map<String, Object> body) {
        return ApiResponse.success(create.execute(projectId, body));
    }
    @GetMapping @Operation(summary="List RAID items")
    public ApiResponse<List<RaidItemResponse>> list(@PathVariable UUID projectId, @RequestParam(required=false) String type) {
        return ApiResponse.success(query.list(projectId, type));
    }
    @GetMapping("/{raidItemId}") @Operation(summary="Get RAID item")
    public ApiResponse<RaidItemResponse> get(@PathVariable UUID projectId, @PathVariable UUID raidItemId) {
        return ApiResponse.success(query.get(projectId, raidItemId));
    }
    @PutMapping("/{raidItemId}") @Operation(summary="Update RAID item")
    public ApiResponse<RaidItemResponse> update(@PathVariable UUID projectId, @PathVariable UUID raidItemId, @RequestBody Map<String, Object> body) {
        return ApiResponse.success(update.execute(projectId, raidItemId, body));
    }
    @PatchMapping("/{raidItemId}/status") @Operation(summary="Change RAID item status")
    public ApiResponse<RaidItemResponse> status(@PathVariable UUID projectId, @PathVariable UUID raidItemId, @RequestBody Map<String, Object> body) {
        return ApiResponse.success(changeStatus.execute(projectId, raidItemId, body));
    }
    @PostMapping("/{raidItemId}/resolve") @Operation(summary="Resolve RAID item")
    public ApiResponse<RaidItemResponse> resolve(@PathVariable UUID projectId, @PathVariable UUID raidItemId, @RequestBody Map<String, Object> body) {
        return ApiResponse.success(resolve.execute(projectId, raidItemId, body.get("outcomeNote")==null?null:String.valueOf(body.get("outcomeNote"))));
    }
    @PostMapping("/{raidItemId}/close") @Operation(summary="Close RAID item")
    public ApiResponse<RaidItemResponse> close(@PathVariable UUID projectId, @PathVariable UUID raidItemId) {
        return ApiResponse.success(close.execute(projectId, raidItemId));
    }
    @PostMapping("/{raidItemId}/reopen") @Operation(summary="Reopen RAID item")
    public ApiResponse<RaidItemResponse> reopen(@PathVariable UUID projectId, @PathVariable UUID raidItemId) {
        return ApiResponse.success(reopen.execute(projectId, raidItemId));
    }
    @PostMapping("/{raidItemId}/escalate") @Operation(summary="Escalate RAID item")
    public ApiResponse<RaidItemResponse> escalate(@PathVariable UUID projectId, @PathVariable UUID raidItemId, @RequestBody Map<String, Object> body) {
        return ApiResponse.success(escalate.execute(projectId, raidItemId,
                body.get("escalationLevel")==null?null:String.valueOf(body.get("escalationLevel")),
                body.get("reason")==null?null:String.valueOf(body.get("reason"))));
    }
    @PatchMapping("/{raidItemId}/archive") @Operation(summary="Archive RAID item")
    public ApiResponse<RaidItemResponse> archive(@PathVariable UUID projectId, @PathVariable UUID raidItemId) {
        return ApiResponse.success(archive.execute(projectId, raidItemId));
    }
    @PostMapping("/{raidItemId}/convert-risk-to-issue") @Operation(summary="Convert risk to issue")
    public ApiResponse<RaidItemResponse> convert(@PathVariable UUID projectId, @PathVariable UUID raidItemId) {
        return ApiResponse.success(convert.execute(projectId, raidItemId));
    }
    @PostMapping("/{raidItemId}/create-change-request-draft") @Operation(summary="Create CR draft from RAID")
    public ApiResponse<Map<String, Object>> createCr(@PathVariable UUID projectId, @PathVariable UUID raidItemId) {
        return ApiResponse.success(createCr.execute(projectId, raidItemId));
    }
    @PostMapping("/{raidItemId}/actions") @Operation(summary="Create RAID action")
    public ApiResponse<RaidActionResponse> createAction(@PathVariable UUID projectId, @PathVariable UUID raidItemId, @RequestBody Map<String, Object> body) {
        return ApiResponse.success(createAction.execute(projectId, raidItemId, body));
    }
    @GetMapping("/{raidItemId}/actions") @Operation(summary="List RAID actions for item")
    public ApiResponse<List<RaidActionResponse>> listActions(@PathVariable UUID projectId, @PathVariable UUID raidItemId) {
        return ApiResponse.success(actionQuery.listByItem(projectId, raidItemId));
    }
}
""",
    )

    W(
        "src/main/java/com/company/scopery/modules/raid/raidaction/http/controller/RaidActionController.java",
        """
package com.company.scopery.modules.raid.raidaction.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.raid.raidaction.application.action.CompleteRaidActionAction;
import com.company.scopery.modules.raid.raidaction.application.response.RaidActionResponse;
import com.company.scopery.modules.raid.raidaction.application.service.RaidActionQueryService;
import com.company.scopery.modules.raid.shared.constant.RaidApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.Map; import java.util.UUID;
@RestController @RequestMapping(RaidApiPaths.ACTIONS) @Tag(name="RAID - Actions")
public class RaidActionController {
    private final RaidActionQueryService query; private final CompleteRaidActionAction complete;
    public RaidActionController(RaidActionQueryService query, CompleteRaidActionAction complete) {
        this.query=query; this.complete=complete;
    }
    @GetMapping("/{raidActionId}") @Operation(summary="Get RAID action")
    public ApiResponse<RaidActionResponse> get(@PathVariable UUID projectId, @PathVariable UUID raidActionId) {
        return ApiResponse.success(query.get(projectId, raidActionId));
    }
    @PostMapping("/{raidActionId}/complete") @Operation(summary="Complete RAID action")
    public ApiResponse<RaidActionResponse> complete(@PathVariable UUID projectId, @PathVariable UUID raidActionId,
                                                    @RequestBody(required=false) Map<String, Object> body) {
        return ApiResponse.success(complete.execute(projectId, raidActionId, body));
    }
}
""",
    )

    W(
        "src/main/java/com/company/scopery/modules/raid/decision/http/controller/DecisionController.java",
        """
package com.company.scopery.modules.raid.decision.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.raid.decision.application.action.*;
import com.company.scopery.modules.raid.decision.application.response.DecisionRecordResponse;
import com.company.scopery.modules.raid.decision.application.service.DecisionQueryService;
import com.company.scopery.modules.raid.shared.constant.RaidApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.Map; import java.util.UUID;
@RestController @RequestMapping(RaidApiPaths.DECISIONS) @Tag(name="RAID - Decisions")
public class DecisionController {
    private final CreateDecisionAction create; private final DecideDecisionAction decide;
    private final RejectDecisionAction reject; private final ArchiveDecisionAction archive;
    private final CreateDecisionOptionAction createOption; private final UpsertDecisionImpactAction upsertImpact;
    private final DecisionQueryService query;
    public DecisionController(CreateDecisionAction create, DecideDecisionAction decide, RejectDecisionAction reject,
                              ArchiveDecisionAction archive, CreateDecisionOptionAction createOption,
                              UpsertDecisionImpactAction upsertImpact, DecisionQueryService query) {
        this.create=create; this.decide=decide; this.reject=reject; this.archive=archive;
        this.createOption=createOption; this.upsertImpact=upsertImpact; this.query=query;
    }
    @PostMapping @Operation(summary="Create decision")
    public ApiResponse<DecisionRecordResponse> create(@PathVariable UUID projectId, @RequestBody Map<String, Object> body) {
        return ApiResponse.success(create.execute(projectId, body));
    }
    @GetMapping @Operation(summary="List decisions")
    public ApiResponse<List<DecisionRecordResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(query.list(projectId));
    }
    @GetMapping("/{decisionId}") @Operation(summary="Get decision")
    public ApiResponse<DecisionRecordResponse> get(@PathVariable UUID projectId, @PathVariable UUID decisionId) {
        return ApiResponse.success(query.get(projectId, decisionId));
    }
    @PostMapping("/{decisionId}/decide") @Operation(summary="Decide")
    public ApiResponse<DecisionRecordResponse> decide(@PathVariable UUID projectId, @PathVariable UUID decisionId, @RequestBody Map<String, Object> body) {
        return ApiResponse.success(decide.execute(projectId, decisionId, body));
    }
    @PostMapping("/{decisionId}/reject") @Operation(summary="Reject decision")
    public ApiResponse<DecisionRecordResponse> reject(@PathVariable UUID projectId, @PathVariable UUID decisionId, @RequestBody Map<String, Object> body) {
        return ApiResponse.success(reject.execute(projectId, decisionId, body));
    }
    @PatchMapping("/{decisionId}/archive") @Operation(summary="Archive decision")
    public ApiResponse<DecisionRecordResponse> archive(@PathVariable UUID projectId, @PathVariable UUID decisionId) {
        return ApiResponse.success(archive.execute(projectId, decisionId));
    }
    @PostMapping("/{decisionId}/options") @Operation(summary="Add decision option")
    public ApiResponse<Map<String, Object>> addOption(@PathVariable UUID projectId, @PathVariable UUID decisionId, @RequestBody Map<String, Object> body) {
        return ApiResponse.success(createOption.execute(projectId, decisionId, body));
    }
    @GetMapping("/{decisionId}/options") @Operation(summary="List decision options")
    public ApiResponse<List<Map<String, Object>>> options(@PathVariable UUID projectId, @PathVariable UUID decisionId) {
        return ApiResponse.success(query.listOptions(projectId, decisionId));
    }
    @GetMapping("/{decisionId}/impact") @Operation(summary="Get decision impact")
    public ApiResponse<Map<String, Object>> impact(@PathVariable UUID projectId, @PathVariable UUID decisionId) {
        return ApiResponse.success(query.getImpact(projectId, decisionId));
    }
    @PutMapping("/{decisionId}/impact") @Operation(summary="Upsert decision impact")
    public ApiResponse<Map<String, Object>> upsertImpact(@PathVariable UUID projectId, @PathVariable UUID decisionId, @RequestBody Map<String, Object> body) {
        return ApiResponse.success(upsertImpact.execute(projectId, decisionId, body));
    }
}
""",
    )

    W(
        "src/main/java/com/company/scopery/modules/raid/report/application/service/RaidReportQueryService.java",
        """
package com.company.scopery.modules.raid.report.application.service;
import com.company.scopery.modules.raid.decision.domain.model.DecisionRecordRepository;
import com.company.scopery.modules.raid.raidaction.domain.model.RaidActionRepository;
import com.company.scopery.modules.raid.raiditem.application.response.RaidItemResponse;
import com.company.scopery.modules.raid.raiditem.domain.enums.RaidItemType;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItem;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItemRepository;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service
public class RaidReportQueryService {
    private final RaidItemRepository items; private final RaidActionRepository actions;
    private final DecisionRecordRepository decisions; private final RaidAuthorizationService authorization;
    public RaidReportQueryService(RaidItemRepository items, RaidActionRepository actions,
                                  DecisionRecordRepository decisions, RaidAuthorizationService authorization) {
        this.items=items; this.actions=actions; this.decisions=decisions; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public Map<String, Object> summary(UUID projectId) {
        authorization.requireView(projectId);
        List<RaidItem> all = items.findByProjectId(projectId);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("total", all.size());
        Map<String, Long> byType = new LinkedHashMap<>();
        for (RaidItemType t : RaidItemType.values()) {
            byType.put(t.name(), all.stream().filter(i -> i.type() == t).count());
        }
        out.put("byType", byType);
        out.put("openCount", all.stream().filter(i -> "OPEN".equals(i.status().name()) || "ESCALATED".equals(i.status().name())).count());
        out.put("decisionCount", decisions.findByProjectId(projectId).size());
        return out;
    }
    @Transactional(readOnly=true)
    public List<RaidItemResponse> byType(UUID projectId, RaidItemType type) {
        authorization.requireView(projectId);
        return items.findByProjectIdAndType(projectId, type).stream().map(RaidItemResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public List<Map<String, Object>> actions(UUID projectId) {
        authorization.requireView(projectId);
        List<Map<String, Object>> rows = new ArrayList<>();
        for (RaidItem item : items.findByProjectId(projectId)) {
            actions.findByRaidItemId(item.id()).forEach(a -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("raidItemId", item.id()); m.put("raidItemTitle", item.title()); m.put("actionId", a.id());
                m.put("title", a.title()); m.put("status", a.status().name()); m.put("dueDate", a.dueDate());
                rows.add(m);
            });
        }
        return rows;
    }
    @Transactional(readOnly=true)
    public List<Map<String, Object>> decisionLog(UUID projectId) {
        authorization.requireDecisionView(projectId);
        return decisions.findByProjectId(projectId).stream().map(d -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", d.id()); m.put("title", d.title()); m.put("category", d.category().name());
            m.put("status", d.status().name()); m.put("outcome", d.outcome()); m.put("decidedAt", d.decidedAt());
            return m;
        }).toList();
    }
}
""",
    )

    W(
        "src/main/java/com/company/scopery/modules/raid/report/http/controller/RaidReportController.java",
        """
package com.company.scopery.modules.raid.report.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.raid.raiditem.application.response.RaidItemResponse;
import com.company.scopery.modules.raid.raiditem.domain.enums.RaidItemType;
import com.company.scopery.modules.raid.report.application.service.RaidReportQueryService;
import com.company.scopery.modules.raid.shared.constant.RaidApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.Map; import java.util.UUID;
@RestController @RequestMapping(RaidApiPaths.REPORTS) @Tag(name="RAID - Reports")
public class RaidReportController {
    private final RaidReportQueryService reports;
    public RaidReportController(RaidReportQueryService reports) { this.reports = reports; }
    @GetMapping("/raid-summary") @Operation(summary="RAID summary")
    public ApiResponse<Map<String, Object>> summary(@PathVariable UUID projectId) { return ApiResponse.success(reports.summary(projectId)); }
    @GetMapping("/risk-register") @Operation(summary="Risk register")
    public ApiResponse<List<RaidItemResponse>> risks(@PathVariable UUID projectId) { return ApiResponse.success(reports.byType(projectId, RaidItemType.RISK)); }
    @GetMapping("/issue-log") @Operation(summary="Issue log")
    public ApiResponse<List<RaidItemResponse>> issues(@PathVariable UUID projectId) { return ApiResponse.success(reports.byType(projectId, RaidItemType.ISSUE)); }
    @GetMapping("/assumption-log") @Operation(summary="Assumption log")
    public ApiResponse<List<RaidItemResponse>> assumptions(@PathVariable UUID projectId) { return ApiResponse.success(reports.byType(projectId, RaidItemType.ASSUMPTION)); }
    @GetMapping("/dependency-log") @Operation(summary="Dependency log")
    public ApiResponse<List<RaidItemResponse>> dependencies(@PathVariable UUID projectId) { return ApiResponse.success(reports.byType(projectId, RaidItemType.DEPENDENCY)); }
    @GetMapping("/raid-actions") @Operation(summary="RAID actions report")
    public ApiResponse<List<Map<String, Object>>> actions(@PathVariable UUID projectId) { return ApiResponse.success(reports.actions(projectId)); }
    @GetMapping("/decision-log") @Operation(summary="Decision log")
    public ApiResponse<List<Map<String, Object>>> decisions(@PathVariable UUID projectId) { return ApiResponse.success(reports.decisionLog(projectId)); }
}
""",
    )

    W(
        "src/main/java/com/company/scopery/modules/raid/shared/listeners/RaidEventDefinitionSeedInitializer.java",
        """
package com.company.scopery.modules.raid.shared.listeners;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDataClassification;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Component
@Order(31)
public class RaidEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {
    public static final String SOURCE_SYSTEM = "SCOPERY_RAID";
    public static final String OWNER_MODULE = "RAID";
    private final EventDefinitionRepository eventDefinitionRepository;
    public RaidEventDefinitionSeedInitializer(EventDefinitionRepository eventDefinitionRepository) {
        this.eventDefinitionRepository = eventDefinitionRepository;
    }
    @Override @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        for (SeedEvent seed : EVENTS) {
            EventDefinitionSeedSupport.findOrCreate(
                    eventDefinitionRepository, SOURCE_SYSTEM, seed.code(), seed.name(),
                    seed.description(), EventDataClassification.INTERNAL, OWNER_MODULE);
        }
    }
    private record SeedEvent(String code, String name, String description) {}
    static final List<SeedEvent> EVENTS = List.of(
            new SeedEvent("RAID_ITEM_CREATED", "RAID Item Created", "RAID item created"),
            new SeedEvent("RAID_ITEM_UPDATED", "RAID Item Updated", "RAID item updated"),
            new SeedEvent("RAID_ITEM_RESOLVED", "RAID Item Resolved", "RAID item resolved"),
            new SeedEvent("RAID_ITEM_ESCALATED", "RAID Item Escalated", "RAID item escalated"),
            new SeedEvent("RAID_RISK_CONVERTED_TO_ISSUE", "Risk Converted To Issue", "Risk converted to issue"),
            new SeedEvent("RAID_ACTION_CREATED", "RAID Action Created", "RAID action created"),
            new SeedEvent("RAID_ACTION_COMPLETED", "RAID Action Completed", "RAID action completed"),
            new SeedEvent("DECISION_CREATED", "Decision Created", "Decision created"),
            new SeedEvent("DECISION_DECIDED", "Decision Decided", "Decision decided"),
            new SeedEvent("DECISION_REJECTED", "Decision Rejected", "Decision rejected")
    );
}
""",
    )

    print("RAID app/HTTP generated")


if __name__ == "__main__":
    main()
