package com.company.scopery.modules.quote.quoteversion.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.quote.calculation.FinanceSnapshotBuilder;
import com.company.scopery.modules.quote.calculation.QuoteCalculationService;
import com.company.scopery.modules.quote.calculation.QuoteRecalculator;
import com.company.scopery.modules.quote.calculation.TargetMarginSolver;
import com.company.scopery.modules.quote.quoteversion.application.response.SolveTargetMarginResponse;
import com.company.scopery.modules.quote.quoteversion.domain.enums.PricingMethod;
import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersion;
import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersionRepository;
import com.company.scopery.modules.quote.shared.activity.QuoteActivityLogger;
import com.company.scopery.modules.quote.shared.authorization.QuoteAuthorizationService;
import com.company.scopery.modules.quote.shared.constant.QuoteActivityActions;
import com.company.scopery.modules.quote.shared.constant.QuoteEntityTypes;
import com.company.scopery.modules.quote.shared.error.QuoteExceptions;
import com.company.scopery.modules.quote.shared.support.QuotePlatformPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class SolveTargetMarginAction {
    private final ProjectRepository projects;
    private final QuoteVersionRepository versions;
    private final TargetMarginSolver solver;
    private final FinanceSnapshotBuilder snapshotBuilder;
    private final QuoteCalculationService calculationService;
    private final QuoteRecalculator recalculator;
    private final QuoteAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final QuotePlatformPublisher publisher;
    private final QuoteActivityLogger activityLogger;

    public SolveTargetMarginAction(ProjectRepository projects, QuoteVersionRepository versions,
                                   TargetMarginSolver solver, FinanceSnapshotBuilder snapshotBuilder,
                                   QuoteCalculationService calculationService, QuoteRecalculator recalculator,
                                   QuoteAuthorizationService authorization,
                                   CurrentUserAuthorizationService currentUser,
                                   QuotePlatformPublisher publisher, QuoteActivityLogger activityLogger) {
        this.projects = projects;
        this.versions = versions;
        this.solver = solver;
        this.snapshotBuilder = snapshotBuilder;
        this.calculationService = calculationService;
        this.recalculator = recalculator;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public SolveTargetMarginResponse execute(UUID projectId, UUID quoteId, UUID versionId,
                                             BigDecimal costBase, BigDecimal targetMarginPercent,
                                             String currencyCode) {
        authorization.requireSolverUse(projectId);
        UUID actorId = currentUser.resolveCurrentUser().id();
        Project project = projects.findById(projectId)
                .orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        QuoteVersion version = versions.findByIdAndQuoteId(versionId, quoteId)
                .orElseThrow(() -> QuoteExceptions.versionNotFound(versionId));
        if (!version.isEditable()) {
            throw QuoteExceptions.versionNotDraft(version.id());
        }
        var finance = snapshotBuilder.parseAmounts(version.financeSnapshotJson());
        BigDecimal resolvedCost = costBase != null ? costBase : calculationService.resolveCostBase(version, finance);
        BigDecimal required = solver.solveRequiredContractValue(resolvedCost, targetMarginPercent);
        version = versions.save(version.updateDraft(
                targetMarginPercent, PricingMethod.TARGET_MARGIN_SOLVER, version.costBaseMethod(),
                version.discountMethod(), version.discountPercent(), version.discountAmount(),
                version.discountReason(), version.validUntil(), version.proposalTitle(), version.proposalNotes()));
        recalculator.recalculateAndSave(version);
        publisher.enqueueVersion(version, "QUOTE_TARGET_MARGIN_SOLVED");
        publisher.audit(AuditEventType.QUOTE_TARGET_MARGIN_SOLVED, actorId, project, version, "Target margin solved");
        activityLogger.logSuccess(QuoteEntityTypes.QUOTE_VERSION, version.id(),
                QuoteActivityActions.QUOTE_TARGET_MARGIN_SOLVED, "Target margin solved");
        String currency = currencyCode == null || currencyCode.isBlank() ? version.currencyCode() : currencyCode;
        return new SolveTargetMarginResponse(resolvedCost, targetMarginPercent, required, currency);
    }
}
