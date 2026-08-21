package com.company.scopery.modules.quality.testcase.application.action;

import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.testcase.application.command.DeleteTestCaseCommand;
import com.company.scopery.modules.quality.testcase.domain.model.TestCaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeleteTestCaseAction {

    private final TestCaseRepository repo;
    private final QualityAuthorizationService authorization;

    public DeleteTestCaseAction(TestCaseRepository repo, QualityAuthorizationService authorization) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional
    public void execute(DeleteTestCaseCommand c) {
        authorization.requireTestUpdate(c.projectId());
        repo.findByIdAndProjectId(c.testCaseId(), c.projectId())
                .orElseThrow(() -> QualityExceptions.testCaseNotFound(c.testCaseId()));
        repo.delete(c.testCaseId(), c.projectId());
    }
}
