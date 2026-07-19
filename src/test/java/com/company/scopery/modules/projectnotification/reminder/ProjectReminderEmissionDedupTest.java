package com.company.scopery.modules.projectnotification.reminder;

import com.company.scopery.modules.projectnotification.reminder.domain.enums.ReminderType;
import com.company.scopery.modules.projectnotification.reminder.domain.model.ProjectReminderEmission;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProjectReminderEmissionDedupTest {

    @Test
    void buildDedupKey_stable() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2026, 7, 12);
        String a = ProjectReminderEmission.buildDedupKey(taskId, ReminderType.TASK_DUE_SOON, date, userId);
        String b = ProjectReminderEmission.buildDedupKey(taskId, ReminderType.TASK_DUE_SOON, date, userId);
        assertEquals(a, b);
        assertTrue(a.contains(taskId.toString()));
        assertTrue(a.contains("TASK_DUE_SOON"));
    }
}
