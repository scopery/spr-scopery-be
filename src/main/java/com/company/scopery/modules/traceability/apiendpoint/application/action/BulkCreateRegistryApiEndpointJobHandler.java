package com.company.scopery.modules.traceability.apiendpoint.application.action;

import com.company.scopery.modules.traceability.apiendpoint.application.command.BulkCreateRegistryApiEndpointCommand;
import com.company.scopery.modules.traceability.apiendpoint.application.command.CreateRegistryApiEndpointCommand;
import com.company.scopery.platform.bulkjob.AbstractBulkCreateJobHandler;
import com.company.scopery.platform.bulkjob.BulkJobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BulkCreateRegistryApiEndpointJobHandler extends AbstractBulkCreateJobHandler<CreateRegistryApiEndpointCommand> {

    public static final String JOB_TYPE = "BULK_CREATE_REGISTRY_API_ENDPOINT";

    private final CreateRegistryApiEndpointAction createAction;

    public BulkCreateRegistryApiEndpointJobHandler(CreateRegistryApiEndpointAction createAction,
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
    protected List<CreateRegistryApiEndpointCommand> parseItems(String payloadJson) throws Exception {
        return objectMapper.readValue(payloadJson, BulkCreateRegistryApiEndpointCommand.class).items();
    }

    @Override
    protected void createOne(CreateRegistryApiEndpointCommand item) {
        createAction.execute(item);
    }

    @Override
    protected String extractIdentity(CreateRegistryApiEndpointCommand item) {
        return item.method() + " " + item.pathPattern();
    }
}
