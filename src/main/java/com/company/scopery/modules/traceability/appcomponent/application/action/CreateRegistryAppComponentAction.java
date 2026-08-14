package com.company.scopery.modules.traceability.appcomponent.application.action;

import com.company.scopery.modules.traceability.appcomponent.application.command.CreateRegistryAppComponentCommand;
import com.company.scopery.modules.traceability.appcomponent.application.response.RegistryAppComponentResponse;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponent;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponentRepository;
import com.company.scopery.modules.traceability.dataentity.domain.model.RegistryDataEntity;
import com.company.scopery.modules.traceability.dataentity.domain.model.RegistryDataEntityRepository;
import com.company.scopery.modules.traceability.dataentityfield.domain.model.RegistryDataEntityField;
import com.company.scopery.modules.traceability.dataentityfield.domain.model.RegistryDataEntityFieldRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CreateRegistryAppComponentAction {

    private static final Set<String> VALID_FILTER_OPS = Set.of("IS_NULL", "EQUALS", "IN");

    private final RegistryAppComponentRepository repo;
    private final RegistryDataEntityRepository dataEntityRepo;
    private final RegistryDataEntityFieldRepository dataEntityFieldRepo;
    private final TraceabilityAuthorizationService authorization;
    private final ObjectMapper objectMapper;

    public CreateRegistryAppComponentAction(RegistryAppComponentRepository repo,
                                            RegistryDataEntityRepository dataEntityRepo,
                                            RegistryDataEntityFieldRepository dataEntityFieldRepo,
                                            TraceabilityAuthorizationService authorization,
                                            ObjectMapper objectMapper) {
        this.repo = repo;
        this.dataEntityRepo = dataEntityRepo;
        this.dataEntityFieldRepo = dataEntityFieldRepo;
        this.authorization = authorization;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public RegistryAppComponentResponse execute(CreateRegistryAppComponentCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());

        String sourceType = c.optionSourceType() != null ? c.optionSourceType().trim().toUpperCase() : "NONE";

        if ("DYNAMIC".equals(sourceType)) {
            validateDynamicSource(c.workspaceId(), c.sourceEntityId(),
                    c.sourceValueColumn(), c.sourceLabelColumn(), c.sourceFilterJson());
        }

        return RegistryAppComponentResponse.from(repo.save(
                RegistryAppComponent.create(c.applicationId(), c.workspaceId(), c.code().trim(), c.name().trim(),
                        c.description(), c.componentType(), sourceType,
                        c.sourceEntityId(), c.sourceValueColumn(), c.sourceLabelColumn(), c.sourceFilterJson())));
    }

    private void validateDynamicSource(UUID workspaceId, UUID sourceEntityId,
                                        String sourceValueColumn, String sourceLabelColumn, String sourceFilterJson) {
        if (sourceEntityId == null) {
            throw TraceabilityExceptions.filterFieldNotInEntity("sourceEntityId is required for DYNAMIC source");
        }
        RegistryDataEntity entity = dataEntityRepo.findByIdAndWorkspaceId(sourceEntityId, workspaceId)
                .orElseThrow(() -> TraceabilityExceptions.dataEntityNotFound(sourceEntityId));
        if (!entity.status().name().equals("ACTIVE")) {
            throw TraceabilityExceptions.dataEntityNotActive(sourceEntityId);
        }

        List<RegistryDataEntityField> fields = dataEntityFieldRepo.findByEntityId(sourceEntityId);
        Set<String> columns = fields.stream().map(RegistryDataEntityField::columnName).collect(Collectors.toSet());

        if (sourceValueColumn == null || !columns.contains(sourceValueColumn)) {
            throw TraceabilityExceptions.dataEntityFieldColumnNotFound(sourceValueColumn != null ? sourceValueColumn : "null");
        }
        if (sourceLabelColumn == null || !columns.contains(sourceLabelColumn)) {
            throw TraceabilityExceptions.dataEntityFieldColumnNotFound(sourceLabelColumn != null ? sourceLabelColumn : "null");
        }

        if (sourceFilterJson != null && !sourceFilterJson.isBlank()) {
            validateSourceFilterJson(sourceFilterJson, columns);
        }
    }

    private void validateSourceFilterJson(String sourceFilterJson, Set<String> entityColumns) {
        try {
            JsonNode root = objectMapper.readTree(sourceFilterJson);
            if (!root.isArray()) {
                throw TraceabilityExceptions.filterFieldNotInEntity("source_filter_json must be a JSON array");
            }
            for (JsonNode filter : root) {
                String op = filter.path("op").asText(null);
                if (op == null || !VALID_FILTER_OPS.contains(op)) {
                    throw TraceabilityExceptions.filterFieldNotInEntity("Invalid op: " + op + ". Must be one of: IS_NULL, EQUALS, IN");
                }
                String field = filter.path("field").asText(null);
                if (field == null) {
                    throw TraceabilityExceptions.filterFieldNotInEntity("Filter must have 'field' key");
                }
                if (!entityColumns.contains(field)) {
                    throw TraceabilityExceptions.filterFieldNotInEntity(field);
                }
            }
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw TraceabilityExceptions.filterFieldNotInEntity("source_filter_json is not valid JSON");
        }
    }
}
