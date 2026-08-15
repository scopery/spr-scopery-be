package com.company.scopery.modules.traceability.dataentity.application.action;

import com.company.scopery.modules.traceability.dataentity.application.command.ImportFullDataEntityItemCommand;
import com.company.scopery.modules.traceability.dataentity.application.command.ImportFullDataEntityJobCommand;
import com.company.scopery.platform.bulkjob.AbstractBulkCreateJobHandler;
import com.company.scopery.platform.bulkjob.BulkJobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ImportFullDataEntityJobHandler extends AbstractBulkCreateJobHandler<ImportFullDataEntityItemCommand> {

    public static final String JOB_TYPE = "IMPORT_FULL_DATA_ENTITY";

    private final ImportFullDataEntityAction importAction;

    public ImportFullDataEntityJobHandler(ImportFullDataEntityAction importAction,
                                          BulkJobService bulkJobService,
                                          ObjectMapper objectMapper) {
        super(bulkJobService, objectMapper);
        this.importAction = importAction;
    }

    @Override
    public String supportsJobType() { return JOB_TYPE; }

    @Override
    protected List<ImportFullDataEntityItemCommand> parseItems(String payloadJson) throws Exception {
        return objectMapper.readValue(payloadJson, ImportFullDataEntityJobCommand.class).items();
    }

    @Override
    protected void createOne(ImportFullDataEntityItemCommand item) {
        importAction.execute(item);
    }

    @Override
    protected String extractIdentity(ImportFullDataEntityItemCommand item) {
        return item.code();
    }
}
