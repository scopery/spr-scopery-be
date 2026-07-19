package com.company.scopery.modules.servicesupport.warranty.http.request;
import java.time.LocalDate; import java.util.UUID;
public record CreateWarrantyCoverageRequest(UUID projectId, UUID serviceProfileId, LocalDate startDate, LocalDate endDate) {}
