package com.company.scopery.modules.quality.testcase.http.request;
import java.util.List; import java.util.UUID;
public record UpdateUseCaseLinksRequest(List<UUID> useCaseIds) {}
