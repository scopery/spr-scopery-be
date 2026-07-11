package com.company.scopery.modules.iam.grant.http.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddIamGrantRightRequest(@NotNull UUID rightId) {
}
