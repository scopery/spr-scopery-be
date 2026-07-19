package com.company.scopery.modules.scope.evidence.application.action;
import com.company.scopery.modules.scope.criteria.domain.model.AcceptanceCriteriaRepository;
import com.company.scopery.modules.scope.deliverable.domain.model.DeliverableRepository;
import com.company.scopery.modules.scope.evidence.application.command.CreateAcceptanceEvidenceCommand;
import com.company.scopery.modules.scope.evidence.application.response.AcceptanceEvidenceResponse;
import com.company.scopery.modules.scope.evidence.domain.enums.EvidenceType;
import com.company.scopery.modules.scope.evidence.domain.model.AcceptanceEvidence;
import com.company.scopery.modules.scope.evidence.domain.model.AcceptanceEvidenceRepository;
import com.company.scopery.modules.scope.shared.activity.ScopeActivityLogger;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.constant.ScopeActivityActions;
import com.company.scopery.modules.scope.shared.constant.ScopeEntityTypes;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import com.company.scopery.modules.scope.shared.util.ScopeEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateAcceptanceEvidenceAction {
    private final DeliverableRepository deliverables;
    private final AcceptanceCriteriaRepository criteria;
    private final AcceptanceEvidenceRepository evidence;
    private final ScopeAuthorizationService authorization;
    private final ScopeActivityLogger activityLogger;
    public CreateAcceptanceEvidenceAction(DeliverableRepository deliverables, AcceptanceCriteriaRepository criteria,
                                          AcceptanceEvidenceRepository evidence, ScopeAuthorizationService authorization,
                                          ScopeActivityLogger activityLogger) {
        this.deliverables = deliverables; this.criteria = criteria; this.evidence = evidence;
        this.authorization = authorization; this.activityLogger = activityLogger;
    }
    @Transactional
    public AcceptanceEvidenceResponse execute(CreateAcceptanceEvidenceCommand command) {
        authorization.requireDeliverableUpdate(command.projectId());
        deliverables.findByIdAndProjectId(command.deliverableId(), command.projectId())
                .orElseThrow(() -> ScopeExceptions.deliverableNotFound(command.deliverableId()));
        if (command.acceptanceCriteriaId() != null) {
            criteria.findById(command.acceptanceCriteriaId())
                    .filter(c -> c.deliverableId().equals(command.deliverableId()))
                    .orElseThrow(() -> ScopeExceptions.criteriaNotFound(command.acceptanceCriteriaId()));
        }
        if (command.title() == null || command.title().isBlank()) {
            throw ScopeExceptions.criteriaTitleRequired();
        }
        EvidenceType type = ScopeEnumParser.parseRequired(EvidenceType.class, command.evidenceType(), "evidenceType");
        AcceptanceEvidence saved = evidence.save(AcceptanceEvidence.create(
                command.deliverableId(), command.acceptanceCriteriaId(), command.projectId(), type,
                command.title().trim(), command.contentText(), command.linkUrl(), command.referenceId()));
        activityLogger.logSuccess(ScopeEntityTypes.EVIDENCE, saved.id(), ScopeActivityActions.EVIDENCE_CREATED, "Evidence created");
        return AcceptanceEvidenceResponse.from(saved);
    }
}
