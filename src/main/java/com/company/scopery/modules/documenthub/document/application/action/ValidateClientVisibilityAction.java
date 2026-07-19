package com.company.scopery.modules.documenthub.document.application.action;

import com.company.scopery.modules.documenthub.document.domain.model.DocumentRepository;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentMention;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentMentionRepository;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ValidateClientVisibilityAction {

    private final DocumentRepository documentRepo;
    private final DocumentMentionRepository mentionRepo;
    private final DocumentHubAuthorizationService authorization;

    public ValidateClientVisibilityAction(DocumentRepository documentRepo,
                                           DocumentMentionRepository mentionRepo,
                                           DocumentHubAuthorizationService authorization) {
        this.documentRepo = documentRepo;
        this.mentionRepo = mentionRepo;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public ValidationResult execute(UUID projectId, UUID documentId) {
        authorization.requireView(projectId);

        var document = documentRepo.findByIdAndProjectId(documentId, projectId)
                .orElseThrow(() -> DocumentHubExceptions.documentNotFound(documentId));

        List<ClientVisibilityIssue> issues = new ArrayList<>();

        if ("RESTRICTED".equalsIgnoreCase(document.classification())) {
            issues.add(new ClientVisibilityIssue("RESTRICTED_CLASSIFICATION",
                    "Document classification is RESTRICTED — not eligible for client visibility", null));
        }

        var mentions = mentionRepo.findByDocumentId(documentId);
        for (DocumentMention mention : mentions) {
            if ("USER".equals(mention.mentionType()) || "TEAM".equals(mention.mentionType())) {
                issues.add(new ClientVisibilityIssue("INTERNAL_MENTION",
                        "Document contains internal " + mention.mentionType() + " mention",
                        mention.mentionedResourceId()));
            }
        }

        return new ValidationResult(issues.isEmpty(), issues);
    }

    public record ClientVisibilityIssue(String issueType, String message, UUID resourceId) {}

    public record ValidationResult(boolean valid, List<ClientVisibilityIssue> issues) {}
}
