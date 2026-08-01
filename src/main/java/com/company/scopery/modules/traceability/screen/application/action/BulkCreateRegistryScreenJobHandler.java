package com.company.scopery.modules.traceability.screen.application.action;

import com.company.scopery.modules.traceability.screen.application.command.BulkCreateRegistryScreenCommand;
import com.company.scopery.modules.traceability.screen.application.command.CreateRegistryScreenCommand;
import com.company.scopery.platform.bulkjob.AbstractBulkCreateJobHandler;
import com.company.scopery.platform.bulkjob.BulkJobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BulkCreateRegistryScreenJobHandler extends AbstractBulkCreateJobHandler<CreateRegistryScreenCommand> {

    public static final String JOB_TYPE = "BULK_CREATE_REGISTRY_SCREEN";

    private final CreateRegistryScreenAction createAction;

    public BulkCreateRegistryScreenJobHandler(CreateRegistryScreenAction createAction,
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
    protected List<CreateRegistryScreenCommand> parseItems(String payloadJson) throws Exception {
        return objectMapper.readValue(payloadJson, BulkCreateRegistryScreenCommand.class).items();
    }

    @Override
    protected void createOne(CreateRegistryScreenCommand item) {
        createAction.execute(item);
    }

    @Override
    protected String extractIdentity(CreateRegistryScreenCommand item) {
        return item.code();
    }
}
