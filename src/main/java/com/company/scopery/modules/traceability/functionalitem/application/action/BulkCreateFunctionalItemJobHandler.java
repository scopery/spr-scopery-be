package com.company.scopery.modules.traceability.functionalitem.application.action;

import com.company.scopery.modules.traceability.functionalitem.application.command.BulkCreateFunctionalItemCommand;
import com.company.scopery.modules.traceability.functionalitem.application.command.CreateFunctionalItemCommand;
import com.company.scopery.platform.bulkjob.AbstractBulkCreateJobHandler;
import com.company.scopery.platform.bulkjob.BulkJobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BulkCreateFunctionalItemJobHandler extends AbstractBulkCreateJobHandler<CreateFunctionalItemCommand> {

    public static final String JOB_TYPE = "BULK_CREATE_FUNCTIONAL_ITEM";

    private final CreateFunctionalItemAction createAction;

    public BulkCreateFunctionalItemJobHandler(CreateFunctionalItemAction createAction,
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
    protected List<CreateFunctionalItemCommand> parseItems(String payloadJson) throws Exception {
        return objectMapper.readValue(payloadJson, BulkCreateFunctionalItemCommand.class).items();
    }

    @Override
    protected void createOne(CreateFunctionalItemCommand item) {
        createAction.execute(item);
    }

    @Override
    protected String extractIdentity(CreateFunctionalItemCommand item) {
        return item.code();
    }
}
