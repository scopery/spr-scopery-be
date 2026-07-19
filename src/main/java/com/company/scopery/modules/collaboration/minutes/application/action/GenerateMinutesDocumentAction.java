package com.company.scopery.modules.collaboration.minutes.application.action;
import com.company.scopery.modules.collaboration.minutes.application.response.MeetingMinutesResponse;
import com.company.scopery.modules.collaboration.minutes.domain.model.MeetingMinutesRepository;
import com.company.scopery.modules.collaboration.meeting.domain.model.MeetingRepository;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationActivityActions;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationEntityTypes;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import com.company.scopery.modules.documenthub.document.application.action.CreateDocumentAction;
import com.company.scopery.modules.documenthub.document.application.command.CreateDocumentCommand;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.collaboration.minutes.application.command.GenerateMinutesDocumentCommand;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GenerateMinutesDocumentAction {
    private final MeetingRepository meetings;
    private final MeetingMinutesRepository minutesRepo;
    private final CreateDocumentAction createDocument;
    private final CollaborationAuthorizationService authorization;
    private final CollaborationActivityLogger activityLogger;

    public GenerateMinutesDocumentAction(MeetingRepository meetings, MeetingMinutesRepository minutesRepo,
                                         CreateDocumentAction createDocument,
                                         CollaborationAuthorizationService authorization,
                                         CollaborationActivityLogger activityLogger) {
        this.meetings = meetings;
        this.minutesRepo = minutesRepo;
        this.createDocument = createDocument;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public MeetingMinutesResponse execute(GenerateMinutesDocumentCommand c) {
        authorization.requireMinutesGenerateDocument(c.projectId());
        meetings.findByIdAndProjectId(c.meetingId(), c.projectId()).orElseThrow(() -> CollaborationExceptions.meetingNotFound(c.meetingId()));
        var minutes = minutesRepo.findByIdAndProjectId(c.minutesId(), c.projectId())
                .orElseThrow(() -> CollaborationExceptions.minutesNotFound(c.minutesId()));
        if (!minutes.meetingId().equals(c.meetingId())) throw CollaborationExceptions.minutesNotFound(c.minutesId());
        if (minutes.documentId() != null) throw CollaborationExceptions.minutesDocumentAlreadyLinked(c.minutesId());

        String docTitle = (c.title() == null || c.title().isBlank())
                ? "Meeting minutes " + c.meetingId()
                : c.title().trim();
        String docCode = (c.code() == null || c.code().isBlank())
                ? ("MIN-" + c.minutesId().toString().substring(0, 8).toUpperCase())
                : c.code().trim();
        String description = buildDescription(minutes);
        var doc = createDocument.execute(new CreateDocumentCommand(
                c.projectId(), c.folderId(), "MEETING_MINUTES", docCode, docTitle, description, null));
        try {
            minutes = minutes.attachDocument(doc.id(), doc.currentVersionId());
        } catch (IllegalStateException ex) {
            throw CollaborationExceptions.minutesDocumentAlreadyLinked(c.minutesId());
        }
        minutes = minutesRepo.save(minutes);
        activityLogger.logSuccess(CollaborationEntityTypes.MINUTES, minutes.id(),
                CollaborationActivityActions.MINUTES_DOCUMENT_GENERATED,
                "Minutes document generated: " + doc.id());
        return MeetingMinutesResponse.from(minutes);
    }

    private static String buildDescription(com.company.scopery.modules.collaboration.minutes.domain.model.MeetingMinutes m) {
        StringBuilder sb = new StringBuilder();
        if (m.summary() != null) sb.append(m.summary());
        if (m.decisionsSummary() != null) {
            if (!sb.isEmpty()) sb.append("\n\n");
            sb.append("Decisions:\n").append(m.decisionsSummary());
        }
        if (m.actionsSummary() != null) {
            if (!sb.isEmpty()) sb.append("\n\n");
            sb.append("Actions:\n").append(m.actionsSummary());
        }
        return sb.isEmpty() ? null : sb.toString();
    }
}
