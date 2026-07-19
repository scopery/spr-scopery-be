package com.company.scopery.modules.documenthub.template.application.action;

import com.company.scopery.modules.documenthub.nativecontent.application.action.SaveDocumentContentAction;
import com.company.scopery.modules.documenthub.nativecontent.application.command.SaveDocumentContentCommand;
import com.company.scopery.modules.documenthub.nativecontent.application.response.DocumentContentResponse;
import com.company.scopery.modules.documenthub.nativecontent.domain.enums.RevisionType;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import com.company.scopery.modules.documenthub.template.application.command.InstantiateNativeTemplateCommand;
import com.company.scopery.modules.documenthub.template.domain.model.DocumentTemplateVariableRepository;
import com.company.scopery.modules.documenthub.template.domain.model.DocumentTemplateVersionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InstantiateNativeTemplateAction {

    private final DocumentTemplateVersionRepository versionRepo;
    private final DocumentTemplateVariableRepository variableRepo;
    private final SaveDocumentContentAction saveContent;
    private final DocumentHubAuthorizationService authorization;

    public InstantiateNativeTemplateAction(DocumentTemplateVersionRepository versionRepo,
                                            DocumentTemplateVariableRepository variableRepo,
                                            SaveDocumentContentAction saveContent,
                                            DocumentHubAuthorizationService authorization) {
        this.versionRepo = versionRepo;
        this.variableRepo = variableRepo;
        this.saveContent = saveContent;
        this.authorization = authorization;
    }

    @Transactional
    public DocumentContentResponse execute(InstantiateNativeTemplateCommand c) {
        authorization.requireCreate(c.projectId());

        var version = versionRepo.findByIdAndTemplateId(c.templateVersionId(), c.templateId())
                .orElseThrow(() -> DocumentHubExceptions.templateNotFound(c.templateVersionId()));

        var variableDefs = variableRepo.findByTemplateVersionId(version.id());

        // Validate required variables are provided
        for (var varDef : variableDefs) {
            if (varDef.required() && (c.variables() == null || !c.variables().containsKey(varDef.variableKey()))) {
                throw DocumentHubExceptions.templateVariableMissing(varDef.variableKey());
            }
        }

        // Substitute variable placeholders in AST (non-sensitive only)
        String resolvedAst = substituteVariables(version.ast(), c.variables(), variableDefs.stream()
                .filter(v -> !v.sensitive())
                .collect(Collectors.toMap(v -> v.variableKey(), v -> v.variableKey())));

        return saveContent.execute(new SaveDocumentContentCommand(
                c.projectId(), c.targetDocumentId(), resolvedAst, 0L,
                null, RevisionType.TEMPLATE_CREATE));
    }

    private String substituteVariables(String ast, Map<String, String> provided, Map<String, String> allowedKeys) {
        if (ast == null || provided == null || provided.isEmpty()) return ast;
        String result = ast;
        for (var entry : provided.entrySet()) {
            if (allowedKeys.containsKey(entry.getKey())) {
                result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }
        }
        return result;
    }
}
