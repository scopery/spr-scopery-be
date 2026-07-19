package com.company.scopery.modules.quote.quoteversion.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.quote.calculation.QuoteCalculationService;
import com.company.scopery.modules.quote.calculation.QuoteRecalculator;
import com.company.scopery.modules.quote.quoteline.domain.model.QuoteLineRepository;
import com.company.scopery.modules.quote.quoteversion.application.command.UpdateQuoteVersionCommand;
import com.company.scopery.modules.quote.quoteversion.application.response.QuoteVersionResponse;
import com.company.scopery.modules.quote.quoteversion.domain.enums.CostBaseMethod;
import com.company.scopery.modules.quote.quoteversion.domain.enums.DiscountMethod;
import com.company.scopery.modules.quote.quoteversion.domain.enums.PricingMethod;
import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersion;
import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersionRepository;
import com.company.scopery.modules.quote.shared.activity.QuoteActivityLogger;
import com.company.scopery.modules.quote.shared.authorization.QuoteAuthorizationService;
import com.company.scopery.modules.quote.shared.constant.QuoteActivityActions;
import com.company.scopery.modules.quote.shared.constant.QuoteEntityTypes;
import com.company.scopery.modules.quote.shared.error.QuoteErrorCatalog;
import com.company.scopery.modules.quote.shared.error.QuoteExceptions;
import com.company.scopery.modules.quote.shared.support.QuotePlatformPublisher;
import com.company.scopery.modules.quote.shared.util.QuoteEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class UpdateQuoteVersionAction {
    private final ProjectRepository projects;
    private final QuoteVersionRepository versions;
    private final QuoteLineRepository lines;
    private final QuoteRecalculator recalculator;
    private final QuoteCalculationService calculationService;
    private final QuoteAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final QuotePlatformPublisher publisher;
    private final QuoteActivityLogger activityLogger;

    public UpdateQuoteVersionAction(ProjectRepository projects, QuoteVersionRepository versions,
                                    QuoteLineRepository lines, QuoteRecalculator recalculator,
                                    QuoteCalculationService calculationService,
                                    QuoteAuthorizationService authorization,
                                    CurrentUserAuthorizationService currentUser,
                                    QuotePlatformPublisher publisher, QuoteActivityLogger activityLogger) {
        this.projects = projects;
        this.versions = versions;
        this.lines = lines;
        this.recalculator = recalculator;
        this.calculationService = calculationService;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public QuoteVersionResponse execute(UpdateQuoteVersionCommand command) {
        authorization.requireVersionUpdate(command.projectId());
        UUID actorId = currentUser.resolveCurrentUser().id();
        Project project = projects.findById(command.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(command.projectId()));
        QuoteVersion version = versions.findByIdAndQuoteId(command.versionId(), command.quoteId())
                .orElseThrow(() -> QuoteExceptions.versionNotFound(command.versionId()));
        if (!version.projectId().equals(command.projectId()) || !version.isEditable()) {
            throw QuoteExceptions.versionNotDraft(version.id());
        }

        PricingMethod pricing = command.pricingMethod() == null || command.pricingMethod().isBlank()
                ? version.pricingMethod()
                : QuoteEnumParser.parseRequired(PricingMethod.class, command.pricingMethod(),
                QuoteErrorCatalog.QUOTE_VERSION_INVALID_STATUS.code(), "pricingMethod");
        CostBaseMethod costBase = command.costBaseMethod() == null || command.costBaseMethod().isBlank()
                ? version.costBaseMethod()
                : QuoteEnumParser.parseRequired(CostBaseMethod.class, command.costBaseMethod(),
                QuoteErrorCatalog.QUOTE_VERSION_INVALID_STATUS.code(), "costBaseMethod");
        DiscountMethod discount = command.discountMethod() == null || command.discountMethod().isBlank()
                ? version.discountMethod()
                : QuoteEnumParser.parseRequired(DiscountMethod.class, command.discountMethod(),
                QuoteErrorCatalog.QUOTE_DISCOUNT_INVALID.code(), "discountMethod");

        BigDecimal discountPercent = command.discountPercent() != null ? command.discountPercent() : version.discountPercent();
        BigDecimal discountAmount = command.discountAmount() != null ? command.discountAmount() : version.discountAmount();
        String discountReason = command.discountReason() != null ? command.discountReason() : version.discountReason();

        boolean discountChanged = discount != version.discountMethod()
                || !eq(discountPercent, version.discountPercent())
                || !eq(discountAmount, version.discountAmount());
        if (discountChanged) {
            authorization.requireDiscountUpdate(command.projectId());
            BigDecimal subtotal = lines.findByQuoteVersionId(version.id()).stream()
                    .map(l -> l.amount()).reduce(BigDecimal.ZERO, BigDecimal::add);
            calculationService.validateDiscountReason(discount, discountPercent, discountAmount, subtotal, discountReason);
            if (calculationService.requiresDiscountApproval(discount, discountPercent, discountAmount, subtotal)) {
                authorization.requireDiscountApprove(command.projectId());
            }
        }

        version = versions.save(version.updateDraft(
                command.targetMarginPercent() != null ? command.targetMarginPercent() : version.targetMarginPercent(),
                pricing, costBase, discount, discountPercent, discountAmount, discountReason,
                command.validUntil() != null ? command.validUntil() : version.validUntil(),
                command.proposalTitle() != null ? command.proposalTitle() : version.proposalTitle(),
                command.proposalNotes() != null ? command.proposalNotes() : version.proposalNotes()));
        recalculator.recalculateAndSave(version);
        publisher.enqueueVersion(version, "QUOTE_VERSION_UPDATED");
        if (discountChanged) {
            publisher.enqueueVersion(version, "QUOTE_DISCOUNT_UPDATED");
            publisher.audit(AuditEventType.QUOTE_DISCOUNT_CHANGED, actorId, project, version, "Quote discount changed");
            activityLogger.logSuccess(QuoteEntityTypes.QUOTE_VERSION, version.id(),
                    QuoteActivityActions.QUOTE_DISCOUNT_UPDATED, "Quote discount updated");
        }
        activityLogger.logSuccess(QuoteEntityTypes.QUOTE_VERSION, version.id(),
                QuoteActivityActions.QUOTE_VERSION_UPDATED, "Quote version updated");
        return QuoteVersionResponse.from(version);
    }

    private static boolean eq(BigDecimal a, BigDecimal b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.compareTo(b) == 0;
    }
}
