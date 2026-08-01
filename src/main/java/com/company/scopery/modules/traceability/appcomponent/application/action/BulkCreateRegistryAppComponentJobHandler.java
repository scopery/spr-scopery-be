package com.company.scopery.modules.traceability.appcomponent.application.action;

import com.company.scopery.modules.traceability.appcomponent.application.command.BulkCreateRegistryAppComponentCommand;
import com.company.scopery.modules.traceability.appcomponent.application.command.CreateRegistryAppComponentCommand;
import com.company.scopery.platform.bulkjob.AbstractBulkCreateJobHandler;
import com.company.scopery.platform.bulkjob.BulkJobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BulkCreateRegistryAppComponentJobHandler extends AbstractBulkCreateJobHandler<CreateRegistryAppComponentCommand> {

    public static final String JOB_TYPE = "BULK_CREATE_REGISTRY_APP_COMPONENT";

    private final CreateRegistryAppComponentAction createAction;

    public BulkCreateRegistryAppComponentJobHandler(CreateRegistryAppComponentAction createAction,
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
    protected List<CreateRegistryAppComponentCommand> parseItems(String payloadJson) throws Exception {
        return objectMapper.readValue(payloadJson, BulkCreateRegistryAppComponentCommand.class).items();
    }

    @Override
    protected void createOne(CreateRegistryAppComponentCommand item) {
        createAction.execute(item);
    }

    @Override
    protected String extractIdentity(CreateRegistryAppComponentCommand item) {
        return item.code();
    }
}
