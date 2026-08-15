package com.company.scopery.modules.traceability.componentfield.application.action;

import com.company.scopery.modules.traceability.componentfield.application.command.BulkCreateRegistryComponentFieldCommand;
import com.company.scopery.modules.traceability.componentfield.application.command.CreateRegistryComponentFieldCommand;
import com.company.scopery.platform.bulkjob.AbstractBulkCreateJobHandler;
import com.company.scopery.platform.bulkjob.BulkJobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BulkCreateRegistryComponentFieldJobHandler extends AbstractBulkCreateJobHandler<CreateRegistryComponentFieldCommand> {

    public static final String JOB_TYPE = "BULK_CREATE_REGISTRY_COMPONENT_FIELD";

    private final CreateRegistryComponentFieldAction createAction;

    public BulkCreateRegistryComponentFieldJobHandler(CreateRegistryComponentFieldAction createAction,
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
    protected List<CreateRegistryComponentFieldCommand> parseItems(String payloadJson) throws Exception {
        return objectMapper.readValue(payloadJson, BulkCreateRegistryComponentFieldCommand.class).items();
    }

    @Override
    protected void createOne(CreateRegistryComponentFieldCommand item) {
        createAction.execute(item);
    }

    @Override
    protected String extractIdentity(CreateRegistryComponentFieldCommand item) {
        return item.fieldKey();
    }
}
