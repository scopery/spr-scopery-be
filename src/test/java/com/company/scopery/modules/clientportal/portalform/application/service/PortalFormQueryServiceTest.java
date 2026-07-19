package com.company.scopery.modules.clientportal.portalform.application.service;
import com.company.scopery.modules.clientportal.shared.security.PortalGrantEnforcementService;
import com.company.scopery.modules.configuration.form.domain.model.CustomFormDefinition;
import com.company.scopery.modules.configuration.form.domain.model.CustomFormDefinitionRepository;
import com.company.scopery.modules.configuration.form.domain.model.CustomFormVersionRepository;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortalFormQueryServiceTest {
    @Mock ProjectRepository projects;
    @Mock CustomFormDefinitionRepository forms;
    @Mock CustomFormVersionRepository versions;
    @Mock PortalGrantEnforcementService grantEnforcement;
    @InjectMocks PortalFormQueryService service;

    @Test
    void listPublished_filtersClientFacingWithCurrentVersion() {
        UUID projectId = UUID.randomUUID();
        UUID workspaceId = UUID.randomUUID();
        Project project = mock(Project.class);
        when(project.workspaceId()).thenReturn(workspaceId);
        when(projects.findById(projectId)).thenReturn(Optional.of(project));

        CustomFormDefinition client = CustomFormDefinition.create(workspaceId, projectId, "PROJECT", "F1", "Client form", "CLIENT_PORTAL");
        client = client.withCurrentVersion(UUID.randomUUID());
        CustomFormDefinition internal = CustomFormDefinition.create(workspaceId, projectId, "PROJECT", "F2", "Internal", "INTERNAL");
        internal = internal.withCurrentVersion(UUID.randomUUID());
        when(forms.findByWorkspaceId(workspaceId)).thenReturn(List.of(client, internal));

        var result = service.listPublished(projectId);
        verify(grantEnforcement).requireActiveGrant(projectId);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).formCode()).isEqualTo("F1");
    }
}
