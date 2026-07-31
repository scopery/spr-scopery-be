package com.company.scopery.modules.quality.defect.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.quality.defect.application.command.CreateDefectCommand;
import com.company.scopery.modules.quality.defect.application.response.DefectResponse;
import com.company.scopery.modules.quality.defect.domain.enums.*;
import com.company.scopery.modules.quality.defect.domain.model.Defect;
import com.company.scopery.modules.quality.defect.domain.model.DefectRepository;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.QualityActivityActions;
import com.company.scopery.modules.quality.shared.constant.QualityEntityTypes;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import com.company.scopery.modules.quality.testrun.domain.model.TestCaseResult;
import com.company.scopery.modules.quality.testrun.domain.model.TestCaseResultRepository;
import com.company.scopery.modules.quality.verificationresult.domain.model.VerificationCaseResult;
import com.company.scopery.modules.quality.verificationresult.domain.model.VerificationCaseResultRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateDefectAction {
    private final ProjectRepository projects;
    private final DefectRepository repo;
    private final TestCaseResultRepository testCaseResults;
    private final VerificationCaseResultRepository verificationResults;
    private final QualityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final QualityActivityLogger activityLogger;
    public CreateDefectAction(ProjectRepository projects, DefectRepository repo,
                              TestCaseResultRepository testCaseResults,
                              VerificationCaseResultRepository verificationResults,
                              QualityAuthorizationService authorization,
                              CurrentUserAuthorizationService currentUser, QualityActivityLogger activityLogger) {
        this.projects = projects; this.repo = repo;
        this.testCaseResults = testCaseResults; this.verificationResults = verificationResults;
        this.authorization = authorization; this.currentUser = currentUser; this.activityLogger = activityLogger;
    }
    @Transactional
    public DefectResponse execute(CreateDefectCommand c) {
        authorization.requireDefectCreate(c.projectId());
        Project project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw QualityExceptions.projectArchived(c.projectId());
        var actor = currentUser.resolveCurrentUser();

        UUID sourceTestCaseResultId = null, sourceVerificationResultId = null;
        UUID sourceTestRunId = null, sourceTestCaseId = null, sourceVerificationCaseId = null;

        if (c.sourceTestCaseResultId() != null) {
            TestCaseResult result = testCaseResults.findByIdAndProjectId(c.sourceTestCaseResultId(), c.projectId())
                    .orElseThrow(() -> QualityExceptions.defectSourceResultNotFound(c.sourceTestCaseResultId()));
            sourceTestCaseResultId = result.id();
            sourceTestRunId = result.testRunId();
            sourceTestCaseId = result.testCaseId();
        } else if (c.sourceVerificationResultId() != null) {
            VerificationCaseResult result = verificationResults.findByIdAndProjectId(c.sourceVerificationResultId(), c.projectId())
                    .orElseThrow(() -> QualityExceptions.defectSourceResultNotFound(c.sourceVerificationResultId()));
            sourceVerificationResultId = result.id();
            sourceTestRunId = result.testRunId();
            sourceVerificationCaseId = result.verificationCaseId();
        }

        Defect saved = repo.save(Defect.create(project.id(), project.workspaceId(), c.code(), c.title().trim(), c.description(),
                QualityEnumParser.parseRequired(DefectCategory.class, c.category(), "category"),
                QualityEnumParser.parseRequired(DefectSeverity.class, c.severity(), "severity"),
                QualityEnumParser.parseRequired(DefectPriority.class, c.priority(), "priority"),
                actor.id(), c.reproductionSteps(), c.expectedResult(), c.actualResult(),
                sourceTestCaseResultId, sourceVerificationResultId, sourceTestRunId, sourceTestCaseId, sourceVerificationCaseId));

        if (sourceTestCaseResultId != null) {
            final UUID resultId = sourceTestCaseResultId;
            testCaseResults.findByIdAndProjectId(resultId, c.projectId())
                    .ifPresent(r -> testCaseResults.save(r.withDefect(saved.id())));
        } else if (sourceVerificationResultId != null) {
            final UUID resultId = sourceVerificationResultId;
            verificationResults.findByIdAndProjectId(resultId, c.projectId())
                    .ifPresent(r -> verificationResults.save(r.withDefect(saved.id())));
        }

        activityLogger.logSuccess(QualityEntityTypes.DEFECT, saved.id(), QualityActivityActions.DEFECT_CREATED, "Defect created");
        return DefectResponse.from(saved);
    }
}
