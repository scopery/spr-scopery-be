package com.company.scopery.modules.documenthub.document.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.documenthub.document.application.command.ApproveDocumentCommand;
import com.company.scopery.modules.documenthub.document.domain.enums.DocumentStatus;
import com.company.scopery.modules.documenthub.document.domain.model.Document;
import com.company.scopery.modules.documenthub.document.domain.model.DocumentRepository;
import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubErrorCatalog;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ApproveDocumentActionTest {

    @Mock DocumentRepository repo;
    @Mock DocumentHubAuthorizationService authorization;
    @Mock CurrentUserAuthorizationService currentUser;
    @Mock DocumentHubActivityLogger activityLogger;
    @Mock IamUser iamUser;

    ApproveDocumentAction action;

    final UUID projectId = UUID.randomUUID();
    final UUID documentId = UUID.randomUUID();
    final UUID actorId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        action = new ApproveDocumentAction(repo, authorization, currentUser, activityLogger);
        when(currentUser.resolveCurrentUser()).thenReturn(iamUser);
        when(iamUser.id()).thenReturn(actorId);
    }

    @Test
    void execute_approvesDraftDocument() {
        Document draft = draftDocument();
        when(repo.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(draft));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var response = action.execute(new ApproveDocumentCommand(projectId, documentId));

        assertThat(response.status()).isEqualTo(DocumentStatus.APPROVED.name());
        verify(repo).save(any());
    }

    @Test
    void execute_blocksApproveWhenAlreadyApproved() {
        Document approved = draftDocument().approve(actorId);
        when(repo.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(approved));

        assertThatThrownBy(() -> action.execute(new ApproveDocumentCommand(projectId, documentId)))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(DocumentHubErrorCatalog.DOCUMENT_IMMUTABLE.code()));
        verify(repo, never()).save(any());
    }

    @Test
    void execute_blocksApproveWhenArchived() {
        Document archived = draftDocument().archive(actorId);
        when(repo.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(archived));

        assertThatThrownBy(() -> action.execute(new ApproveDocumentCommand(projectId, documentId)))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(DocumentHubErrorCatalog.DOCUMENT_IMMUTABLE.code()));
        verify(repo, never()).save(any());
    }

    @Test
    void execute_throwsNotFoundWhenDocumentMissing() {
        when(repo.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> action.execute(new ApproveDocumentCommand(projectId, documentId)))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(DocumentHubErrorCatalog.DOCUMENT_NOT_FOUND.code()));
    }

    private Document draftDocument() {
        return Document.create(UUID.randomUUID(), projectId, null, "SPEC", "D-1", "Spec", null);
    }
}
