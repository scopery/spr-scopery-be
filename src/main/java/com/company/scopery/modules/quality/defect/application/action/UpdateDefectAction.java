package com.company.scopery.modules.quality.defect.application.action;

import com.company.scopery.modules.quality.defect.application.command.UpdateDefectCommand;
import com.company.scopery.modules.quality.defect.application.response.DefectResponse;
import com.company.scopery.modules.quality.defect.domain.enums.*;
import com.company.scopery.modules.quality.defect.domain.model.DefectRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateDefectAction {
    private final DefectRepository repo;
    private final QualityAuthorizationService authorization;

    public UpdateDefectAction(DefectRepository repo, QualityAuthorizationService authorization) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional
    public DefectResponse execute(UpdateDefectCommand c) {
        authorization.requireDefectUpdate(c.projectId());
        var d = repo.findByIdAndProjectId(c.defectId(), c.projectId())
                .orElseThrow(() -> QualityExceptions.defectNotFound(c.defectId()));
        return DefectResponse.from(repo.save(d.update(
                c.title().trim(),
                c.description(),
                c.category() == null ? d.category() : QualityEnumParser.parseRequired(DefectCategory.class, c.category(), "category"),
                c.severity() == null ? d.severity() : QualityEnumParser.parseRequired(DefectSeverity.class, c.severity(), "severity"),
                c.priority() == null ? d.priority() : QualityEnumParser.parseRequired(DefectPriority.class, c.priority(), "priority"),
                c.reproductionSteps(),
                c.expectedResult(),
                c.actualResult(),
                c.environmentNotes())));
    }
}
