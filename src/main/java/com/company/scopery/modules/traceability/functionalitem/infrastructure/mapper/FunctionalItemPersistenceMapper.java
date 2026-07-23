package com.company.scopery.modules.traceability.functionalitem.infrastructure.mapper;
import com.company.scopery.modules.traceability.functionalitem.domain.enums.FunctionalItemPriority;
import com.company.scopery.modules.traceability.functionalitem.domain.enums.FunctionalItemStatus;
import com.company.scopery.modules.traceability.functionalitem.domain.enums.FunctionalItemType;
import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItem;
import com.company.scopery.modules.traceability.functionalitem.infrastructure.persistence.FunctionalItemJpaEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;
@Component
public class FunctionalItemPersistenceMapper {
    private final ObjectMapper objectMapper;
    public FunctionalItemPersistenceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    public FunctionalItem toDomain(FunctionalItemJpaEntity e) {
        return new FunctionalItem(
                e.getId(),
                e.getProjectId(),
                e.getWorkspaceId(),
                e.getModuleId(),
                e.getCode(),
                e.getTitle(),
                e.getDescription(),
                FunctionalItemPriority.valueOf(e.getPriority()),
                FunctionalItemStatus.valueOf(e.getStatus()),
                FunctionalItemType.valueOf(e.getType()),
                parseJson(e.getAcceptanceCriteria()),
                e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
    public FunctionalItemJpaEntity toJpaEntity(FunctionalItem d) {
        FunctionalItemJpaEntity e = new FunctionalItemJpaEntity();
        e.setId(d.id());
        e.setProjectId(d.projectId());
        e.setWorkspaceId(d.workspaceId());
        e.setModuleId(d.moduleId());
        e.setCode(d.code());
        e.setTitle(d.title());
        e.setDescription(d.description());
        e.setPriority(d.priority().name());
        e.setStatus(d.status().name());
        e.setType(d.type().name());
        e.setAcceptanceCriteria(toJson(d.acceptanceCriteria()));
        // New: leave version/createdAt null → persist. Update: stamp both for optimistic lock.
        if (d.createdAt() != null) {
            e.setVersion(d.version());
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
    private List<String> parseJson(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
    private String toJson(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception ex) {
            return null;
        }
    }
}
