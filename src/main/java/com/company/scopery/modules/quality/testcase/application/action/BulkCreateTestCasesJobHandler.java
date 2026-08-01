package com.company.scopery.modules.quality.testcase.application.action;

import com.company.scopery.modules.quality.testcase.application.command.BulkCreateTestCasesCommand;
import com.company.scopery.modules.quality.testcase.application.command.CreateTestCaseCommand;
import com.company.scopery.platform.bulkjob.AbstractBulkCreateJobHandler;
import com.company.scopery.platform.bulkjob.BulkJobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BulkCreateTestCasesJobHandler extends AbstractBulkCreateJobHandler<CreateTestCaseCommand> {

    public static final String JOB_TYPE = "BULK_CREATE_TEST_CASE";

    private final CreateTestCaseAction createAction;

    public BulkCreateTestCasesJobHandler(CreateTestCaseAction createAction,
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
    protected List<CreateTestCaseCommand> parseItems(String payloadJson) throws Exception {
        return objectMapper.readValue(payloadJson, BulkCreateTestCasesCommand.class).items();
    }

    @Override
    protected void createOne(CreateTestCaseCommand item) {
        createAction.execute(item);
    }

    @Override
    protected String extractIdentity(CreateTestCaseCommand item) {
        return item.code() != null ? item.code() : item.title();
    }
}
