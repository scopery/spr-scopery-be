package com.company.scopery.modules.aiagent.execution.application.guard;

import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Single source of truth for the CT-2 restriction: execution log lifecycle writes
 * (create/running/succeeded/failed/cancel) are internal-only unless explicitly
 * opted out via config. Injected into every lifecycle-write Action so the check
 * can't be silently missed on one endpoint, as happened previously with cancel.
 */
@Component
public class ExecutionLifecycleWriteGuard {

    private final boolean restrictLifecycleWrites;

    public ExecutionLifecycleWriteGuard(
            @Value("${scopery.aiagent.execution.restrict-lifecycle-writes:true}") boolean restrictLifecycleWrites) {
        this.restrictLifecycleWrites = restrictLifecycleWrites;
    }

    public void verifyWritable() {
        if (restrictLifecycleWrites) {
            throw AiAgentExceptions.executionLifecycleWriteRestricted();
        }
    }
}
