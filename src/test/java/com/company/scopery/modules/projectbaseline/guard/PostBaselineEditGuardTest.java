package com.company.scopery.modules.projectbaseline.guard;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.support.BaselineApplyContext;
import com.company.scopery.modules.project.shared.support.ProjectMutationGuard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PostBaselineEditGuardTest {

    private ProjectRepository projects;
    private ProjectMutationGuard guard;

    @BeforeEach
    void setUp() {
        projects = mock(ProjectRepository.class);
        guard = new ProjectMutationGuard(projects);
    }

    @Test
    void currentBaseline_blocksDirectTaskEstimateUpdate() {
        UUID projectId = UUID.randomUUID();
        Project project = project(projectId, UUID.randomUUID());
        when(projects.findById(projectId)).thenReturn(Optional.of(project));
        assertThatThrownBy(() -> guard.requireMutableProject(projectId))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo("POST_BASELINE_EDIT_BLOCKED"));
    }

    @Test
    void applyContext_allowsMutation() {
        UUID projectId = UUID.randomUUID();
        Project project = project(projectId, UUID.randomUUID());
        when(projects.findById(projectId)).thenReturn(Optional.of(project));
        BaselineApplyContext.run(() ->
                assertThatCode(() -> guard.requireMutableProject(projectId)).doesNotThrowAnyException());
    }

    @Test
    void withoutCurrentBaseline_allowsMutation() {
        UUID projectId = UUID.randomUUID();
        Project project = project(projectId, null);
        when(projects.findById(projectId)).thenReturn(Optional.of(project));
        assertThatCode(() -> guard.requireMutableProject(projectId)).doesNotThrowAnyException();
    }

    private Project project(UUID id, UUID currentBaselineId) {
        return new Project(id, UUID.randomUUID(), UUID.randomUUID(), "P", "Project", null,
                UUID.randomUUID(), "USD", null, null, ProjectStatus.ACTIVE,
                null, null, null, null, null, null, null, null, null, null, null, null,
                currentBaselineId, 0, null, null);
    }
}
