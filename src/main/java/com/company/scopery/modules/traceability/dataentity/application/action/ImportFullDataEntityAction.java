package com.company.scopery.modules.traceability.dataentity.application.action;

import com.company.scopery.modules.traceability.dataentity.application.command.ImportFullDataEntityItemCommand;
import com.company.scopery.modules.traceability.dataentity.application.response.RegistryDataEntityResponse;
import com.company.scopery.modules.traceability.dataentity.domain.model.RegistryDataEntity;
import com.company.scopery.modules.traceability.dataentity.domain.model.RegistryDataEntityRepository;
import com.company.scopery.modules.traceability.dataentityfield.domain.enums.DataEntityFieldDataType;
import com.company.scopery.modules.traceability.dataentityfield.domain.model.RegistryDataEntityField;
import com.company.scopery.modules.traceability.dataentityfield.domain.model.RegistryDataEntityFieldRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.util.TraceabilityEnumParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ImportFullDataEntityAction {

    private static final Logger log = LoggerFactory.getLogger(ImportFullDataEntityAction.class);

    private final RegistryDataEntityRepository entityRepo;
    private final RegistryDataEntityFieldRepository fieldRepo;
    private final TraceabilityAuthorizationService authorization;

    public ImportFullDataEntityAction(RegistryDataEntityRepository entityRepo,
                                      RegistryDataEntityFieldRepository fieldRepo,
                                      TraceabilityAuthorizationService authorization) {
        this.entityRepo = entityRepo;
        this.fieldRepo = fieldRepo;
        this.authorization = authorization;
    }

    @Transactional
    public RegistryDataEntityResponse execute(ImportFullDataEntityItemCommand cmd) {
        authorization.requireWorkspaceCreate(cmd.workspaceId());

        RegistryDataEntity entity = entityRepo.save(
                RegistryDataEntity.create(cmd.applicationId(), cmd.workspaceId(),
                        cmd.moduleId(), cmd.code().trim(), cmd.name().trim(),
                        cmd.description(), cmd.tableName()));

        if (cmd.fields() != null) {
            for (var f : cmd.fields()) {
                DataEntityFieldDataType dataType = TraceabilityEnumParser.parseRequired(
                        DataEntityFieldDataType.class, f.dataType(), "dataType");
                try {
                    fieldRepo.save(RegistryDataEntityField.create(
                            entity.id(), cmd.workspaceId(), f.columnName().trim(), dataType.name(),
                            f.maxLength(), f.isNullable(), f.isUnique(), f.isPrimaryKey(),
                            f.defaultValue(), f.precision(), f.scale(), f.remark(), f.displayOrder()));
                } catch (Exception e) {
                    log.warn("[ImportFullDataEntity] Skipping field '{}' for entity '{}': {}",
                            f.columnName(), cmd.code(), e.getMessage());
                }
            }
        }

        log.info("[ImportFullDataEntity] Imported entity id={} code={} fields={}",
                entity.id(), entity.code(), cmd.fields() != null ? cmd.fields().size() : 0);

        return RegistryDataEntityResponse.from(entity);
    }
}
