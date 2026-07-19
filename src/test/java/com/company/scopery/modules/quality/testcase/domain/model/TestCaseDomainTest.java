package com.company.scopery.modules.quality.testcase.domain.model;
import com.company.scopery.modules.quality.testcase.domain.enums.*;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
class TestCaseDomainTest {
    private TestCase newCase() {
        return TestCase.create(UUID.randomUUID(), null, "TC-1", "Login test", null,
                TestCaseType.FUNCTIONAL, TestCasePriority.HIGH, null, "User is logged in");
    }
    @Test void approvedIsImmutable() {
        var tc = newCase().approve(UUID.randomUUID());
        assertEquals(TestCaseStatus.APPROVED, tc.status());
        var immutable = tc;
        assertThrows(IllegalStateException.class, () -> immutable.update("x", null, TestCaseType.FUNCTIONAL, TestCasePriority.LOW, null, null));
    }
    @Test void draftIsEditable() {
        var tc = newCase();
        assertTrue(tc.isEditable());
        assertEquals(TestCaseStatus.DRAFT, tc.status());
    }
    @Test void archivedIsNotEditable() {
        var tc = newCase().archive(UUID.randomUUID());
        assertFalse(tc.isEditable());
        assertEquals(TestCaseStatus.ARCHIVED, tc.status());
        assertNotNull(tc.archivedAt());
    }
    @Test void update_success_whenDraft() {
        var tc = newCase().update("New Title", "desc", TestCaseType.REGRESSION, TestCasePriority.MEDIUM, "pre", "expected");
        assertEquals("New Title", tc.title());
        assertEquals(TestCaseType.REGRESSION, tc.type());
        assertEquals(TestCasePriority.MEDIUM, tc.priority());
    }
    @Test void approve_setsApprovedAt() {
        var actor = UUID.randomUUID();
        var tc = newCase().approve(actor);
        assertEquals(actor, tc.approvedBy());
        assertNotNull(tc.approvedAt());
        assertEquals(1, tc.versionNumber());
    }
    @Test void create_hasExpectedDefaults() {
        var projectId = UUID.randomUUID();
        var suiteId = UUID.randomUUID();
        var tc = TestCase.create(projectId, suiteId, "TC-99", "Title", null,
                TestCaseType.UAT, TestCasePriority.CRITICAL, "pre", "expected");
        assertEquals(projectId, tc.projectId());
        assertEquals(suiteId, tc.testSuiteId());
        assertEquals(TestCaseStatus.DRAFT, tc.status());
        assertEquals(1, tc.versionNumber());
        assertNull(tc.approvedAt());
        assertNotNull(tc.id());
        assertNotNull(tc.createdAt());
    }
}
