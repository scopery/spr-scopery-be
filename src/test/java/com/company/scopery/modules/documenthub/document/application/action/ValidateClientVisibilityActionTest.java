package com.company.scopery.modules.documenthub.document.application.action;

import com.company.scopery.modules.documenthub.document.domain.enums.ContentMode;
import com.company.scopery.modules.documenthub.document.domain.enums.ContentWidth;
import com.company.scopery.modules.documenthub.document.domain.enums.DocumentStatus;
import com.company.scopery.modules.documenthub.document.domain.model.Document;
import com.company.scopery.modules.documenthub.document.domain.model.DocumentRepository;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentMention;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentMentionRepository;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests BR-NDE-010: client visibility validation.
 * RESTRICTED classification and internal (USER/TEAM) mentions block visibility.
 */
@ExtendWith(MockitoExtension.class)
class ValidateClientVisibilityActionTest {

    @Mock DocumentRepository documentRepo;
    @Mock DocumentMentionRepository mentionRepo;
    @Mock DocumentHubAuthorizationService authorization;

    ValidateClientVisibilityAction action;

    final UUID projectId = UUID.randomUUID();
    final UUID documentId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        action = new ValidateClientVisibilityAction(documentRepo, mentionRepo, authorization);
    }

    @Test
    void validate_valid_whenNoIssues() {
        Document doc = documentWithClassification("INTERNAL");
        when(documentRepo.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(doc));
        when(mentionRepo.findByDocumentId(documentId)).thenReturn(List.of());

        var result = action.execute(projectId, documentId);

        assertThat(result.valid()).isTrue();
        assertThat(result.issues()).isEmpty();
    }

    @Test
    void validate_invalid_whenClassificationIsRestricted() {
        Document doc = documentWithClassification("RESTRICTED");
        when(documentRepo.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(doc));
        when(mentionRepo.findByDocumentId(documentId)).thenReturn(List.of());

        var result = action.execute(projectId, documentId);

        assertThat(result.valid()).isFalse();
        assertThat(result.issues()).hasSize(1);
        assertThat(result.issues().get(0).issueType()).isEqualTo("RESTRICTED_CLASSIFICATION");
    }

    @Test
    void validate_invalid_whenUserMentionPresent() {
        Document doc = documentWithClassification("INTERNAL");
        when(documentRepo.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(doc));

        UUID mentionedUserId = UUID.randomUUID();
        DocumentMention userMention = new DocumentMention(UUID.randomUUID(), documentId, UUID.randomUUID(),
                projectId, "block-1", "USER", "IAM_USER", mentionedUserId, Instant.now(), Instant.now());
        when(mentionRepo.findByDocumentId(documentId)).thenReturn(List.of(userMention));

        var result = action.execute(projectId, documentId);

        assertThat(result.valid()).isFalse();
        assertThat(result.issues()).hasSize(1);
        assertThat(result.issues().get(0).issueType()).isEqualTo("INTERNAL_MENTION");
        assertThat(result.issues().get(0).resourceId()).isEqualTo(mentionedUserId);
    }

    @Test
    void validate_invalid_whenTeamMentionPresent() {
        Document doc = documentWithClassification("INTERNAL");
        when(documentRepo.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(doc));

        UUID teamId = UUID.randomUUID();
        DocumentMention teamMention = new DocumentMention(UUID.randomUUID(), documentId, UUID.randomUUID(),
                projectId, "block-2", "TEAM", "IAM_TEAM", teamId, Instant.now(), Instant.now());
        when(mentionRepo.findByDocumentId(documentId)).thenReturn(List.of(teamMention));

        var result = action.execute(projectId, documentId);

        assertThat(result.valid()).isFalse();
        assertThat(result.issues()).hasSize(1);
        assertThat(result.issues().get(0).issueType()).isEqualTo("INTERNAL_MENTION");
    }

    @Test
    void validate_valid_whenExternalResourceMentionOnly() {
        // PROJECT, TASK etc. are not USER/TEAM — should not block visibility
        Document doc = documentWithClassification("INTERNAL");
        when(documentRepo.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(doc));

        DocumentMention taskMention = new DocumentMention(UUID.randomUUID(), documentId, UUID.randomUUID(),
                projectId, "block-3", "TASK", "PROJECT_TASK", UUID.randomUUID(), Instant.now(), Instant.now());
        when(mentionRepo.findByDocumentId(documentId)).thenReturn(List.of(taskMention));

        var result = action.execute(projectId, documentId);

        assertThat(result.valid()).isTrue();
        assertThat(result.issues()).isEmpty();
    }

    @Test
    void validate_collectsMultipleIssues_whenBothRestrictedAndUserMention() {
        Document doc = documentWithClassification("RESTRICTED");
        when(documentRepo.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(doc));

        DocumentMention userMention = new DocumentMention(UUID.randomUUID(), documentId, UUID.randomUUID(),
                projectId, "block-1", "USER", "IAM_USER", UUID.randomUUID(), Instant.now(), Instant.now());
        when(mentionRepo.findByDocumentId(documentId)).thenReturn(List.of(userMention));

        var result = action.execute(projectId, documentId);

        assertThat(result.valid()).isFalse();
        assertThat(result.issues()).hasSize(2);

        var issueTypes = result.issues().stream().map(ValidateClientVisibilityAction.ClientVisibilityIssue::issueType).toList();
        assertThat(issueTypes).contains("RESTRICTED_CLASSIFICATION", "INTERNAL_MENTION");
    }

    @Test
    void validate_collectsIssuePerMention_forMultipleUserMentions() {
        Document doc = documentWithClassification("INTERNAL");
        when(documentRepo.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(doc));

        DocumentMention user1 = new DocumentMention(UUID.randomUUID(), documentId, UUID.randomUUID(),
                projectId, "block-1", "USER", "IAM_USER", UUID.randomUUID(), Instant.now(), Instant.now());
        DocumentMention user2 = new DocumentMention(UUID.randomUUID(), documentId, UUID.randomUUID(),
                projectId, "block-2", "USER", "IAM_USER", UUID.randomUUID(), Instant.now(), Instant.now());
        when(mentionRepo.findByDocumentId(documentId)).thenReturn(List.of(user1, user2));

        var result = action.execute(projectId, documentId);

        assertThat(result.valid()).isFalse();
        assertThat(result.issues()).hasSize(2);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Document documentWithClassification(String classification) {
        return new Document(documentId, UUID.randomUUID(), projectId, null, "SPEC", "D-1", "Title", null,
                DocumentStatus.DRAFT, classification, null, false, null, null, null, null, null, 0,
                Instant.now(), Instant.now(),
                ContentMode.NATIVE, null, null, 0L, null, null, null, null, null, null, null,
                ContentWidth.CENTERED, false);
    }
}
