package com.company.scopery.modules.project.templatephase.application.action;

import com.company.scopery.modules.project.phasedefinition.domain.enums.PhaseDefinitionStatus;
import com.company.scopery.modules.project.phasedefinition.domain.model.PhaseDefinition;
import com.company.scopery.modules.project.phasedefinition.domain.model.PhaseDefinitionRepository;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.TemplateAccessSupport;
import com.company.scopery.modules.project.shared.support.TemplateVersionMutationGuard;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import com.company.scopery.modules.project.templatephase.application.command.UpdateProjectTemplatePhaseCommand;
import com.company.scopery.modules.project.templatephase.application.response.ProjectTemplatePhaseResponse;
import com.company.scopery.modules.project.templatephase.domain.model.ProjectTemplatePhase;
import com.company.scopery.modules.project.templatephase.domain.model.ProjectTemplatePhaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class UpdateProjectTemplatePhaseAction {

    private final ProjectTemplateRepository templateRepository;
    private final ProjectTemplatePhaseRepository phaseRepository;
    private final PhaseDefinitionRepository phaseDefinitionRepository;
    private final TemplateVersionMutationGuard mutationGuard;
    private final TemplateAccessSupport authorizationSupport;
    private final ProjectActivityLogger activityLogger;

    public UpdateProjectTemplatePhaseAction(ProjectTemplateRepository templateRepository,
                                            ProjectTemplatePhaseRepository phaseRepository,
                                            PhaseDefinitionRepository phaseDefinitionRepository,
                                            TemplateVersionMutationGuard mutationGuard,
                                            TemplateAccessSupport authorizationSupport,
                                            ProjectActivityLogger activityLogger) {
        this.templateRepository = templateRepository;
        this.phaseRepository = phaseRepository;
        this.phaseDefinitionRepository = phaseDefinitionRepository;
        this.mutationGuard = mutationGuard;
        this.authorizationSupport = authorizationSupport;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ProjectTemplatePhaseResponse execute(UpdateProjectTemplatePhaseCommand cmd) {
        ProjectTemplate template = templateRepository.findById(cmd.templateId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(cmd.templateId()));
        authorizationSupport.requireUpdate(template);
        mutationGuard.requireDraftVersion(cmd.templateId(), cmd.versionId());

        ProjectTemplatePhase phase = phaseRepository.findById(cmd.templatePhaseId())
                .orElseThrow(() -> ProjectExceptions.projectTemplatePhaseNotFound(cmd.templatePhaseId()));
        if (!phase.templateVersionId().equals(cmd.versionId())) {
            throw ProjectExceptions.projectTemplatePhasePathMismatch(phase.id(), cmd.versionId());
        }

        UUID phaseDefinitionId = cmd.phaseDefinitionId() != null ? cmd.phaseDefinitionId() : phase.phaseDefinitionId();
        if (cmd.phaseDefinitionId() != null) {
            PhaseDefinition def = phaseDefinitionRepository.findById(cmd.phaseDefinitionId())
                    .orElseThrow(() -> ProjectExceptions.phaseDefinitionNotFound(cmd.phaseDefinitionId()));
            if (def.status() != PhaseDefinitionStatus.ACTIVE) {
                throw ProjectExceptions.phaseDefinitionNotActive(def.id());
            }
        }

        if (cmd.displayOrder() != phase.displayOrder()
                && phaseRepository.existsByTemplateVersionIdAndDisplayOrder(cmd.versionId(), cmd.displayOrder())) {
            throw ProjectExceptions.projectTemplateVersionStructureInvalid(
                    "Display order already used: " + cmd.displayOrder());
        }

        ProjectTemplatePhase saved = phaseRepository.save(phase.update(
                cmd.code(),
                cmd.name(),
                cmd.description(),
                cmd.displayOrder(),
                cmd.defaultDurationDays(),
                cmd.startOffsetDays(),
                cmd.deliverableDocumentTypeId(),
                phaseDefinitionId
        ));

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT_TEMPLATE_PHASE,
                saved.id(),
                ProjectActivityActions.UPDATE_PROJECT_TEMPLATE_PHASE,
                "Template phase updated: " + saved.name()
        );

        return ProjectTemplatePhaseResponse.from(saved);
    }
}
