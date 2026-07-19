package com.company.scopery.modules.servicesupport.queue.http.request;
import jakarta.validation.constraints.NotBlank;
public record CreateSupportQueueRequest(@NotBlank String queueCode, @NotBlank String name) {}
