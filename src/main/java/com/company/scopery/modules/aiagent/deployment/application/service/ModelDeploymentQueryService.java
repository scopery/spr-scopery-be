package com.company.scopery.modules.aiagent.deployment.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModel;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModelRepository;
import com.company.scopery.modules.aiagent.deployment.application.query.GetModelDeploymentDetailQuery;
import com.company.scopery.modules.aiagent.deployment.application.query.SearchModelDeploymentQuery;
import com.company.scopery.modules.aiagent.deployment.application.response.ModelDeploymentDetailResponse;
import com.company.scopery.modules.aiagent.deployment.application.response.ModelDeploymentResponse;
import com.company.scopery.modules.aiagent.deployment.domain.enums.ModelDeploymentEnvironment;
import com.company.scopery.modules.aiagent.deployment.domain.enums.ModelDeploymentStatus;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeployment;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentSortFields;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ModelDeploymentQueryService {

    private final ModelDeploymentRepository deploymentRepository;
    private final AiModelRepository aiModelRepository;

    public ModelDeploymentQueryService(ModelDeploymentRepository deploymentRepository,
                                       AiModelRepository aiModelRepository) {
        this.deploymentRepository = deploymentRepository;
        this.aiModelRepository = aiModelRepository;
    }

    @Transactional(readOnly = true)
    public ModelDeploymentDetailResponse getModelDeploymentDetail(GetModelDeploymentDetailQuery query) {
        ModelDeployment deployment = findOrThrow(query.id());
        String modelName = loadModelName(deployment.modelId());
        return ModelDeploymentDetailResponse.from(deployment, modelName);
    }

    @Transactional(readOnly = true)
    public PageResult<ModelDeploymentResponse> searchModelDeployments(SearchModelDeploymentQuery query) {
        ModelDeploymentEnvironment environment = AiAgentEnumParser.parseOptional(
                ModelDeploymentEnvironment.class, query.environment(),
                AiAgentErrorCatalog.INVALID_MODEL_DEPLOYMENT_ENVIRONMENT.code(), "environment");
        ModelDeploymentStatus status = AiAgentEnumParser.parseOptional(
                ModelDeploymentStatus.class, query.status(),
                AiAgentErrorCatalog.INVALID_MODEL_DEPLOYMENT_STATUS.code(), "status");
        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), AiAgentSortFields.CREATED_AT, false);
        return deploymentRepository
                .findAll(query.modelId(), environment, query.keyword(), status, query.isDefault(), pageQuery)
                .map(ModelDeploymentResponse::from);
    }

    private ModelDeployment findOrThrow(UUID id) {
        return deploymentRepository.findById(id)
                .orElseThrow(() -> AiAgentExceptions.modelDeploymentNotFound(id));
    }

    private String loadModelName(UUID modelId) {
        return aiModelRepository.findById(modelId)
                .map(AiModel::name)
                .orElse(null);
    }
}
