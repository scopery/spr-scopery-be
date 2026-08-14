package com.company.scopery.modules.traceability.screen.application.action;

import com.company.scopery.modules.traceability.screen.application.command.ImportFullScreenSpecItemCommand;
import com.company.scopery.modules.traceability.screen.application.command.ImportFullScreenSpecJobCommand;
import com.company.scopery.platform.bulkjob.AbstractBulkCreateJobHandler;
import com.company.scopery.platform.bulkjob.BulkJobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ImportFullScreenSpecJobHandler extends AbstractBulkCreateJobHandler<ImportFullScreenSpecItemCommand> {

    public static final String JOB_TYPE = "IMPORT_FULL_SCREEN_SPEC";

    private final ImportFullScreenSpecAction importAction;

    public ImportFullScreenSpecJobHandler(ImportFullScreenSpecAction importAction,
                                          BulkJobService bulkJobService,
                                          ObjectMapper objectMapper) {
        super(bulkJobService, objectMapper);
        this.importAction = importAction;
    }

    @Override
    public String supportsJobType() { return JOB_TYPE; }

    @Override
    protected List<ImportFullScreenSpecItemCommand> parseItems(String payloadJson) throws Exception {
        return objectMapper.readValue(payloadJson, ImportFullScreenSpecJobCommand.class).items();
    }

    @Override
    protected void createOne(ImportFullScreenSpecItemCommand item) {
        importAction.execute(item);
    }

    @Override
    protected String extractIdentity(ImportFullScreenSpecItemCommand item) {
        return item.code();
    }
}
