package com.company.scopery.modules.project.templatephase.application.action;

import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.TemplateAccessSupport;
import com.company.scopery.modules.project.shared.support.TemplateVersionMutationGuard;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import com.company.scopery.modules.project.templatephase.application.command.ReorderProjectTemplatePhasesCommand;
import com.company.scopery.modules.project.templatephase.application.response.ProjectTemplatePhaseResponse;
import com.company.scopery.modules.project.templatephase.domain.model.ProjectTemplatePhase;
import com.company.scopery.modules.project.templatephase.domain.model.ProjectTemplatePhaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class ReorderProjectTemplatePhasesAction {

    private final ProjectTemplateRepository templateRepository;
    private final ProjectTemplatePhaseRepository phaseRepository;
    private final TemplateVersionMutationGuard mutationGuard;
    private final TemplateAccessSupport authorizationSupport;
    private final ProjectActivityLogger activityLogger;

    public ReorderProjectTemplatePhasesAction(ProjectTemplateRepository templateRepository,
                                              ProjectTemplatePhaseRepository phaseRepository,
                                              TemplateVersionMutationGuard mutationGuard,
                                              TemplateAccessSupport authorizationSupport,
                                              ProjectActivityLogger activityLogger) {
        this.templateRepository = templateRepository;
        this.phaseRepository = phaseRepository;
        this.mutationGuard = mutationGuard;
        this.authorizationSupport = authorizationSupport;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public List<ProjectTemplatePhaseResponse> execute(ReorderProjectTemplatePhasesCommand cmd) {
        ProjectTemplate template = templateRepository.findById(cmd.templateId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(cmd.templateId()));
        authorizationSupport.requireUpdate(template);
        mutationGuard.requireDraftVersion(cmd.templateId(), cmd.versionId());

        List<ProjectTemplatePhase> existing = phaseRepository.findByTemplateVersionId(cmd.versionId());
        Map<UUID, ProjectTemplatePhase> byId = new HashMap<>();
        for (ProjectTemplatePhase phase : existing) {
            byId.put(phase.id(), phase);
        }

        if (cmd.orderedPhaseIds() == null || cmd.orderedPhaseIds().size() != existing.size()
                || !byId.keySet().containsAll(cmd.orderedPhaseIds())) {
            throw ProjectExceptions.projectTemplateVersionStructureInvalid(
                    "Reorder list must contain exactly all phase ids for the version");
        }

        // Two-pass to avoid unique display_order conflicts
        int offset = existing.size() + 1000;
        List<ProjectTemplatePhase> temp = new ArrayList<>();
        for (int i = 0; i < cmd.orderedPhaseIds().size(); i++) {
            ProjectTemplatePhase phase = byId.get(cmd.orderedPhaseIds().get(i));
            temp.add(phaseRepository.save(phase.withDisplayOrder(offset + i)));
        }

        List<ProjectTemplatePhaseResponse> result = new ArrayList<>();
        for (int i = 0; i < cmd.orderedPhaseIds().size(); i++) {
            ProjectTemplatePhase phase = byId.get(cmd.orderedPhaseIds().get(i));
            // reload from temp list after first pass
            ProjectTemplatePhase current = phaseRepository.findById(phase.id()).orElse(phase);
            ProjectTemplatePhase saved = phaseRepository.save(current.withDisplayOrder(i + 1));
            result.add(ProjectTemplatePhaseResponse.from(saved));
        }

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT_TEMPLATE_PHASE,
                cmd.versionId(),
                ProjectActivityActions.REORDER_PROJECT_TEMPLATE_PHASES,
                "Template phases reordered"
        );

        return result;
    }
}
