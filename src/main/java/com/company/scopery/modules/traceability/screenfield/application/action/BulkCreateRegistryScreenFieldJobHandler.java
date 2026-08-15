package com.company.scopery.modules.traceability.screenfield.application.action;

import com.company.scopery.modules.traceability.screenfield.application.command.BulkCreateRegistryScreenFieldCommand;
import com.company.scopery.modules.traceability.screenfield.application.command.CreateRegistryScreenFieldCommand;
import com.company.scopery.platform.bulkjob.AbstractBulkCreateJobHandler;
import com.company.scopery.platform.bulkjob.BulkJobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BulkCreateRegistryScreenFieldJobHandler extends AbstractBulkCreateJobHandler<CreateRegistryScreenFieldCommand> {

    public static final String JOB_TYPE = "BULK_CREATE_REGISTRY_SCREEN_FIELD";

    private final CreateRegistryScreenFieldAction createAction;

    public BulkCreateRegistryScreenFieldJobHandler(CreateRegistryScreenFieldAction createAction,
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
    protected List<CreateRegistryScreenFieldCommand> parseItems(String payloadJson) throws Exception {
        return objectMapper.readValue(payloadJson, BulkCreateRegistryScreenFieldCommand.class).items();
    }

    @Override
    protected void createOne(CreateRegistryScreenFieldCommand item) {
        createAction.execute(item);
    }

    @Override
    protected String extractIdentity(CreateRegistryScreenFieldCommand item) {
        return item.fieldKey();
    }
}
