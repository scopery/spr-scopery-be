package com.company.scopery.modules.traceability.fieldvalidation.application.action;

import com.company.scopery.modules.traceability.fieldvalidation.application.command.DeleteRegistryScreenFieldValidationCommand;
import com.company.scopery.modules.traceability.fieldvalidation.domain.model.RegistryScreenFieldValidation;
import com.company.scopery.modules.traceability.fieldvalidation.domain.model.RegistryScreenFieldValidationRepository;
import com.company.scopery.modules.traceability.screenfield.domain.model.RegistryScreenField;
import com.company.scopery.modules.traceability.screenfield.domain.model.RegistryScreenFieldRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeleteRegistryScreenFieldValidationAction {

    private final RegistryScreenFieldValidationRepository repo;
    private final RegistryScreenFieldRepository fieldRepo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public DeleteRegistryScreenFieldValidationAction(
            RegistryScreenFieldValidationRepository repo,
            RegistryScreenFieldRepository fieldRepo,
            TraceabilityAuthorizationService authorization,
            TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.fieldRepo = fieldRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(DeleteRegistryScreenFieldValidationCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());

        // IDOR check: verify field belongs to the specified screen and workspace
        RegistryScreenField field = fieldRepo.findByIdAndWorkspaceId(c.fieldId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.screenFieldNotFound(c.fieldId()));
        if (!field.screenId().equals(c.screenId())) {
            throw TraceabilityExceptions.screenFieldNotFound(c.fieldId());
        }

        // Verify the validation belongs to this field
        RegistryScreenFieldValidation validation = repo.findByIdAndWorkspaceId(c.validationId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.fieldValidationNotFound(c.validationId()));
        if (!validation.fieldId().equals(c.fieldId())) {
            throw TraceabilityExceptions.fieldValidationNotFound(c.validationId());
        }

        repo.delete(c.validationId(), c.workspaceId());

        activityLogger.logSuccess(TraceabilityEntityTypes.SCREEN_FIELD_VALIDATION, c.validationId(),
                TraceabilityActivityActions.FIELD_VALIDATION_DELETED,
                "Field validation deleted: " + c.validationId());
    }
}
