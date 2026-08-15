package com.company.scopery.modules.traceability.dataentityfield.application.action;

import com.company.scopery.modules.traceability.dataentityfield.application.command.UpdateRegistryDataEntityFieldCommand;
import com.company.scopery.modules.traceability.dataentityfield.application.response.RegistryDataEntityFieldResponse;
import com.company.scopery.modules.traceability.dataentityfield.domain.enums.DataEntityFieldDataType;
import com.company.scopery.modules.traceability.dataentityfield.domain.model.RegistryDataEntityFieldRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.shared.util.TraceabilityEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateRegistryDataEntityFieldAction {

    private final RegistryDataEntityFieldRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public UpdateRegistryDataEntityFieldAction(RegistryDataEntityFieldRepository repo,
                                               TraceabilityAuthorizationService authorization,
                                               TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RegistryDataEntityFieldResponse execute(UpdateRegistryDataEntityFieldCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());

        DataEntityFieldDataType dataType = TraceabilityEnumParser.parseRequired(
                DataEntityFieldDataType.class, c.dataType(), "dataType");

        var field = repo.findByIdAndWorkspaceId(c.fieldId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.dataEntityFieldNotFound(c.fieldId()));

        var saved = repo.save(field.withUpdated(
                c.columnName().trim(), dataType.name(), c.maxLength(),
                c.isNullable(), c.isUnique(), c.isPrimaryKey(),
                c.defaultValue(), c.precision(), c.scale(), c.remark(), c.displayOrder()));

        activityLogger.logSuccess(TraceabilityEntityTypes.DATA_ENTITY_FIELD, saved.id(),
                TraceabilityActivityActions.DATA_ENTITY_FIELD_UPDATED,
                "Data entity field updated: " + saved.columnName());

        return RegistryDataEntityFieldResponse.from(saved);
    }
}
