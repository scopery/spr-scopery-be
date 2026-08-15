package com.company.scopery.modules.traceability.appcomponent.application.action;

import com.company.scopery.modules.traceability.appcomponent.application.command.ImportFullAppComponentItemCommand;
import com.company.scopery.modules.traceability.appcomponent.application.command.ImportFullAppComponentJobCommand;
import com.company.scopery.platform.bulkjob.AbstractBulkCreateJobHandler;
import com.company.scopery.platform.bulkjob.BulkJobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ImportFullAppComponentJobHandler extends AbstractBulkCreateJobHandler<ImportFullAppComponentItemCommand> {

    public static final String JOB_TYPE = "IMPORT_FULL_APP_COMPONENT";

    private final ImportFullAppComponentAction importAction;

    public ImportFullAppComponentJobHandler(ImportFullAppComponentAction importAction,
                                            BulkJobService bulkJobService,
                                            ObjectMapper objectMapper) {
        super(bulkJobService, objectMapper);
        this.importAction = importAction;
    }

    @Override
    public String supportsJobType() { return JOB_TYPE; }

    @Override
    protected List<ImportFullAppComponentItemCommand> parseItems(String payloadJson) throws Exception {
        return objectMapper.readValue(payloadJson, ImportFullAppComponentJobCommand.class).items();
    }

    @Override
    protected void createOne(ImportFullAppComponentItemCommand item) {
        importAction.execute(item);
    }

    @Override
    protected String extractIdentity(ImportFullAppComponentItemCommand item) {
        return item.code();
    }
}
