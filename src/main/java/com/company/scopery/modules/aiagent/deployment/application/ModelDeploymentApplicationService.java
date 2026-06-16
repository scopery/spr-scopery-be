package com.company.scopery.modules.aiagent.deployment.application;

import com.company.scopery.common.pagination.PageRequestUtils;
import com.company.scopery.modules.aiagent.aimodel.domain.AiModel;
import com.company.scopery.modules.aiagent.aimodel.domain.AiModelRepository;
import com.company.scopery.modules.aiagent.aimodel.domain.AiModelStatus;
import com.company.scopery.modules.aiagent.deployment.application.command.*;
import com.company.scopery.modules.aiagent.deployment.application.query.*;
import com.company.scopery.modules.aiagent.deployment.application.response.*;
import com.company.scopery.modules.aiagent.deployment.domain.*;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentSortFields;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ModelDeploymentApplicationService {

    private final ModelDeploymentRepository deploymentRepository;
    private final AiModelRepository aiModelRepository;
    private final AiAgentActivityLogger activityLogger;

    public ModelDeploymentApplicationService(ModelDeploymentRepository deploymentRepository,
                                              AiModelRepository aiModelRepository,
                                              AiAgentActivityLogger activityLogger) {
        this.deploymentRepository = deploymentRepository;
        this.aiModelRepository = aiModelRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ModelDeploymentResponse createModelDeployment(CreateModelDeploymentCommand command) {
        AiModel model = findModelOrThrow(command.modelId());

        if (model.status() != AiModelStatus.ACTIVE) {
            throw AiAgentExceptions.modelDeploymentModelNotActive(model.code().value());
        }

        ModelDeploymentEnvironment environment = AiAgentEnumParser.parseRequired(
                ModelDeploymentEnvironment.class, command.environment(),
                AiAgentErrorCatalog.INVALID_MODEL_DEPLOYMENT_ENVIRONMENT.code(), "environment");
        ModelDeploymentCode code = ModelDeploymentCode.of(command.code());

        if (deploymentRepository.existsByModelIdAndCode(command.modelId(), code)) {
            throw AiAgentExceptions.modelDeploymentCodeAlreadyExists(code.value());
        }

        if (command.isDefault()) {
            deploymentRepository.clearDefaultFlags(command.modelId(), environment, null);
        }

        ModelDeployment deployment = ModelDeployment.create(
                command.modelId(), command.name(), code, environment,
                command.providerDeploymentId(), command.endpointUrl(),
                command.defaultTemperature(), command.defaultMaxOutputTokens(),
                command.isDefault(), command.description());

        ModelDeployment saved = deploymentRepository.save(deployment);

        activityLogger.logSuccess(AiAgentEntityTypes.MODEL_DEPLOYMENT, saved.id(),
                AiAgentActivityActions.CREATE_MODEL_DEPLOYMENT,
                "Model deployment created: " + saved.code().value());

        return ModelDeploymentResponse.from(saved);
    }

    @Transactional
    public ModelDeploymentDetailResponse updateModelDeployment(UpdateModelDeploymentCommand command) {
        ModelDeployment deployment = findOrThrow(command.id());

        deployment.update(command.name(), command.providerDeploymentId(), command.endpointUrl(),
                command.defaultTemperature(), command.defaultMaxOutputTokens(),
                command.isDefault(), command.description());

        if (command.isDefault()) {
            deploymentRepository.clearDefaultFlags(deployment.modelId(), deployment.environment(), deployment.id());
        }

        ModelDeployment saved = deploymentRepository.save(deployment);

        activityLogger.logSuccess(AiAgentEntityTypes.MODEL_DEPLOYMENT, saved.id(),
                AiAgentActivityActions.UPDATE_MODEL_DEPLOYMENT,
                "Model deployment updated: " + saved.code().value());

        String modelName = loadModelName(saved.modelId());
        return ModelDeploymentDetailResponse.from(saved, modelName);
    }

    @Transactional(readOnly = true)
    public ModelDeploymentDetailResponse getModelDeploymentDetail(GetModelDeploymentDetailQuery query) {
        ModelDeployment deployment = findOrThrow(query.id());
        String modelName = loadModelName(deployment.modelId());
        return ModelDeploymentDetailResponse.from(deployment, modelName);
    }

    @Transactional(readOnly = true)
    public Page<ModelDeploymentResponse> searchModelDeployments(SearchModelDeploymentQuery query) {
        ModelDeploymentEnvironment environment = AiAgentEnumParser.parseOptional(
                ModelDeploymentEnvironment.class, query.environment(),
                AiAgentErrorCatalog.INVALID_MODEL_DEPLOYMENT_ENVIRONMENT.code(), "environment");
        ModelDeploymentStatus status = AiAgentEnumParser.parseOptional(
                ModelDeploymentStatus.class, query.status(),
                AiAgentErrorCatalog.INVALID_MODEL_DEPLOYMENT_STATUS.code(), "status");
        var pageable = PageRequestUtils.of(query.page(), query.size(),
                Sort.by(Sort.Direction.DESC, AiAgentSortFields.CREATED_AT));
        return deploymentRepository
                .findAll(query.modelId(), environment, query.keyword(), status, query.isDefault(), pageable)
                .map(ModelDeploymentResponse::from);
    }

    @Transactional
    public ModelDeploymentDetailResponse activateModelDeployment(ActivateModelDeploymentCommand command) {
        ModelDeployment deployment = findOrThrow(command.id());

        if (deployment.status() == ModelDeploymentStatus.DEPRECATED) {
            throw AiAgentExceptions.deprecatedModelDeploymentCannotBeActivated(deployment.code().value());
        }

        AiModel model = findModelOrThrow(deployment.modelId());
        if (model.status() != AiModelStatus.ACTIVE) {
            throw AiAgentExceptions.modelDeploymentModelNotActive(model.code().value());
        }

        deployment.activate();
        ModelDeployment saved = deploymentRepository.save(deployment);

        activityLogger.logSuccess(AiAgentEntityTypes.MODEL_DEPLOYMENT, saved.id(),
                AiAgentActivityActions.ACTIVATE_MODEL_DEPLOYMENT,
                "Model deployment activated: " + saved.code().value());

        return ModelDeploymentDetailResponse.from(saved, model.name());
    }

    @Transactional
    public ModelDeploymentDetailResponse deactivateModelDeployment(DeactivateModelDeploymentCommand command) {
        ModelDeployment deployment = findOrThrow(command.id());
        deployment.deactivate();
        ModelDeployment saved = deploymentRepository.save(deployment);

        activityLogger.logSuccess(AiAgentEntityTypes.MODEL_DEPLOYMENT, saved.id(),
                AiAgentActivityActions.DEACTIVATE_MODEL_DEPLOYMENT,
                "Model deployment deactivated: " + saved.code().value());

        String modelName = loadModelName(saved.modelId());
        return ModelDeploymentDetailResponse.from(saved, modelName);
    }

    @Transactional
    public ModelDeploymentDetailResponse setDefaultModelDeployment(SetDefaultModelDeploymentCommand command) {
        ModelDeployment deployment = findOrThrow(command.id());

        if (deployment.status() != ModelDeploymentStatus.ACTIVE) {
            throw AiAgentExceptions.modelDeploymentNotActive(deployment.code().value());
        }

        AiModel model = findModelOrThrow(deployment.modelId());
        if (model.status() != AiModelStatus.ACTIVE) {
            throw AiAgentExceptions.modelDeploymentModelNotActive(model.code().value());
        }

        deploymentRepository.clearDefaultFlags(deployment.modelId(), deployment.environment(), deployment.id());
        deployment.setDefault();
        ModelDeployment saved = deploymentRepository.save(deployment);

        activityLogger.logSuccess(AiAgentEntityTypes.MODEL_DEPLOYMENT, saved.id(),
                AiAgentActivityActions.SET_DEFAULT_MODEL_DEPLOYMENT,
                "Model deployment set as default: " + saved.code().value());

        return ModelDeploymentDetailResponse.from(saved, model.name());
    }

    private ModelDeployment findOrThrow(UUID id) {
        return deploymentRepository.findById(id)
                .orElseThrow(() -> AiAgentExceptions.modelDeploymentNotFound(id));
    }

    private AiModel findModelOrThrow(UUID modelId) {
        return aiModelRepository.findById(modelId)
                .orElseThrow(() -> AiAgentExceptions.modelDeploymentModelNotFound(modelId));
    }

    private String loadModelName(UUID modelId) {
        return aiModelRepository.findById(modelId)
                .map(AiModel::name)
                .orElse(null);
    }
}
