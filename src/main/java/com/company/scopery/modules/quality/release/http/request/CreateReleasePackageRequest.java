package com.company.scopery.modules.quality.release.http.request;
import jakarta.validation.constraints.NotBlank; import java.time.LocalDate;
public record CreateReleasePackageRequest(@NotBlank String code, @NotBlank String versionLabel, @NotBlank String name, String description, @NotBlank String releaseType, LocalDate plannedReleaseDate) {}
