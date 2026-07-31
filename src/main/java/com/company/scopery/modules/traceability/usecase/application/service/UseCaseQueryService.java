package com.company.scopery.modules.traceability.usecase.application.service;

import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItemRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.usecase.application.response.SupportingFunctionResponse;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseAcceptanceCriterionResponse;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseBusinessRuleResponse;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseConditionResponse;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseDetailResponse;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseFlowResponse;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseFlowStepResponse;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseResponse;
import com.company.scopery.modules.traceability.usecase.domain.model.RequirementFunctionRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.RequirementUseCaseRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCase;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseAcceptanceCriterionRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseBusinessRuleRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseConditionRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseFlowRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseFlowStepRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseSupportingFunctionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UseCaseQueryService {

    private final UseCaseRepository useCaseRepo;
    private final UseCaseFlowRepository flowRepo;
    private final UseCaseFlowStepRepository stepRepo;
    private final UseCaseConditionRepository conditionRepo;
    private final UseCaseBusinessRuleRepository ruleRepo;
    private final UseCaseAcceptanceCriterionRepository criterionRepo;
    private final UseCaseSupportingFunctionRepository supFnRepo;
    private final RequirementFunctionRepository requirementFunctionRepo;
    private final RequirementUseCaseRepository requirementUseCaseRepo;
    private final FunctionalItemRepository functionalItems;
    private final TraceabilityAuthorizationService authorization;

    public UseCaseQueryService(
            UseCaseRepository useCaseRepo,
            UseCaseFlowRepository flowRepo,
            UseCaseFlowStepRepository stepRepo,
            UseCaseConditionRepository conditionRepo,
            UseCaseBusinessRuleRepository ruleRepo,
            UseCaseAcceptanceCriterionRepository criterionRepo,
            UseCaseSupportingFunctionRepository supFnRepo,
            RequirementFunctionRepository requirementFunctionRepo,
            RequirementUseCaseRepository requirementUseCaseRepo,
            FunctionalItemRepository functionalItems,
            TraceabilityAuthorizationService authorization) {
        this.useCaseRepo = useCaseRepo;
        this.flowRepo = flowRepo;
        this.stepRepo = stepRepo;
        this.conditionRepo = conditionRepo;
        this.ruleRepo = ruleRepo;
        this.criterionRepo = criterionRepo;
        this.supFnRepo = supFnRepo;
        this.requirementFunctionRepo = requirementFunctionRepo;
        this.requirementUseCaseRepo = requirementUseCaseRepo;
        this.functionalItems = functionalItems;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public UseCaseDetailResponse getDetail(UUID projectId, UUID useCaseId) {
        authorization.requireView(projectId);
        UseCase uc = useCaseRepo.findByIdAndProjectId(useCaseId, projectId)
                .orElseThrow(() -> TraceabilityExceptions.useCaseNotFound(useCaseId));

        String primaryFunctionName = functionalItems.findByIdAndProjectId(uc.primaryFunctionId(), projectId)
                .map(f -> f.title()).orElse("");

        List<UseCaseFlowResponse> flows = flowRepo.findByUseCaseIdOrderByDisplayOrder(useCaseId).stream()
                .map(flow -> {
                    List<UseCaseFlowStepResponse> steps = stepRepo
                            .findByFlowIdOrderByDisplayOrder(flow.id()).stream()
                            .map(UseCaseFlowStepResponse::from).toList();
                    return UseCaseFlowResponse.from(flow, steps);
                }).toList();

        List<UseCaseConditionResponse> conditions = conditionRepo
                .findByUseCaseIdOrderByConditionTypeAndDisplayOrder(useCaseId).stream()
                .map(UseCaseConditionResponse::from).toList();

        List<UseCaseBusinessRuleResponse> rules = ruleRepo
                .findByUseCaseIdOrderByDisplayOrder(useCaseId).stream()
                .map(UseCaseBusinessRuleResponse::from).toList();

        List<UseCaseAcceptanceCriterionResponse> criteria = criterionRepo
                .findByUseCaseIdOrderByDisplayOrder(useCaseId).stream()
                .map(UseCaseAcceptanceCriterionResponse::from).toList();

        List<SupportingFunctionResponse> supportingFunctions = supFnRepo
                .findFunctionIdsByUseCaseId(useCaseId).stream()
                .map(fnId -> functionalItems.findByIdAndProjectId(fnId, projectId)
                        .map(f -> new SupportingFunctionResponse(f.id(), f.title(), f.code()))
                        .orElse(null))
                .filter(r -> r != null)
                .toList();

        return new UseCaseDetailResponse(
                UseCaseResponse.from(uc, primaryFunctionName),
                flows, conditions, rules, criteria, supportingFunctions);
    }

    @Transactional(readOnly = true)
    public List<UseCaseResponse> listByProject(UUID projectId) {
        authorization.requireView(projectId);
        return useCaseRepo.findByProjectId(projectId).stream()
                .map(uc -> {
                    String fnName = functionalItems.findByIdAndProjectId(uc.primaryFunctionId(), projectId)
                            .map(f -> f.title()).orElse("");
                    return UseCaseResponse.from(uc, fnName);
                }).toList();
    }

    @Transactional(readOnly = true)
    public List<UseCaseResponse> listByFunction(UUID projectId, UUID functionId) {
        authorization.requireView(projectId);
        String fnName = functionalItems.findByIdAndProjectId(functionId, projectId)
                .orElseThrow(() -> TraceabilityExceptions.functionalItemNotFound(functionId))
                .title();

        List<UseCaseResponse> primary = useCaseRepo.findByPrimaryFunctionId(functionId).stream()
                .map(uc -> UseCaseResponse.from(uc, fnName))
                .toList();

        Set<UUID> primaryIds = primary.stream().map(UseCaseResponse::id).collect(Collectors.toSet());

        List<UseCaseResponse> supporting = supFnRepo.findUseCaseIdsByFunctionId(functionId).stream()
                .filter(id -> !primaryIds.contains(id))
                .map(id -> useCaseRepo.findByIdAndProjectId(id, projectId)
                        .map(uc -> {
                            String ucFnName = functionalItems.findByIdAndProjectId(uc.primaryFunctionId(), projectId)
                                    .map(f -> f.title()).orElse("");
                            return UseCaseResponse.from(uc, ucFnName);
                        })
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        List<UseCaseResponse> all = new ArrayList<>(primary);
        all.addAll(supporting);
        return all;
    }

    @Transactional(readOnly = true)
    public List<UUID> getLinkedRequirementIdsByUseCase(UUID projectId, UUID useCaseId) {
        authorization.requireView(projectId);
        useCaseRepo.findByIdAndProjectId(useCaseId, projectId)
                .orElseThrow(() -> TraceabilityExceptions.useCaseNotFound(useCaseId));
        return requirementUseCaseRepo.findRequirementIdsByUseCaseId(useCaseId);
    }

    @Transactional(readOnly = true)
    public List<UUID> getLinkedRequirementIdsByFunction(UUID projectId, UUID functionId) {
        authorization.requireView(projectId);
        functionalItems.findByIdAndProjectId(functionId, projectId)
                .orElseThrow(() -> TraceabilityExceptions.functionalItemNotFound(functionId));
        return requirementFunctionRepo.findRequirementIdsByFunctionId(functionId);
    }
}
