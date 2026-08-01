package com.company.scopery.modules.project.wbs.application.action;

import com.company.scopery.modules.project.wbs.application.command.BulkCreateWbsNodeCommand;
import com.company.scopery.modules.project.wbs.application.command.CreateWbsNodeCommand;
import com.company.scopery.platform.bulkjob.AbstractBulkCreateJobHandler;
import com.company.scopery.platform.bulkjob.BulkJobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BulkCreateWbsNodeJobHandler extends AbstractBulkCreateJobHandler<CreateWbsNodeCommand> {

    public static final String JOB_TYPE = "BULK_CREATE_WBS_NODE";

    private final CreateWbsNodeAction createAction;

    public BulkCreateWbsNodeJobHandler(CreateWbsNodeAction createAction,
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
    protected List<CreateWbsNodeCommand> parseItems(String payloadJson) throws Exception {
        return objectMapper.readValue(payloadJson, BulkCreateWbsNodeCommand.class).items();
    }

    @Override
    protected void createOne(CreateWbsNodeCommand item) {
        createAction.execute(item);
    }

    @Override
    protected String extractIdentity(CreateWbsNodeCommand item) {
        return item.code();
    }
}
