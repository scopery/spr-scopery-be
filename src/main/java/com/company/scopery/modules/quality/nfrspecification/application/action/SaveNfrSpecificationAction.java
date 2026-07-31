package com.company.scopery.modules.quality.nfrspecification.application.action;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.QualityActivityActions;
import com.company.scopery.modules.quality.shared.constant.QualityEntityTypes;
import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import com.company.scopery.modules.quality.nfrspecification.application.command.SaveNfrSpecificationCommand;
import com.company.scopery.modules.quality.nfrspecification.application.response.NfrSpecificationResponse;
import com.company.scopery.modules.quality.nfrspecification.domain.enums.*;
import com.company.scopery.modules.quality.nfrspecification.domain.model.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class SaveNfrSpecificationAction {
    private final NfrSpecificationRepository repo;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;
    public SaveNfrSpecificationAction(NfrSpecificationRepository repo,
            QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.repo = repo; this.authorization = authorization; this.activityLogger = activityLogger;
    }
    @Transactional
    public NfrSpecificationResponse execute(SaveNfrSpecificationCommand c) {
        authorization.requireQualityUpdate(c.projectId());
        var attribute = QualityEnumParser.parseRequired(QualityAttribute.class, c.qualityAttribute(), "qualityAttribute");
        var operator = QualityEnumParser.parseOptional(ComparisonOperator.class, c.comparisonOperator(), "comparisonOperator");
        var saved = repo.findByRequirementId(c.requirementId())
                .map(existing -> repo.save(existing.update(attribute, c.metricName(), operator,
                        c.targetValue(), c.secondaryTargetValue(), c.unit(),
                        c.measurementWindow(), c.environment(), c.verificationFrequency(), c.configurationJson())))
                .orElseGet(() -> repo.save(NfrSpecification.create(c.requirementId(), attribute,
                        c.metricName(), operator, c.targetValue(), c.secondaryTargetValue(),
                        c.unit(), c.measurementWindow(), c.environment(),
                        c.verificationFrequency(), c.configurationJson())));
        activityLogger.logSuccess(QualityEntityTypes.NFR_SPECIFICATION, c.requirementId(),
                QualityActivityActions.NFR_SPECIFICATION_SAVED, "NFR specification saved");
        return NfrSpecificationResponse.from(saved);
    }
}
