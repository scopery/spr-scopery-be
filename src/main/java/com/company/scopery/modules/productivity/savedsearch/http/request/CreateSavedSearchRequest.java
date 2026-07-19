package com.company.scopery.modules.productivity.savedsearch.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateSavedSearchRequest(@NotBlank String name, @NotBlank String scope, String queryText, String filtersJson, UUID projectId) {}
