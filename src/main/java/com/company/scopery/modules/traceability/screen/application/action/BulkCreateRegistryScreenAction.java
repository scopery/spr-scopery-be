package com.company.scopery.modules.traceability.screen.application.action;

import com.company.scopery.modules.traceability.application.domain.model.RegistryApplicationRepository;
import com.company.scopery.modules.traceability.screen.application.command.BulkCreateRegistryScreenCommand;
import com.company.scopery.modules.traceability.screen.application.response.RegistryScreenResponse;
import com.company.scopery.modules.traceability.screen.domain.model.RegistryScreen;
import com.company.scopery.modules.traceability.screen.domain.model.RegistryScreenRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class BulkCreateRegistryScreenAction {

    private final RegistryScreenRepository repo;
    private final RegistryApplicationRepository applications;
    private final TraceabilityAuthorizationService authorization;

    public BulkCreateRegistryScreenAction(RegistryScreenRepository repo,
                                          RegistryApplicationRepository applications,
                                          TraceabilityAuthorizationService authorization) {
        this.repo = repo;
        this.applications = applications;
        this.authorization = authorization;
    }

    @Transactional
    public List<RegistryScreenResponse> execute(BulkCreateRegistryScreenCommand cmd) {
        authorization.requireWorkspaceCreate(cmd.workspaceId());
        applications.findByIdAndWorkspaceId(cmd.applicationId(), cmd.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.applicationNotFound(cmd.applicationId()));

        List<RegistryScreenResponse> results = new ArrayList<>();
        for (var item : cmd.items()) {
            results.add(RegistryScreenResponse.from(repo.save(
                    RegistryScreen.create(cmd.applicationId(), item.projectId(), item.code().trim(), item.name().trim(), item.routePath())
            )));
        }
        return results;
    }
}
