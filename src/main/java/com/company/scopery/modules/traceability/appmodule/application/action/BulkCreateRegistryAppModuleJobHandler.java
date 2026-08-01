package com.company.scopery.modules.traceability.appmodule.application.action;

import com.company.scopery.modules.traceability.appmodule.application.command.BulkCreateRegistryAppModuleCommand;
import com.company.scopery.modules.traceability.appmodule.application.command.CreateRegistryAppModuleCommand;
import com.company.scopery.platform.bulkjob.AbstractBulkCreateJobHandler;
import com.company.scopery.platform.bulkjob.BulkJobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BulkCreateRegistryAppModuleJobHandler extends AbstractBulkCreateJobHandler<CreateRegistryAppModuleCommand> {

    public static final String JOB_TYPE = "BULK_CREATE_REGISTRY_APP_MODULE";

    private final CreateRegistryAppModuleAction createAction;

    public BulkCreateRegistryAppModuleJobHandler(CreateRegistryAppModuleAction createAction,
                                                  BulkJobService bulkJobService,
                                                  ObjectMapper objectMapper) {
        super(bulkJobService, objectMapper);
        this.createAction = createAction;
    }

    @Override
    public String supportsJobType() {
        return JOB_TYPE;
    }

    @Override
    protected List<CreateRegistryAppModuleCommand> parseItems(String payloadJson) throws Exception {
        return objectMapper.readValue(payloadJson, BulkCreateRegistryAppModuleCommand.class).items();
    }

    @Override
    protected void createOne(CreateRegistryAppModuleCommand item) {
        createAction.execute(item);
    }

    @Override
    protected String extractIdentity(CreateRegistryAppModuleCommand item) {
        return item.code();
    }
}
