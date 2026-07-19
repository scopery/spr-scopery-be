package com.company.scopery.modules.scope.scopepackage.application.action;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.quote.quoteline.domain.model.QuoteLineRepository;
import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersionRepository;
import com.company.scopery.modules.scope.scopeitem.domain.model.ScopeItem;
import com.company.scopery.modules.scope.scopeitem.domain.model.ScopeItemRepository;
import com.company.scopery.modules.scope.scopepackage.application.command.ImportScopePackageFromQuoteCommand;
import com.company.scopery.modules.scope.scopepackage.application.response.ScopePackageResponse;
import com.company.scopery.modules.scope.scopepackage.domain.model.ScopePackage;
import com.company.scopery.modules.scope.scopepackage.domain.model.ScopePackageRepository;
import com.company.scopery.modules.scope.shared.activity.ScopeActivityLogger;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.constant.ScopeActivityActions;
import com.company.scopery.modules.scope.shared.constant.ScopeEntityTypes;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class ImportScopePackageFromQuoteAction {
    private final ProjectRepository projects;
    private final QuoteVersionRepository quoteVersions;
    private final QuoteLineRepository quoteLines;
    private final ScopePackageRepository packages;
    private final ScopeItemRepository items;
    private final ScopeAuthorizationService authorization;
    private final ScopeActivityLogger activityLogger;
    public ImportScopePackageFromQuoteAction(ProjectRepository projects, QuoteVersionRepository quoteVersions,
                                             QuoteLineRepository quoteLines, ScopePackageRepository packages,
                                             ScopeItemRepository items, ScopeAuthorizationService authorization,
                                             ScopeActivityLogger activityLogger) {
        this.projects = projects; this.quoteVersions = quoteVersions; this.quoteLines = quoteLines;
        this.packages = packages; this.items = items; this.authorization = authorization; this.activityLogger = activityLogger;
    }
    @Transactional
    public ScopePackageResponse execute(ImportScopePackageFromQuoteCommand command) {
        authorization.requireScopeCreate(command.projectId());
        Project project = projects.findById(command.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(command.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) {
            throw ScopeExceptions.projectArchived(command.projectId());
        }
        var quoteVersion = quoteVersions.findById(command.quoteVersionId())
                .orElseThrow(() -> ScopeExceptions.quoteVersionNotFound(command.quoteVersionId()));
        if (!quoteVersion.projectId().equals(command.projectId())) {
            throw ScopeExceptions.quoteVersionProjectMismatch(command.quoteVersionId(), command.projectId());
        }
        String code = command.code() == null || command.code().isBlank()
                ? "SCOPE-QV-" + quoteVersion.versionNumber() : command.code().trim();
        if (packages.existsByProjectIdAndCode(command.projectId(), code)) {
            throw ScopeExceptions.codeExists(code);
        }
        String name = command.name() == null || command.name().isBlank()
                ? (quoteVersion.proposalTitle() != null ? quoteVersion.proposalTitle()
                : quoteVersion.titleSnapshot() != null ? quoteVersion.titleSnapshot() : "Scope from quote v" + quoteVersion.versionNumber())
                : command.name().trim();
        ScopePackage pkg = packages.save(ScopePackage.importFromQuote(
                project.id(), project.workspaceId(), quoteVersion.id(), code, name, null, MDC.get("traceId")));
        var lines = quoteLines.findByQuoteVersionId(quoteVersion.id());
        int order = 0;
        for (var line : lines) {
            items.save(ScopeItem.fromQuoteLine(pkg.id(), project.id(), project.workspaceId(),
                    line.id(), line.name(), line.description(), order++));
        }
        activityLogger.logSuccess(ScopeEntityTypes.PACKAGE, pkg.id(), ScopeActivityActions.PACKAGE_IMPORTED_FROM_QUOTE,
                "Scope package imported from quote version " + quoteVersion.versionNumber());
        return ScopePackageResponse.from(pkg);
    }
}
