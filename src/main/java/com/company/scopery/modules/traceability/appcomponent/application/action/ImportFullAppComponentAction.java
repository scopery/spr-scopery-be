package com.company.scopery.modules.traceability.appcomponent.application.action;

import com.company.scopery.modules.traceability.appcomponent.application.command.ImportFullAppComponentItemCommand;
import com.company.scopery.modules.traceability.appcomponent.application.response.RegistryAppComponentResponse;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponent;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponentRepository;
import com.company.scopery.modules.traceability.componentfield.domain.model.RegistryComponentField;
import com.company.scopery.modules.traceability.componentfield.domain.model.RegistryComponentFieldRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ImportFullAppComponentAction {

    private static final Logger log = LoggerFactory.getLogger(ImportFullAppComponentAction.class);

    private final RegistryAppComponentRepository componentRepo;
    private final RegistryComponentFieldRepository fieldRepo;
    private final TraceabilityAuthorizationService authorization;

    public ImportFullAppComponentAction(RegistryAppComponentRepository componentRepo,
                                        RegistryComponentFieldRepository fieldRepo,
                                        TraceabilityAuthorizationService authorization) {
        this.componentRepo = componentRepo;
        this.fieldRepo = fieldRepo;
        this.authorization = authorization;
    }

    @Transactional
    public RegistryAppComponentResponse execute(ImportFullAppComponentItemCommand cmd) {
        authorization.requireWorkspaceCreate(cmd.workspaceId());

        RegistryAppComponent component = componentRepo.save(
                RegistryAppComponent.create(cmd.applicationId(), cmd.workspaceId(),
                        cmd.code().trim(), cmd.name().trim(), cmd.description(),
                        cmd.componentType(), cmd.optionSourceType(),
                        cmd.sourceEntityId(), cmd.sourceValueColumn(),
                        cmd.sourceLabelColumn(), cmd.sourceFilterJson()));

        if (cmd.fields() != null) {
            for (var f : cmd.fields()) {
                try {
                    fieldRepo.save(RegistryComponentField.create(
                            component.id(), cmd.workspaceId(),
                            f.fieldKey().trim(), f.label().trim(), f.fieldType().trim(),
                            f.required(), f.maxLength(), f.remark(), f.displayOrder()));
                } catch (Exception e) {
                    log.warn("[ImportFullAppComponent] Skipping field '{}' for component '{}': {}",
                            f.fieldKey(), cmd.code(), e.getMessage());
                }
            }
        }

        log.info("[ImportFullAppComponent] Imported component id={} code={} fields={}",
                component.id(), component.code(), cmd.fields() != null ? cmd.fields().size() : 0);

        return RegistryAppComponentResponse.from(component);
    }
}
