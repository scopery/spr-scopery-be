package com.company.scopery.modules.quote.quote.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.projectfinance.scenario.domain.enums.FinanceScenarioStatus;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenario;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenarioRepository;
import com.company.scopery.modules.quote.quote.application.command.CreateQuoteCommand;
import com.company.scopery.modules.quote.quote.application.response.QuoteResponse;
import com.company.scopery.modules.quote.quote.domain.model.Quote;
import com.company.scopery.modules.quote.quote.domain.model.QuoteRepository;
import com.company.scopery.modules.quote.shared.activity.QuoteActivityLogger;
import com.company.scopery.modules.quote.shared.authorization.QuoteAuthorizationService;
import com.company.scopery.modules.quote.shared.constant.QuoteActivityActions;
import com.company.scopery.modules.quote.shared.constant.QuoteEntityTypes;
import com.company.scopery.modules.quote.shared.error.QuoteExceptions;
import com.company.scopery.modules.quote.shared.support.QuotePlatformPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateQuoteAction {

    private final ProjectRepository projects;
    private final ProjectFinanceScenarioRepository scenarios;
    private final QuoteRepository quotes;
    private final QuoteAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final QuotePlatformPublisher publisher;
    private final QuoteActivityLogger activityLogger;

    public CreateQuoteAction(ProjectRepository projects,
                             ProjectFinanceScenarioRepository scenarios,
                             QuoteRepository quotes,
                             QuoteAuthorizationService authorization,
                             CurrentUserAuthorizationService currentUser,
                             QuotePlatformPublisher publisher,
                             QuoteActivityLogger activityLogger) {
        this.projects = projects;
        this.scenarios = scenarios;
        this.quotes = quotes;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public QuoteResponse execute(CreateQuoteCommand command) {
        authorization.requireCreate(command.projectId());
        currentUser.resolveCurrentUser();
        Project project = projects.findById(command.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(command.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) {
            throw QuoteExceptions.projectArchived(project.id());
        }

        ProjectFinanceScenario scenario = scenarios.findById(command.sourceFinanceScenarioId())
                .orElseThrow(() -> QuoteExceptions.sourceFinanceNotFound(command.sourceFinanceScenarioId()));
        if (!scenario.projectId().equals(project.id())) {
            throw QuoteExceptions.sourceFinanceProjectMismatch(scenario.id(), project.id());
        }
        if (scenario.status() != FinanceScenarioStatus.APPROVED && !scenario.currentFlag()) {
            throw QuoteExceptions.sourceFinanceNotApproved(scenario.id());
        }

        if (quotes.existsByProjectIdAndCode(project.id(), command.code().trim())) {
            throw QuoteExceptions.codeAlreadyExists(command.code());
        }

        Quote quote = Quote.create(
                project.id(), project.workspaceId(), scenario.id(),
                command.code().trim(), command.title().trim(), command.description(),
                command.clientName(), command.clientCompany(), command.clientEmail(),
                command.clientContactName(), command.clientReference());
        quote = quotes.save(quote);

        publisher.enqueueQuote(quote, "QUOTE_CREATED");
        activityLogger.logSuccess(QuoteEntityTypes.QUOTE, quote.id(),
                QuoteActivityActions.QUOTE_CREATED, "Quote created: " + quote.code());
        return QuoteResponse.from(quote);
    }
}
