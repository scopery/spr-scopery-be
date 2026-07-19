package com.company.scopery.modules.collaboration.shared.error;
import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;
public enum CollaborationErrorCatalog implements ErrorCatalog {
    MEETING_NOT_FOUND("MEETING_NOT_FOUND", "Meeting not found", HttpStatus.NOT_FOUND),
    MEETING_SERIES_NOT_FOUND("MEETING_SERIES_NOT_FOUND", "Meeting series not found", HttpStatus.NOT_FOUND),
    PARTICIPANT_NOT_FOUND("MEETING_PARTICIPANT_NOT_FOUND", "Meeting participant not found", HttpStatus.NOT_FOUND),
    AGENDA_ITEM_NOT_FOUND("MEETING_AGENDA_ITEM_NOT_FOUND", "Agenda item not found", HttpStatus.NOT_FOUND),
    MINUTES_NOT_FOUND("MEETING_MINUTES_NOT_FOUND", "Meeting minutes not found", HttpStatus.NOT_FOUND),
    MINUTES_IMMUTABLE("MEETING_MINUTES_IMMUTABLE", "Approved minutes are immutable", HttpStatus.UNPROCESSABLE_ENTITY),
    NOTE_NOT_FOUND("MEETING_NOTE_NOT_FOUND", "Meeting note not found", HttpStatus.NOT_FOUND),
    ACTION_ITEM_NOT_FOUND("MEETING_ACTION_ITEM_NOT_FOUND", "Meeting action item not found", HttpStatus.NOT_FOUND),
    ARTIFACT_LINK_NOT_FOUND("MEETING_ARTIFACT_LINK_NOT_FOUND", "Artifact link not found", HttpStatus.NOT_FOUND),
    ARTIFACT_LINK_DUPLICATE("MEETING_ARTIFACT_LINK_DUPLICATE", "Active artifact link already exists", HttpStatus.CONFLICT),
    COMMENT_THREAD_NOT_FOUND("COMMENT_THREAD_NOT_FOUND", "Comment thread not found", HttpStatus.NOT_FOUND),
    COMMENT_NOT_FOUND("COMMENT_NOT_FOUND", "Comment not found", HttpStatus.NOT_FOUND),
    MEETING_INVALID_STATUS("MEETING_INVALID_STATUS", "Invalid meeting status for this action", HttpStatus.UNPROCESSABLE_ENTITY),
    COLLABORATION_ACCESS_DENIED("COLLABORATION_ACCESS_DENIED", "Collaboration access denied", HttpStatus.FORBIDDEN),
    COLLABORATION_PROJECT_ARCHIVED("COLLABORATION_PROJECT_ARCHIVED", "Cannot modify collaboration for archived project", HttpStatus.UNPROCESSABLE_ENTITY),
    COLLABORATION_TITLE_REQUIRED("COLLABORATION_TITLE_REQUIRED", "Title is required", HttpStatus.BAD_REQUEST),
    ACTION_ALREADY_LINKED("MEETING_ACTION_ALREADY_LINKED", "Action item already has a linked task", HttpStatus.CONFLICT),
    MINUTES_DOCUMENT_ALREADY_LINKED("MEETING_MINUTES_DOCUMENT_ALREADY_LINKED", "Minutes already linked to a document", HttpStatus.CONFLICT),
    NOTE_ARCHIVED("MEETING_NOTE_ARCHIVED", "Archived meeting note cannot be converted", HttpStatus.UNPROCESSABLE_ENTITY),
    MEETING_NOT_CLIENT_VISIBLE("MEETING_NOT_CLIENT_VISIBLE", "Meeting is not client-visible", HttpStatus.NOT_FOUND);
    private final String code; private final String defaultMessage; private final HttpStatus httpStatus;
    CollaborationErrorCatalog(String c, String m, HttpStatus s) { code=c; defaultMessage=m; httpStatus=s; }
    @Override public String code() { return code; }
    @Override public String defaultMessage() { return defaultMessage; }
    @Override public HttpStatus httpStatus() { return httpStatus; }
}
