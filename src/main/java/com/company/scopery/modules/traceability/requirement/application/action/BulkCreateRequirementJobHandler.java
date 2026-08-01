package com.company.scopery.modules.traceability.requirement.application.action;

import com.company.scopery.modules.traceability.requirement.application.command.BulkCreateRequirementCommand;
import com.company.scopery.modules.traceability.requirement.application.command.CreateRequirementCommand;
import com.company.scopery.platform.bulkjob.AbstractBulkCreateJobHandler;
import com.company.scopery.platform.bulkjob.BulkJobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BulkCreateRequirementJobHandler extends AbstractBulkCreateJobHandler<CreateRequirementCommand> {

    public static final String JOB_TYPE = "BULK_CREATE_REQUIREMENT";

    private final CreateRequirementAction createAction;

    public BulkCreateRequirementJobHandler(CreateRequirementAction createAction,
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
    protected List<CreateRequirementCommand> parseItems(String payloadJson) throws Exception {
        return objectMapper.readValue(payloadJson, BulkCreateRequirementCommand.class).items();
    }

    @Override
    protected void createOne(CreateRequirementCommand item) {
        createAction.execute(item);
    }

    @Override
    protected String extractIdentity(CreateRequirementCommand item) {
        return item.code() != null ? item.code() : item.title();
    }
}
