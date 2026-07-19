package com.company.scopery.modules.project.security;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.project.project.application.action.ActivateProjectAction;
import com.company.scopery.modules.project.project.application.action.ArchiveProjectAction;
import com.company.scopery.modules.project.project.application.action.CompleteProjectAction;
import com.company.scopery.modules.project.project.application.action.CreateProjectAction;
import com.company.scopery.modules.project.project.application.action.HoldProjectAction;
import com.company.scopery.modules.project.project.application.action.UpdateProjectAction;
import com.company.scopery.modules.project.project.application.service.ProjectQueryService;
import com.company.scopery.modules.project.project.http.controller.ProjectController;
import com.company.scopery.modules.project.shared.constant.ProjectApiPaths;
import com.company.scopery.modules.reporting.exportjob.application.action.CancelReportExportAction;
import com.company.scopery.modules.reporting.exportjob.application.service.ReportExportQueryService;
import com.company.scopery.modules.reporting.exportjob.http.controller.ReportExportController;
import com.company.scopery.modules.reporting.shared.constant.ReportingApiPaths;
import com.company.scopery.modules.reporting.shared.error.ReportingExceptions;
import com.company.scopery.platform.config.SecurityConfig;
import com.company.scopery.platform.config.WebMvcConfig;
import com.company.scopery.platform.security.AiAgentSecurityInterceptor;
import com.company.scopery.platform.security.CookieUtil;
import com.company.scopery.platform.security.JwtAuthFilter;
import com.company.scopery.platform.security.JwtService;
import com.company.scopery.platform.security.support.SecurityWebMvcTestConfig;
import com.company.scopery.platform.web.GlobalExceptionHandler;
import com.company.scopery.platform.web.RateLimitFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ProjectController.class, ReportExportController.class}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                RateLimitFilter.class, WebMvcConfig.class, AiAgentSecurityInterceptor.class,
                com.company.scopery.platform.web.idempotency.IdempotencyKeyFilter.class
        })
})
@Import({
        SecurityConfig.class,
        JwtAuthFilter.class,
        SecurityWebMvcTestConfig.class,
        com.company.scopery.platform.security.CorsProperties.class,
        GlobalExceptionHandler.class
})
class ProjectIdorSecurityIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private ProjectQueryService projectQueryService;
    @MockBean private CreateProjectAction createProjectAction;
    @MockBean private UpdateProjectAction updateProjectAction;
    @MockBean private ActivateProjectAction activateProjectAction;
    @MockBean private HoldProjectAction holdProjectAction;
    @MockBean private CompleteProjectAction completeProjectAction;
    @MockBean private ArchiveProjectAction archiveProjectAction;
    @MockBean private ReportExportQueryService reportExportQueryService;
    @MockBean private CancelReportExportAction cancelReportExportAction;

    @BeforeEach
    void authenticate() {
        when(jwtService.isTokenValid("valid-access")).thenReturn(true);
        when(jwtService.extractUsername("valid-access")).thenReturn("admin");
    }

    @Test
    void getProject_crossWorkspace_returns403() throws Exception {
        UUID foreignProjectId = UUID.randomUUID();
        when(projectQueryService.getProject(foreignProjectId))
                .thenThrow(new AppException(IamErrorCatalog.IAM_ACCESS_DENIED, "Access denied"));

        mockMvc.perform(get(ProjectApiPaths.PROJECTS + "/" + foreignProjectId)
                        .cookie(new jakarta.servlet.http.Cookie(CookieUtil.ACCESS_TOKEN_COOKIE, "valid-access")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value(IamErrorCatalog.IAM_ACCESS_DENIED.code()))
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void downloadExport_otherProject_returns403() throws Exception {
        UUID exportJobId = UUID.randomUUID();
        when(reportExportQueryService.requireDownloadable(exportJobId))
                .thenThrow(ReportingExceptions.accessDenied());

        mockMvc.perform(get(ReportingApiPaths.EXPORTS + "/" + exportJobId + "/download")
                        .cookie(new jakarta.servlet.http.Cookie(CookieUtil.ACCESS_TOKEN_COOKIE, "valid-access")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("REPORT_ACCESS_DENIED"))
                .andExpect(jsonPath("$.status").value(403));
    }
}
