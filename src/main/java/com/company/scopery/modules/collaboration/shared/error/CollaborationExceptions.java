package com.company.scopery.modules.collaboration.shared.error;
import com.company.scopery.common.exception.AppException;
import java.util.Map; import java.util.UUID;
public final class CollaborationExceptions {
    private CollaborationExceptions() {}
    public static AppException meetingNotFound(UUID id) {
        return new AppException(CollaborationErrorCatalog.MEETING_NOT_FOUND, "Meeting not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException seriesNotFound(UUID id) {
        return new AppException(CollaborationErrorCatalog.MEETING_SERIES_NOT_FOUND, "Series not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException participantNotFound(UUID id) {
        return new AppException(CollaborationErrorCatalog.PARTICIPANT_NOT_FOUND, "Participant not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException agendaNotFound(UUID id) {
        return new AppException(CollaborationErrorCatalog.AGENDA_ITEM_NOT_FOUND, "Agenda item not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException minutesNotFound(UUID id) {
        return new AppException(CollaborationErrorCatalog.MINUTES_NOT_FOUND, "Minutes not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException minutesImmutable(UUID id) {
        return new AppException(CollaborationErrorCatalog.MINUTES_IMMUTABLE, "Minutes immutable: " + id, Map.of("id", id));
    }
    public static AppException noteNotFound(UUID id) {
        return new AppException(CollaborationErrorCatalog.NOTE_NOT_FOUND, "Note not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException actionNotFound(UUID id) {
        return new AppException(CollaborationErrorCatalog.ACTION_ITEM_NOT_FOUND, "Action item not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException linkNotFound(UUID id) {
        return new AppException(CollaborationErrorCatalog.ARTIFACT_LINK_NOT_FOUND, "Link not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException linkDuplicate() {
        return new AppException(CollaborationErrorCatalog.ARTIFACT_LINK_DUPLICATE);
    }
    public static AppException threadNotFound(UUID id) {
        return new AppException(CollaborationErrorCatalog.COMMENT_THREAD_NOT_FOUND, "Thread not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException commentNotFound(UUID id) {
        return new AppException(CollaborationErrorCatalog.COMMENT_NOT_FOUND, "Comment not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException invalidStatus(String d) {
        return new AppException(CollaborationErrorCatalog.MEETING_INVALID_STATUS, d, Map.of());
    }
    public static AppException accessDenied() {
        return new AppException(CollaborationErrorCatalog.COLLABORATION_ACCESS_DENIED);
    }
    public static AppException projectArchived(UUID id) {
        return new AppException(CollaborationErrorCatalog.COLLABORATION_PROJECT_ARCHIVED, "Project archived: " + id, Map.of("projectId", id));
    }
    public static AppException titleRequired() {
        return new AppException(CollaborationErrorCatalog.COLLABORATION_TITLE_REQUIRED);
    }
    public static AppException actionAlreadyLinked(UUID id) {
        return new AppException(CollaborationErrorCatalog.ACTION_ALREADY_LINKED, "Already linked: " + id, Map.of("id", id));
    }
    public static AppException minutesDocumentAlreadyLinked(UUID id) {
        return new AppException(CollaborationErrorCatalog.MINUTES_DOCUMENT_ALREADY_LINKED, "Minutes already linked: " + id, Map.of("id", id));
    }
    public static AppException noteArchived(UUID id) {
        return new AppException(CollaborationErrorCatalog.NOTE_ARCHIVED, "Note archived: " + id, Map.of("id", id));
    }
    public static AppException meetingNotClientVisible(UUID id) {
        return new AppException(CollaborationErrorCatalog.MEETING_NOT_CLIENT_VISIBLE, "Meeting not client-visible: " + id, Map.of("id", id == null ? "" : id));
    }
}
