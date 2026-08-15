package com.company.scopery.modules.traceability.dataentityfield.application.action;

import com.company.scopery.modules.traceability.dataentityfield.application.command.BulkCreateRegistryDataEntityFieldCommand;
import com.company.scopery.modules.traceability.dataentityfield.application.command.CreateRegistryDataEntityFieldCommand;
import com.company.scopery.platform.bulkjob.AbstractBulkCreateJobHandler;
import com.company.scopery.platform.bulkjob.BulkJobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BulkCreateRegistryDataEntityFieldJobHandler extends AbstractBulkCreateJobHandler<CreateRegistryDataEntityFieldCommand> {

    public static final String JOB_TYPE = "BULK_CREATE_REGISTRY_DATA_ENTITY_FIELD";

    private final CreateRegistryDataEntityFieldAction createAction;

    public BulkCreateRegistryDataEntityFieldJobHandler(CreateRegistryDataEntityFieldAction createAction,
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
    protected List<CreateRegistryDataEntityFieldCommand> parseItems(String payloadJson) throws Exception {
        return objectMapper.readValue(payloadJson, BulkCreateRegistryDataEntityFieldCommand.class).items();
    }

    @Override
    protected void createOne(CreateRegistryDataEntityFieldCommand item) {
        createAction.execute(item);
    }

    @Override
    protected String extractIdentity(CreateRegistryDataEntityFieldCommand item) {
        return item.columnName();
    }
}
