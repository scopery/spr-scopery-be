package com.company.scopery.modules.project.projectphase.application.action;

import com.company.scopery.modules.project.projectphase.application.command.BulkCreateProjectPhaseCommand;
import com.company.scopery.modules.project.projectphase.application.command.CreateProjectPhaseCommand;
import com.company.scopery.platform.bulkjob.AbstractBulkCreateJobHandler;
import com.company.scopery.platform.bulkjob.BulkJobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BulkCreateProjectPhaseJobHandler extends AbstractBulkCreateJobHandler<CreateProjectPhaseCommand> {

    public static final String JOB_TYPE = "BULK_CREATE_PROJECT_PHASE";

    private final CreateProjectPhaseAction createAction;

    public BulkCreateProjectPhaseJobHandler(CreateProjectPhaseAction createAction,
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
    protected List<CreateProjectPhaseCommand> parseItems(String payloadJson) throws Exception {
        return objectMapper.readValue(payloadJson, BulkCreateProjectPhaseCommand.class).items();
    }

    @Override
    protected void createOne(CreateProjectPhaseCommand item) {
        createAction.execute(item);
    }

    @Override
    protected String extractIdentity(CreateProjectPhaseCommand item) {
        return item.code();
    }
}
