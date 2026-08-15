package com.company.scopery.modules.traceability.apiendpoint.application.action;

import com.company.scopery.modules.traceability.apiendpoint.application.command.ImportFullApiEndpointItemCommand;
import com.company.scopery.modules.traceability.apiendpoint.application.command.ImportFullApiEndpointJobCommand;
import com.company.scopery.platform.bulkjob.AbstractBulkCreateJobHandler;
import com.company.scopery.platform.bulkjob.BulkJobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ImportFullApiEndpointJobHandler extends AbstractBulkCreateJobHandler<ImportFullApiEndpointItemCommand> {

    public static final String JOB_TYPE = "IMPORT_FULL_API_ENDPOINT";

    private final ImportFullApiEndpointAction importAction;

    public ImportFullApiEndpointJobHandler(ImportFullApiEndpointAction importAction,
                                           BulkJobService bulkJobService,
                                           ObjectMapper objectMapper) {
        super(bulkJobService, objectMapper);
        this.importAction = importAction;
    }

    @Override
    public String supportsJobType() { return JOB_TYPE; }

    @Override
    protected List<ImportFullApiEndpointItemCommand> parseItems(String payloadJson) throws Exception {
        return objectMapper.readValue(payloadJson, ImportFullApiEndpointJobCommand.class).items();
    }

    @Override
    protected void createOne(ImportFullApiEndpointItemCommand item) {
        importAction.execute(item);
    }

    @Override
    protected String extractIdentity(ImportFullApiEndpointItemCommand item) {
        return item.method() + " " + item.pathPattern();
    }
}
