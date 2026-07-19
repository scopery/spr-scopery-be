package com.company.scopery.modules.servicesupport.problem.http.request;
import java.util.UUID;
public record ResolveProblemRequest(String rootCause, String workaround, UUID resolvedBy) {}
