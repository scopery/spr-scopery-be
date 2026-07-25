package com.company.scopery.modules.traceability.dataentity.application.action;

import com.company.scopery.modules.traceability.dataentity.application.command.BulkCreateRegistryDataEntityCommand;
import com.company.scopery.modules.traceability.dataentity.application.response.RegistryDataEntityResponse;
import com.company.scopery.modules.traceability.dataentity.domain.model.RegistryDataEntity;
import com.company.scopery.modules.traceability.dataentity.domain.model.RegistryDataEntityRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class BulkCreateRegistryDataEntityAction {

    private final RegistryDataEntityRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public BulkCreateRegistryDataEntityAction(RegistryDataEntityRepository repo,
                                              TraceabilityAuthorizationService authorization) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional
    public List<RegistryDataEntityResponse> execute(BulkCreateRegistryDataEntityCommand cmd) {
        authorization.requireWorkspaceCreate(cmd.workspaceId());

        List<RegistryDataEntityResponse> results = new ArrayList<>();
        for (var item : cmd.items()) {
            results.add(RegistryDataEntityResponse.from(repo.save(
                    RegistryDataEntity.create(cmd.applicationId(), cmd.workspaceId(), item.moduleId(),
                            item.code().trim(), item.name().trim(), item.description(), item.tableName())
            )));
        }
        return results;
    }
}
