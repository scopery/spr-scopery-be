package com.company.scopery.platform.bulkjob;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.common.exception.BusinessException;
import com.company.scopery.common.exception.ConflictException;
import com.company.scopery.common.exception.NotFoundException;
import com.company.scopery.common.exception.ResourceNotFoundException;
import com.company.scopery.common.exception.ValidationException;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;

public abstract class AbstractBulkCreateJobHandler<C> implements BulkJobHandler {

    private static final Logger log = LoggerFactory.getLogger(AbstractBulkCreateJobHandler.class);
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    protected final BulkJobService bulkJobService;
    protected final ObjectMapper objectMapper;

    protected AbstractBulkCreateJobHandler(BulkJobService bulkJobService, ObjectMapper objectMapper) {
        this.bulkJobService = bulkJobService;
        this.objectMapper = objectMapper;
    }

    protected abstract List<C> parseItems(String payloadJson) throws Exception;

    protected abstract void createOne(C item) throws Exception;

    protected String extractIdentity(C item) {
        return null;
    }

    @Override
    public final void process(BulkJobRecord job) {
        SecurityContext savedCtx = SecurityContextHolder.getContext();
        try {
            setSecurityContext(job.actorUsername());
            List<C> items = parseItems(job.payloadJson());

            int succeeded = 0, failed = 0;
            for (int i = 0; i < items.size(); i++) {
                C item = items.get(i);
                try {
                    createOne(item);
                    succeeded++;
                    bulkJobService.updateProgress(job.id(), succeeded, failed);
                } catch (Exception e) {
                    log.warn("[{}] jobId={} item[{}] failed: {}", supportsJobType(), job.id(), i, e.getMessage());
                    failed++;
                    String msg = e.getMessage();
                    if (msg != null && msg.length() > 500) msg = msg.substring(0, 500);
                    BulkJobFailure failure = new BulkJobFailure(
                            i,
                            extractIdentity(item),
                            classifyErrorCode(e),
                            msg != null ? msg : "Unknown error",
                            serializeItem(item)
                    );
                    bulkJobService.updateProgressWithFailure(job.id(), succeeded, failed, failure);
                }
            }

            if (failed == 0) {
                bulkJobService.markSucceeded(job.id(), "Created " + succeeded + " items");
            } else if (succeeded > 0) {
                bulkJobService.markPartial(job.id(), succeeded + " created, " + failed + " failed");
            } else {
                bulkJobService.markFailed(job.id(), "All " + failed + " items failed");
            }
        } catch (Exception e) {
            log.error("[{}] jobId={} failed: {}", supportsJobType(), job.id(), e.getMessage());
            bulkJobService.markFailed(job.id(), e.getMessage());
        } finally {
            SecurityContextHolder.setContext(savedCtx);
        }
    }

    private String classifyErrorCode(Exception e) {
        if (e instanceof ConflictException) return "DUPLICATE_KEY";
        if (e instanceof NotFoundException || e instanceof ResourceNotFoundException) return "NOT_FOUND";
        if (e instanceof ValidationException || e instanceof BusinessException) return "VALIDATION";
        if (e instanceof IllegalArgumentException) return "VALIDATION";
        if (e instanceof AppException appEx) {
            HttpStatus status = appEx.getHttpStatus();
            if (status == HttpStatus.CONFLICT) return "DUPLICATE_KEY";
            if (status == HttpStatus.NOT_FOUND) return "NOT_FOUND";
            if (status == HttpStatus.BAD_REQUEST || status == HttpStatus.UNPROCESSABLE_ENTITY) return "VALIDATION";
            if (status == HttpStatus.FORBIDDEN) return "FORBIDDEN";
        }
        return "UNKNOWN";
    }

    private Object serializeItem(C item) {
        try {
            return objectMapper.convertValue(item, MAP_TYPE);
        } catch (Exception e) {
            return null;
        }
    }

    private void setSecurityContext(String username) {
        if (username == null) return;
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(new UsernamePasswordAuthenticationToken(username, null, List.of()));
        SecurityContextHolder.setContext(ctx);
    }
}
