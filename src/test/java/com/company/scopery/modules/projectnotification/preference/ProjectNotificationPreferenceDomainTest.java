package com.company.scopery.modules.projectnotification.preference;

import com.company.scopery.modules.projectnotification.preference.domain.enums.PreferenceChannel;
import com.company.scopery.modules.projectnotification.preference.domain.model.ProjectNotificationPreference;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProjectNotificationPreferenceDomainTest {

    @Test
    void mutedOptional_skips() {
        var pref = ProjectNotificationPreference.upsert(
                null, UUID.randomUUID(), null, UUID.randomUUID(), UUID.randomUUID(),
                "TASK_ASSIGNED", PreferenceChannel.EMAIL, true, true);
        assertFalse(pref.shouldDeliver(false));
    }

    @Test
    void mandatory_ignoresMute() {
        var pref = ProjectNotificationPreference.upsert(
                null, UUID.randomUUID(), null, UUID.randomUUID(), UUID.randomUUID(),
                "TASK_ASSIGNED", PreferenceChannel.EMAIL, false, true);
        assertTrue(pref.shouldDeliver(true));
    }

    @Test
    void enabledNotMuted_delivers() {
        var pref = ProjectNotificationPreference.upsert(
                null, UUID.randomUUID(), null, UUID.randomUUID(), UUID.randomUUID(),
                null, PreferenceChannel.IN_APP, true, false);
        assertTrue(pref.shouldDeliver(false));
    }
}
