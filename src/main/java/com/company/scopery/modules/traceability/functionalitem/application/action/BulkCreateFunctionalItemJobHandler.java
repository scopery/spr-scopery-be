package com.company.scopery.modules.traceability.functionalitem.application.action;

import com.company.scopery.modules.traceability.functionalitem.application.command.BulkCreateFunctionalItemCommand;
import com.company.scopery.platform.bulkjob.BulkJobHandler;
import com.company.scopery.platform.bulkjob.BulkJobRecord;
import com.company.scopery.platform.bulkjob.BulkJobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BulkCreateFunctionalItemJobHandler implements BulkJobHandler {

    public static final String JOB_TYPE = "BULK_CREATE_FUNCTIONAL_ITEM";

    private static final Logger log = LoggerFactory.getLogger(BulkCreateFunctionalItemJobHandler.class);

    private final BulkCreateFunctionalItemAction action;
    private final BulkJobService bulkJobService;
    private final ObjectMapper objectMapper;

    public BulkCreateFunctionalItemJobHandler(BulkCreateFunctionalItemAction action,
                                               BulkJobService bulkJobService,
                                               ObjectMapper objectMapper) {
        this.action = action;
        this.bulkJobService = bulkJobService;
        this.objectMapper = objectMapper;
    }

    @Override
    public String supportsJobType() {
        return JOB_TYPE;
    }

    @Override
    public void process(BulkJobRecord job) {
        SecurityContext savedCtx = SecurityContextHolder.getContext();
        try {
            setSecurityContext(job.actorUsername());
            BulkCreateFunctionalItemCommand cmd = objectMapper.readValue(
                    job.payloadJson(), BulkCreateFunctionalItemCommand.class);
            action.execute(cmd);
            bulkJobService.markSucceeded(job.id(), null);
        } catch (Exception e) {
            log.error("[BulkCreateFunctionalItemJobHandler] jobId={} failed: {}", job.id(), e.getMessage());
            bulkJobService.markFailed(job.id(), e.getMessage());
        } finally {
            SecurityContextHolder.setContext(savedCtx);
        }
    }

    private void setSecurityContext(String username) {
        if (username == null) return;
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(new UsernamePasswordAuthenticationToken(username, null, List.of()));
        SecurityContextHolder.setContext(ctx);
    }
}
