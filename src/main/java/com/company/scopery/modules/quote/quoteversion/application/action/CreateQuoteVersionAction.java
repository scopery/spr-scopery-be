package com.company.scopery.modules.quote.quoteversion.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.projectfinance.phasefinance.domain.model.ProjectPhaseFinanceRepository;
import com.company.scopery.modules.projectfinance.scenario.domain.enums.FinanceScenarioStatus;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenario;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenarioRepository;
import com.company.scopery.modules.projectfinance.summary.domain.model.ProjectFinanceSummaryRepository;
import com.company.scopery.modules.quote.calculation.FinanceSnapshotBuilder;
import com.company.scopery.modules.quote.calculation.QuoteCalculationService;
import com.company.scopery.modules.quote.calculation.QuoteRecalculator;
import com.company.scopery.modules.quote.quote.domain.model.Quote;
import com.company.scopery.modules.quote.quote.domain.model.QuoteRepository;
import com.company.scopery.modules.quote.quoteline.domain.model.QuoteLine;
import com.company.scopery.modules.quote.quoteline.domain.model.QuoteLineRepository;
import com.company.scopery.modules.quote.quoteversion.application.command.CreateQuoteVersionCommand;
import com.company.scopery.modules.quote.quoteversion.application.response.QuoteVersionResponse;
import com.company.scopery.modules.quote.quoteversion.domain.enums.CostBaseMethod;
import com.company.scopery.modules.quote.quoteversion.domain.enums.DiscountMethod;
import com.company.scopery.modules.quote.quoteversion.domain.enums.GenerateLinesFrom;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class CreateQuoteVersionAction {

    private final ProjectRepository projects;
    private final QuoteRepository quotes;
    private final QuoteVersionRepository versions;
    private final ProjectFinanceScenarioRepository scenarios;
    private final ProjectFinanceSummaryRepository financeSummaries;
    private final ProjectPhaseFinanceRepository phaseFinances;
    private final QuoteLineRepository lines;
    private final FinanceSnapshotBuilder snapshotBuilder;
    private final QuoteRecalculator recalculator;
    private final QuoteCalculationService calculationService;
    private final QuoteAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final QuotePlatformPublisher publisher;
    private final QuoteActivityLogger activityLogger;
    private final ObjectMapper objectMapper;

    public CreateQuoteVersionAction(ProjectRepository projects,
                                    QuoteRepository quotes,
                                    QuoteVersionRepository versions,
                                    ProjectFinanceScenarioRepository scenarios,
                                    ProjectFinanceSummaryRepository financeSummaries,
                                    ProjectPhaseFinanceRepository phaseFinances,
                                    QuoteLineRepository lines,
                                    FinanceSnapshotBuilder snapshotBuilder,
                                    QuoteRecalculator recalculator,
                                    QuoteCalculationService calculationService,
                                    QuoteAuthorizationService authorization,
                                    CurrentUserAuthorizationService currentUser,
                                    QuotePlatformPublisher publisher,
                                    QuoteActivityLogger activityLogger,
                                    ObjectMapper objectMapper) {
        this.projects = projects;
        this.quotes = quotes;
        this.versions = versions;
        this.scenarios = scenarios;
        this.financeSummaries = financeSummaries;
        this.phaseFinances = phaseFinances;
        this.lines = lines;
        this.snapshotBuilder = snapshotBuilder;
        this.recalculator = recalculator;
        this.calculationService = calculationService;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public QuoteVersionResponse execute(CreateQuoteVersionCommand command) {
        authorization.requireVersionCreate(command.projectId());
        UUID actorId = currentUser.resolveCurrentUser().id();
        Project project = projects.findById(command.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(command.projectId()));
        Quote quote = quotes.findByIdAndProjectId(command.quoteId(), command.projectId())
                .orElseThrow(() -> QuoteExceptions.quoteNotFound(command.quoteId()));
        if (quote.isArchived()) {
            throw QuoteExceptions.versionImmutable(quote.id());
        }

        ProjectFinanceScenario scenario = scenarios.findById(command.financeScenarioId())
                .orElseThrow(() -> QuoteExceptions.sourceFinanceNotFound(command.financeScenarioId()));
        if (!scenario.projectId().equals(project.id())) {
            throw QuoteExceptions.sourceFinanceProjectMismatch(scenario.id(), project.id());
        }
        if (scenario.status() != FinanceScenarioStatus.APPROVED && !scenario.currentFlag()) {
            throw QuoteExceptions.sourceFinanceNotApproved(scenario.id());
        }

        PricingMethod pricingMethod = QuoteEnumParser.parseRequired(PricingMethod.class,
                command.pricingMethod(), QuoteErrorCatalog.QUOTE_VERSION_INVALID_STATUS.code(), "pricingMethod");
        CostBaseMethod costBaseMethod = command.costBaseMethod() == null || command.costBaseMethod().isBlank()
                ? CostBaseMethod.BUDGET_OF_COSTS
                : QuoteEnumParser.parseRequired(CostBaseMethod.class, command.costBaseMethod(),
                QuoteErrorCatalog.QUOTE_VERSION_INVALID_STATUS.code(), "costBaseMethod");
        GenerateLinesFrom generateFrom = command.generateLinesFrom() == null || command.generateLinesFrom().isBlank()
                ? GenerateLinesFrom.PHASE_FINANCE
                : QuoteEnumParser.parseRequired(GenerateLinesFrom.class, command.generateLinesFrom(),
                QuoteErrorCatalog.QUOTE_VERSION_INVALID_STATUS.code(), "generateLinesFrom");
        DiscountMethod discountMethod = command.discountMethod() == null || command.discountMethod().isBlank()
                ? DiscountMethod.NONE
                : QuoteEnumParser.parseRequired(DiscountMethod.class, command.discountMethod(),
                QuoteErrorCatalog.QUOTE_DISCOUNT_INVALID.code(), "discountMethod");

        var summary = financeSummaries.findByScenarioId(scenario.id())
                .orElseThrow(() -> QuoteExceptions.financeSnapshotInvalid("Finance summary missing"));
        var phases = phaseFinances.findByScenarioId(scenario.id());
        String financeSnapshot = snapshotBuilder.buildSnapshotJson(scenario, summary, phases);
        String clientSnapshot = buildClientSnapshot(quote);

        if (discountMethod != DiscountMethod.NONE) {
            authorization.requireDiscountUpdate(command.projectId());
            BigDecimal subtotalHint = summary.plannedRevenue() == null ? BigDecimal.ZERO : summary.plannedRevenue();
            calculationService.validateDiscountReason(discountMethod, command.discountPercent(),
                    command.discountAmount(), subtotalHint, command.discountReason());
            if (calculationService.requiresDiscountApproval(discountMethod, command.discountPercent(),
                    command.discountAmount(), subtotalHint)) {
                authorization.requireDiscountApprove(command.projectId());
            }
        }

        int versionNumber = versions.nextVersionNumber(quote.id());
        QuoteVersion version = QuoteVersion.create(
                quote.id(), project.id(), scenario.id(), versionNumber, quote.title(),
                clientSnapshot, financeSnapshot, scenario.currencyCode(),
                command.targetMarginPercent(), pricingMethod, costBaseMethod,
                discountMethod, command.discountPercent(), command.discountAmount(),
                command.discountReason(), command.validUntil(),
                command.proposalTitle(), command.proposalNotes());
        version = versions.save(version);

        if (generateFrom == GenerateLinesFrom.PHASE_FINANCE) {
            List<QuoteLine> generated = snapshotBuilder.generatePhaseLines(
                    version.id(), project.id(), version.currencyCode(),
                    pricingMethod, command.targetMarginPercent(), financeSnapshot);
            for (QuoteLine line : generated) {
                lines.save(line);
            }
        }

        recalculator.recalculateAndSave(version);
        publisher.enqueueVersion(version, "QUOTE_VERSION_CREATED");
        publisher.audit(AuditEventType.QUOTE_VERSION_CREATED_FROM_FINANCE, actorId, project, version,
                "Quote version created from finance scenario");
        activityLogger.logSuccess(QuoteEntityTypes.QUOTE_VERSION, version.id(),
                QuoteActivityActions.QUOTE_VERSION_CREATED, "Quote version created");
        return QuoteVersionResponse.from(version);
    }

    private String buildClientSnapshot(Quote quote) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("clientName", quote.clientName());
        node.put("clientCompany", quote.clientCompany());
        node.put("clientEmail", quote.clientEmail());
        node.put("clientContactName", quote.clientContactName());
        node.put("clientReference", quote.clientReference());
        try {
            return objectMapper.writeValueAsString(node);
        } catch (Exception ex) {
            return "{}";
        }
    }
}
