package com.company.scopery.modules.traceability.usecase.application.action;

import com.company.scopery.modules.traceability.functionapi.domain.model.FunctionApi;
import com.company.scopery.modules.traceability.functionapi.domain.model.FunctionApiRepository;
import com.company.scopery.modules.traceability.functioncomm.domain.model.FunctionCommunication;
import com.company.scopery.modules.traceability.functioncomm.domain.model.FunctionCommunicationRepository;
import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItemRepository;
import com.company.scopery.modules.traceability.functionscreen.domain.model.FunctionScreen;
import com.company.scopery.modules.traceability.functionscreen.domain.model.FunctionScreenRepository;
import com.company.scopery.modules.traceability.screencomponent.domain.model.ScreenComponent;
import com.company.scopery.modules.traceability.screencomponent.domain.model.ScreenComponentRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.shared.util.TraceabilityEnumParser;
import com.company.scopery.modules.traceability.usecase.application.command.UpdateUseCaseCommand;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseResponse;
import com.company.scopery.modules.traceability.usecase.application.service.UseCaseFlowScopeQueryService;
import com.company.scopery.modules.traceability.usecase.domain.enums.UseCaseStatus;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCase;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseSupportingFunctionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UpdateUseCaseAction {

    private final UseCaseRepository useCaseRepo;
    private final FunctionalItemRepository functionalItems;
    private final UseCaseSupportingFunctionRepository supFnRepo;
    private final FunctionScreenRepository functionScreenRepo;
    private final FunctionApiRepository functionApiRepo;
    private final FunctionCommunicationRepository functionCommRepo;
    private final ScreenComponentRepository screenComponentRepo;
    private final UseCaseFlowScopeQueryService flowScopeQuery;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public UpdateUseCaseAction(UseCaseRepository useCaseRepo,
                               FunctionalItemRepository functionalItems,
                               UseCaseSupportingFunctionRepository supFnRepo,
                               FunctionScreenRepository functionScreenRepo,
                               FunctionApiRepository functionApiRepo,
                               FunctionCommunicationRepository functionCommRepo,
                               ScreenComponentRepository screenComponentRepo,
                               UseCaseFlowScopeQueryService flowScopeQuery,
                               TraceabilityAuthorizationService authorization,
                               TraceabilityActivityLogger activityLogger) {
        this.useCaseRepo = useCaseRepo;
        this.functionalItems = functionalItems;
        this.supFnRepo = supFnRepo;
        this.functionScreenRepo = functionScreenRepo;
        this.functionApiRepo = functionApiRepo;
        this.functionCommRepo = functionCommRepo;
        this.screenComponentRepo = screenComponentRepo;
        this.flowScopeQuery = flowScopeQuery;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public UseCaseResponse execute(UpdateUseCaseCommand c) {
        authorization.requireCreate(c.projectId());

        var uc = useCaseRepo.findByIdAndProjectId(c.useCaseId(), c.projectId())
                .orElseThrow(() -> TraceabilityExceptions.useCaseNotFound(c.useCaseId()));

        UseCaseStatus status = TraceabilityEnumParser.parseRequired(UseCaseStatus.class, c.status(), "status");

        UseCase updated = uc.withUpdated(c.name(), c.goal(), c.primaryActorName(), c.triggerText(), status);

        if (c.primaryFunctionId() != null) {
            var newFn = functionalItems.findByIdAndProjectId(c.primaryFunctionId(), c.projectId())
                    .orElseThrow(() -> TraceabilityExceptions.functionalItemNotFound(c.primaryFunctionId()));

            UUID previousPrimary = uc.primaryFunctionId();
            boolean primaryChanged = previousPrimary == null || !previousPrimary.equals(c.primaryFunctionId());
            if (previousPrimary != null
                    && !previousPrimary.equals(c.primaryFunctionId())
                    && !supFnRepo.exists(c.useCaseId(), previousPrimary)) {
                // Keep former primary as supporting so the link is not lost.
                supFnRepo.link(c.useCaseId(), previousPrimary);
            }

            updated = updated.withPrimaryFunctionId(c.primaryFunctionId());

            // New primary should not remain only as a supporting link.
            if (supFnRepo.exists(c.useCaseId(), c.primaryFunctionId())) {
                supFnRepo.unlink(c.useCaseId(), c.primaryFunctionId());
            }

            if (primaryChanged) {
                Set<UUID> allowedScreens = functionScreenRepo.findByFunctionId(newFn.id()).stream()
                        .map(FunctionScreen::screenId).collect(Collectors.toSet());
                Set<UUID> allowedApis = functionApiRepo.findByFunctionId(newFn.id()).stream()
                        .map(FunctionApi::apiEndpointId).collect(Collectors.toSet());
                Set<UUID> allowedEntities = flowScopeQuery.allowedEntityIds(newFn);
                Set<UUID> allowedCommunications = functionCommRepo.findByFunctionId(newFn.id()).stream()
                        .map(FunctionCommunication::communicationId).collect(Collectors.toSet());
                Set<UUID> allowedComponents = screenComponentRepo.findByScreenIdIn(allowedScreens).stream()
                        .map(ScreenComponent::componentId).collect(Collectors.toSet());
                flowScopeQuery.markOutOfScopeMentionsOnUseCase(
                        c.useCaseId(), allowedScreens, allowedComponents, allowedApis,
                        allowedEntities, allowedCommunications);
            }
        }

        var saved = useCaseRepo.save(updated);

        var fn = functionalItems.findByIdAndProjectId(saved.primaryFunctionId(), c.projectId());

        activityLogger.logSuccess(TraceabilityEntityTypes.USE_CASE, saved.id(),
                TraceabilityActivityActions.USE_CASE_UPDATED, "Use case updated: " + saved.key());

        return UseCaseResponse.from(saved, fn.map(f -> f.title()).orElse(""));
    }
}
