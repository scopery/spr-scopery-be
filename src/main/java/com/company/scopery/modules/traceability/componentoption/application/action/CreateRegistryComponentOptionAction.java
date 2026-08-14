package com.company.scopery.modules.traceability.componentoption.application.action;

import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponent;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponentRepository;
import com.company.scopery.modules.traceability.componentoption.application.command.CreateRegistryComponentOptionCommand;
import com.company.scopery.modules.traceability.componentoption.application.response.RegistryComponentOptionResponse;
import com.company.scopery.modules.traceability.componentoption.domain.model.RegistryComponentOption;
import com.company.scopery.modules.traceability.componentoption.domain.model.RegistryComponentOptionRepository;
import com.company.scopery.modules.traceability.componentoption.infrastructure.persistence.SpringDataRegistryComponentOptionJpaRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateRegistryComponentOptionAction {

    private final RegistryComponentOptionRepository repo;
    private final RegistryAppComponentRepository appComponentRepo;
    private final SpringDataRegistryComponentOptionJpaRepository springData;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public CreateRegistryComponentOptionAction(RegistryComponentOptionRepository repo,
                                                RegistryAppComponentRepository appComponentRepo,
                                                SpringDataRegistryComponentOptionJpaRepository springData,
                                                TraceabilityAuthorizationService authorization,
                                                TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.appComponentRepo = appComponentRepo;
        this.springData = springData;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RegistryComponentOptionResponse execute(CreateRegistryComponentOptionCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());

        RegistryAppComponent component = appComponentRepo.findByIdAndWorkspaceId(c.componentId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.appComponentNotFound(c.componentId()));

        if (!"STATIC".equals(component.optionSourceType())) {
            throw TraceabilityExceptions.componentSourceTypeNotStatic(c.componentId());
        }

        if (springData.existsByComponentIdAndOptionValue(c.componentId(), c.optionValue())) {
            throw TraceabilityExceptions.componentOptionValueExists(c.optionValue());
        }

        RegistryComponentOption domain = RegistryComponentOption.create(
                c.componentId(), c.workspaceId(), c.optionValue(), c.optionLabel(), c.displayOrder());
        RegistryComponentOption saved = repo.save(domain);

        activityLogger.logSuccess(TraceabilityEntityTypes.COMPONENT_OPTION, saved.id(),
                TraceabilityActivityActions.COMPONENT_OPTION_CREATED,
                "Component option created: " + saved.optionValue());
        return RegistryComponentOptionResponse.from(saved);
    }
}
