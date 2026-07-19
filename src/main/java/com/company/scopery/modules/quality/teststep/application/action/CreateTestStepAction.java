package com.company.scopery.modules.quality.teststep.application.action;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.quality.teststep.application.command.CreateTestStepCommand;
import com.company.scopery.modules.quality.teststep.application.response.TestStepResponse;
import com.company.scopery.modules.quality.teststep.domain.model.TestStep;
import com.company.scopery.modules.quality.teststep.domain.model.TestStepRepository;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.QualityActivityActions;
import com.company.scopery.modules.quality.shared.constant.QualityEntityTypes;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateTestStepAction {
    private final ProjectRepository projects;
    private final TestStepRepository repo;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;
    public CreateTestStepAction(ProjectRepository projects, TestStepRepository repo,
            QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.projects=projects; this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public TestStepResponse execute(CreateTestStepCommand c) {
        authorization.requireTestCreate(c.projectId());
        Project project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw QualityExceptions.projectArchived(c.projectId());
        TestStep saved = repo.save(TestStep.create(project.id(), c.testCaseId(), c.stepOrder(), c.actionText().trim(), c.expectedResult().trim(), c.dataNotes()));
        
        return TestStepResponse.from(saved);
    }
}
