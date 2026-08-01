package com.company.scopery.modules.project.task.application.action;

import com.company.scopery.modules.project.task.application.command.BulkCreateTaskCommand;
import com.company.scopery.modules.project.task.application.command.CreateTaskCommand;
import com.company.scopery.platform.bulkjob.AbstractBulkCreateJobHandler;
import com.company.scopery.platform.bulkjob.BulkJobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BulkCreateTaskJobHandler extends AbstractBulkCreateJobHandler<CreateTaskCommand> {

    public static final String JOB_TYPE = "BULK_CREATE_TASK";

    private final CreateTaskAction createAction;

    public BulkCreateTaskJobHandler(CreateTaskAction createAction,
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
    protected List<CreateTaskCommand> parseItems(String payloadJson) throws Exception {
        return objectMapper.readValue(payloadJson, BulkCreateTaskCommand.class).items();
    }

    @Override
    protected void createOne(CreateTaskCommand item) {
        createAction.execute(item);
    }

    @Override
    protected String extractIdentity(CreateTaskCommand item) {
        return item.code();
    }
}
