package com.company.scopery.modules.quality.coverage.application.action;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.quality.coverage.application.command.CreateTestCaseCoverageCommand;
import com.company.scopery.modules.quality.coverage.application.response.TestCaseCoverageResponse;
import com.company.scopery.modules.quality.coverage.domain.enums.CoverageType;
import com.company.scopery.modules.quality.coverage.domain.model.TestCaseCoverage;
import com.company.scopery.modules.quality.coverage.domain.model.TestCaseCoverageRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateTestCaseCoverageAction {
    private final ProjectRepository projects;
    private final TestCaseCoverageRepository repo;
    private final QualityAuthorizationService authorization;
    public CreateTestCaseCoverageAction(ProjectRepository projects, TestCaseCoverageRepository repo, QualityAuthorizationService authorization) {
        this.projects=projects; this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public TestCaseCoverageResponse execute(CreateTestCaseCoverageCommand c) {
        authorization.requireTestCreate(c.projectId());
        var project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw QualityExceptions.projectArchived(c.projectId());
        var ct = QualityEnumParser.parseRequired(CoverageType.class, c.coverageType(), "coverageType");
        return TestCaseCoverageResponse.from(repo.save(TestCaseCoverage.create(project.id(), c.testCaseId(), c.targetType().trim(), c.targetId(), ct)));
    }
}
