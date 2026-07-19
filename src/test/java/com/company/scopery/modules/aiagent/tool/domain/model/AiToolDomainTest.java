package com.company.scopery.modules.aiagent.tool.domain.model;

import com.company.scopery.modules.aiagent.tool.domain.enums.AiToolMutationType;
import com.company.scopery.modules.aiagent.tool.domain.enums.AiToolStatus;
import com.company.scopery.modules.aiagent.tool.domain.valueobject.AiToolCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AiToolDomainTest {

    @Test
    void create_activeReadTool() {
        AiTool tool = AiTool.create(
                AiToolCode.of("summarizeMeetingNotes"),
                "Summarize Meeting Notes",
                "Seed tool",
                "MEETING",
                AiToolMutationType.READ,
                false);

        assertEquals("summarizeMeetingNotes", tool.code().value());
        assertEquals(AiToolStatus.ACTIVE, tool.status());
        assertFalse(tool.requiresHumanApproval());
    }

    @Test
    void activate_rejectsDeprecated() {
        AiTool tool = AiTool.create(
                AiToolCode.of("draftClientUpdate"),
                "Draft Client Update",
                null,
                "SERVICE_SUPPORT",
                AiToolMutationType.READ,
                false);
        tool.deprecate();

        IllegalStateException ex = assertThrows(IllegalStateException.class, tool::activate);
        assertTrue(ex.getMessage().contains("Deprecated"));
    }

    @Test
    void toolCode_rejectsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> AiToolCode.of("1bad"));
        assertThrows(IllegalArgumentException.class, () -> AiToolCode.of("bad-code"));
    }

    @Test
    void execution_markNoOp() {
        AiToolExecution execution = AiToolExecution.start(
                java.util.UUID.randomUUID(), null, null, "input", null);
        execution.markNoOp("stub");
        assertEquals(
                com.company.scopery.modules.aiagent.tool.domain.enums.AiToolExecutionStatus.NO_OP,
                execution.status());
        assertNotNull(execution.finishedAt());
    }
}
