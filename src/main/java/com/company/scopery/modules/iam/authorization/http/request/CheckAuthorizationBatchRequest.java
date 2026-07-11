package com.company.scopery.modules.iam.authorization.http.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CheckAuthorizationBatchRequest(
        @NotEmpty @Size(max = 100) List<@Valid CheckAuthorizationRequest> checks) {
}
