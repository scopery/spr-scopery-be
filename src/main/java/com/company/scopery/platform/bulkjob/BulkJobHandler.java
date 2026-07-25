package com.company.scopery.platform.bulkjob;

/**
 * Implement this interface to handle a specific bulk job type.
 * The handler is responsible for updating job progress and final status via BulkJobService.
 * Authorization is NOT required inside the handler — it was already performed at job submission.
 */
public interface BulkJobHandler {

    String supportsJobType();

    void process(BulkJobRecord job);
}
