package com.company.scopery.modules.traceability.apiendpoint.application.action;

import com.company.scopery.modules.traceability.apiendpoint.application.command.ImportFullApiEndpointItemCommand;
import com.company.scopery.modules.traceability.apiendpoint.application.response.RegistryApiEndpointResponse;
import com.company.scopery.modules.traceability.apiendpoint.domain.model.RegistryApiEndpoint;
import com.company.scopery.modules.traceability.apiendpoint.domain.model.RegistryApiEndpointRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ImportFullApiEndpointAction {

    private static final Logger log = LoggerFactory.getLogger(ImportFullApiEndpointAction.class);

    private final RegistryApiEndpointRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final ObjectMapper objectMapper;

    public ImportFullApiEndpointAction(RegistryApiEndpointRepository repo,
                                       TraceabilityAuthorizationService authorization,
                                       ObjectMapper objectMapper) {
        this.repo = repo;
        this.authorization = authorization;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public RegistryApiEndpointResponse execute(ImportFullApiEndpointItemCommand cmd) {
        authorization.requireWorkspaceCreate(cmd.workspaceId());

        String paramsJson = null;
        if (cmd.requestParams() != null && !cmd.requestParams().isEmpty()) {
            try {
                paramsJson = objectMapper.writeValueAsString(cmd.requestParams());
            } catch (JsonProcessingException e) {
                log.warn("[ImportFullApiEndpoint] Failed to serialize requestParams for '{}': {}",
                        cmd.pathPattern(), e.getMessage());
            }
        }

        RegistryApiEndpoint saved = repo.save(
                RegistryApiEndpoint.create(cmd.applicationId(), cmd.projectId(),
                        cmd.method().toUpperCase(), cmd.pathPattern().trim(),
                        cmd.name().trim(), cmd.description(),
                        paramsJson, cmd.responseSchemaJson()));

        log.info("[ImportFullApiEndpoint] Imported id={} method={} path={}",
                saved.id(), saved.method(), saved.pathPattern());

        return RegistryApiEndpointResponse.from(saved);
    }
}
