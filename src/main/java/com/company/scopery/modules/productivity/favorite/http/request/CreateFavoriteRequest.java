package com.company.scopery.modules.productivity.favorite.http.request;
import jakarta.validation.constraints.NotBlank; import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CreateFavoriteRequest(@NotBlank String targetType, @NotNull UUID targetId, String labelOverride) {}
