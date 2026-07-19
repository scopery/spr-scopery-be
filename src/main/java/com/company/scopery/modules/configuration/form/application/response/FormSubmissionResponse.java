package com.company.scopery.modules.configuration.form.application.response;

import com.company.scopery.modules.configuration.form.domain.model.FormSubmission;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Form submission details")
public record FormSubmissionResponse(
        @Schema(description = "Unique identifier of the form submission", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Identifier of the form definition used for this submission", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID formDefinitionId,

        @Schema(description = "Identifier of the form version used for this submission", example = "550e8400-e29b-41d4-a716-446655440002")
        UUID formVersionId,

        @Schema(description = "Validation status of the submission", example = "VALID")
        String validationStatus,

        @Schema(description = "Current status of the submission", example = "SUBMITTED")
        String status) {

    public static FormSubmissionResponse from(FormSubmission s) {
        return new FormSubmissionResponse(s.id(), s.formDefinitionId(), s.formVersionId(), s.validationStatus(), s.status());
    }
}
