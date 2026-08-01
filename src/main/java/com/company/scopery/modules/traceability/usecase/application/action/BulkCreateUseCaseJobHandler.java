package com.company.scopery.modules.traceability.usecase.application.action;

import com.company.scopery.modules.traceability.usecase.application.command.BulkCreateUseCaseCommand;
import com.company.scopery.modules.traceability.usecase.application.command.CreateUseCaseCommand;
import com.company.scopery.platform.bulkjob.AbstractBulkCreateJobHandler;
import com.company.scopery.platform.bulkjob.BulkJobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BulkCreateUseCaseJobHandler extends AbstractBulkCreateJobHandler<CreateUseCaseCommand> {

    public static final String JOB_TYPE = "BULK_CREATE_USE_CASE";

    private final CreateUseCaseAction createAction;

    public BulkCreateUseCaseJobHandler(CreateUseCaseAction createAction,
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
    protected List<CreateUseCaseCommand> parseItems(String payloadJson) throws Exception {
        return objectMapper.readValue(payloadJson, BulkCreateUseCaseCommand.class).items();
    }

    @Override
    protected void createOne(CreateUseCaseCommand item) {
        createAction.execute(item);
    }

    @Override
    protected String extractIdentity(CreateUseCaseCommand item) {
        return item.key();
    }
}
