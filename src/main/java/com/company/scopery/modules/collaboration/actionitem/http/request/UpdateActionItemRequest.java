package com.company.scopery.modules.collaboration.actionitem.http.request;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate; import java.util.UUID;
public record UpdateActionItemRequest(@NotBlank String title, String description, String ownerTargetType, UUID ownerTargetId, LocalDate dueDate, @NotBlank String status, Boolean clientVisible) {}
