package com.company.scopery.modules.traceability.dataentityfield.application.action;

import com.company.scopery.modules.traceability.dataentityfield.application.command.CreateRegistryDataEntityFieldCommand;
import com.company.scopery.modules.traceability.dataentityfield.application.response.RegistryDataEntityFieldResponse;
import com.company.scopery.modules.traceability.dataentityfield.domain.enums.DataEntityFieldDataType;
import com.company.scopery.modules.traceability.dataentityfield.domain.model.RegistryDataEntityField;
import com.company.scopery.modules.traceability.dataentityfield.domain.model.RegistryDataEntityFieldRepository;
import com.company.scopery.modules.traceability.dataentityfield.infrastructure.persistence.SpringDataRegistryDataEntityFieldJpaRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.shared.util.TraceabilityEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateRegistryDataEntityFieldAction {

    private final RegistryDataEntityFieldRepository repo;
    private final SpringDataRegistryDataEntityFieldJpaRepository springData;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public CreateRegistryDataEntityFieldAction(RegistryDataEntityFieldRepository repo,
                                               SpringDataRegistryDataEntityFieldJpaRepository springData,
                                               TraceabilityAuthorizationService authorization,
                                               TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.springData = springData;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RegistryDataEntityFieldResponse execute(CreateRegistryDataEntityFieldCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());

        DataEntityFieldDataType dataType = TraceabilityEnumParser.parseRequired(
                DataEntityFieldDataType.class, c.dataType(), "dataType");

        if (springData.existsByEntityIdAndColumnName(c.entityId(), c.columnName().trim())) {
            throw TraceabilityExceptions.dataEntityFieldColumnExists(c.columnName());
        }

        RegistryDataEntityField saved = repo.save(RegistryDataEntityField.create(
                c.entityId(), c.workspaceId(), c.columnName().trim(), dataType.name(),
                c.maxLength(), c.isNullable(), c.isUnique(), c.remark(), c.displayOrder()));

        activityLogger.logSuccess(TraceabilityEntityTypes.DATA_ENTITY_FIELD, saved.id(),
                TraceabilityActivityActions.DATA_ENTITY_FIELD_CREATED,
                "Data entity field created: " + saved.columnName());

        return RegistryDataEntityFieldResponse.from(saved);
    }
}
