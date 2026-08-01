package com.company.scopery.modules.traceability.nonfunctionalitem.application.action;

import com.company.scopery.modules.traceability.nonfunctionalitem.application.command.BulkCreateNonFunctionalItemCommand;
import com.company.scopery.modules.traceability.nonfunctionalitem.application.command.CreateNonFunctionalItemCommand;
import com.company.scopery.platform.bulkjob.AbstractBulkCreateJobHandler;
import com.company.scopery.platform.bulkjob.BulkJobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BulkCreateNonFunctionalItemJobHandler extends AbstractBulkCreateJobHandler<CreateNonFunctionalItemCommand> {

    public static final String JOB_TYPE = "BULK_CREATE_NON_FUNCTIONAL_ITEM";

    private final CreateNonFunctionalItemAction createAction;

    public BulkCreateNonFunctionalItemJobHandler(CreateNonFunctionalItemAction createAction,
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
    protected List<CreateNonFunctionalItemCommand> parseItems(String payloadJson) throws Exception {
        return objectMapper.readValue(payloadJson, BulkCreateNonFunctionalItemCommand.class).items();
    }

    @Override
    protected void createOne(CreateNonFunctionalItemCommand item) {
        createAction.execute(item);
    }

    @Override
    protected String extractIdentity(CreateNonFunctionalItemCommand item) {
        return item.code();
    }
}
