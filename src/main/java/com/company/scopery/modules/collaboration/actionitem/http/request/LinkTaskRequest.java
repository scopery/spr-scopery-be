package com.company.scopery.modules.collaboration.actionitem.http.request;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record LinkTaskRequest(@NotNull UUID taskId) {}
