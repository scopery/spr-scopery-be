package com.company.scopery.modules.traceability.dataentity.application.action;

import com.company.scopery.modules.traceability.dataentity.application.command.BulkCreateRegistryDataEntityCommand;
import com.company.scopery.modules.traceability.dataentity.application.command.CreateRegistryDataEntityCommand;
import com.company.scopery.platform.bulkjob.AbstractBulkCreateJobHandler;
import com.company.scopery.platform.bulkjob.BulkJobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BulkCreateRegistryDataEntityJobHandler extends AbstractBulkCreateJobHandler<CreateRegistryDataEntityCommand> {

    public static final String JOB_TYPE = "BULK_CREATE_REGISTRY_DATA_ENTITY";

    private final CreateRegistryDataEntityAction createAction;

    public BulkCreateRegistryDataEntityJobHandler(CreateRegistryDataEntityAction createAction,
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
    protected List<CreateRegistryDataEntityCommand> parseItems(String payloadJson) throws Exception {
        return objectMapper.readValue(payloadJson, BulkCreateRegistryDataEntityCommand.class).items();
    }

    @Override
    protected void createOne(CreateRegistryDataEntityCommand item) {
        createAction.execute(item);
    }

    @Override
    protected String extractIdentity(CreateRegistryDataEntityCommand item) {
        return item.code();
    }
}
