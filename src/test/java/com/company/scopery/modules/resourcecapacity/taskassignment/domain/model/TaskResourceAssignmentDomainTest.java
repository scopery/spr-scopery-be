package com.company.scopery.modules.resourcecapacity.taskassignment.domain.model;
import com.company.scopery.modules.resourcecapacity.taskassignment.domain.enums.TaskAssignmentType;
import org.junit.jupiter.api.Test; import java.math.BigDecimal; import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
class TaskResourceAssignmentDomainTest {
    @Test void assignResourceToTask_success() {
        var a = TaskResourceAssignment.create(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                TaskAssignmentType.PRIMARY, new BigDecimal("8"));
        assertThat(a.status().name()).isEqualTo("ACTIVE");
    }
    @Test void assignmentDoesNotGrantAccess() {
        var a = TaskResourceAssignment.create(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                TaskAssignmentType.APPROVER_LABEL_ONLY, null);
        assertThat(a.assignmentType()).isEqualTo(TaskAssignmentType.APPROVER_LABEL_ONLY);
    }
}
