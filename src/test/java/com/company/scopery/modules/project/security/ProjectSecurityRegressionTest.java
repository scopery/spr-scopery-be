package com.company.scopery.modules.project.security;

import com.company.scopery.modules.project.project.http.controller.ProjectController;
import com.company.scopery.modules.project.projectphase.http.controller.ProjectPhaseController;
import com.company.scopery.modules.project.shared.constant.ProjectApiPaths;
import com.company.scopery.modules.project.task.http.controller.TaskController;
import com.company.scopery.modules.project.taskdependency.http.controller.TaskDependencyController;
import com.company.scopery.modules.project.wbs.http.controller.WbsNodeController;
import com.company.scopery.platform.security.SecurityPathPolicy;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Phase 10 §26.8 security regression: project routes stay under /api, never permitAll,
 * and controllers remain thin response wrappers.
 */
class ProjectSecurityRegressionTest {

    @Test
    void allProjectControllers_underApiRequireAuth() {
        List<Class<?>> controllers = List.of(
                ProjectController.class,
                ProjectPhaseController.class,
                WbsNodeController.class,
                TaskController.class,
                TaskDependencyController.class);

        for (Class<?> controller : controllers) {
            assertThat(controller.isAnnotationPresent(RestController.class)).isTrue();
            RequestMapping mapping = controller.getAnnotation(RequestMapping.class);
            assertThat(mapping).isNotNull();
            assertThat(mapping.value()).isNotEmpty();
            String path = mapping.value()[0];
            assertThat(path).startsWith("/api/");
            assertThat(path).doesNotContain("/api/v1");
            assertThat(path).doesNotContain("/api/v2");
        }
    }

    @Test
    void noProjectEndpointPermitAll() {
        assertThat(SecurityPathPolicy.infraPublicPaths())
                .noneMatch(p -> p.contains("project") || p.contains("phase-definition") || p.contains("wbs") || p.contains("task"));
        assertThat(SecurityPathPolicy.publicAuthPostPaths())
                .noneMatch(p -> p.contains("project"));
    }

    @Test
    void noProjectV1Routes() {
        assertThat(ProjectApiPaths.PROJECTS).isEqualTo("/api/projects");
        assertThat(ProjectApiPaths.PROJECT_PHASES).startsWith("/api/projects/");
        assertThat(ProjectApiPaths.WBS_NODES).startsWith("/api/projects/");
        assertThat(ProjectApiPaths.TASKS).startsWith("/api/projects/");
        assertThat(ProjectApiPaths.TASK_DEPENDENCIES).startsWith("/api/projects/");
        assertThat(ProjectApiPaths.PHASE_DEFINITIONS).isEqualTo("/api/phase-definitions");

        assertThat(Arrays.asList(
                ProjectApiPaths.PROJECTS,
                ProjectApiPaths.PROJECT_PHASES,
                ProjectApiPaths.WBS_NODES,
                ProjectApiPaths.TASKS,
                ProjectApiPaths.TASK_DEPENDENCIES,
                ProjectApiPaths.PHASE_DEFINITIONS
        )).noneMatch(SecurityPathPolicy::containsApiVersionPrefix);
    }

    @Test
    void controllersDoNotReturnJpaEntities() {
        List<Class<?>> controllers = List.of(
                ProjectController.class,
                ProjectPhaseController.class,
                WbsNodeController.class,
                TaskController.class,
                TaskDependencyController.class);

        for (Class<?> controller : controllers) {
            Arrays.stream(controller.getDeclaredMethods())
                    .filter(m -> m.getDeclaringClass().equals(controller))
                    .forEach(m -> {
                        Class<?> returnType = m.getReturnType();
                        assertThat(returnType.getName())
                                .as("%s.%s return type", controller.getSimpleName(), m.getName())
                                .doesNotContain("JpaEntity")
                                .doesNotContain(".domain.model.");
                    });
        }
    }
}
