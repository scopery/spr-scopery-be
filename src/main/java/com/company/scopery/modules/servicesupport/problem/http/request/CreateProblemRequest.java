package com.company.scopery.modules.servicesupport.problem.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateProblemRequest(@NotBlank String title, UUID projectId) {}
