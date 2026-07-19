package com.company.scopery.modules.collaboration.report.application.service;
import com.company.scopery.modules.collaboration.actionitem.domain.enums.ActionItemStatus;
import com.company.scopery.modules.collaboration.actionitem.domain.model.MeetingActionItemRepository;
import com.company.scopery.modules.collaboration.commentthread.domain.model.CommentThreadRepository;
import com.company.scopery.modules.collaboration.meeting.domain.model.MeetingRepository;
import com.company.scopery.modules.collaboration.minutes.domain.model.MeetingMinutesRepository;
import com.company.scopery.modules.collaboration.report.application.response.CollaborationReportResponses.*;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList; import java.util.List; import java.util.UUID;
@Service
public class CollaborationReportQueryService {
    private final MeetingRepository meetings; private final MeetingActionItemRepository actions;
    private final MeetingMinutesRepository minutes; private final CommentThreadRepository threads;
    private final CollaborationAuthorizationService authorization;
    public CollaborationReportQueryService(MeetingRepository meetings, MeetingActionItemRepository actions, MeetingMinutesRepository minutes,
                                           CommentThreadRepository threads, CollaborationAuthorizationService authorization) {
        this.meetings=meetings; this.actions=actions; this.minutes=minutes; this.threads=threads; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<MeetingRegisterRow> meetings(UUID projectId) {
        authorization.requireReportView(projectId);
        return meetings.findByProjectId(projectId).stream()
                .map(m -> new MeetingRegisterRow(m.id(), m.title(), m.meetingType().name(), m.status().name(), m.clientVisible())).toList();
    }
    @Transactional(readOnly=true)
    public List<MeetingActionReportRow> actions(UUID projectId, boolean overdueOnly) {
        authorization.requireReportView(projectId);
        LocalDate today = LocalDate.now();
        List<MeetingActionReportRow> rows = new ArrayList<>();
        for (var a : actions.findByProjectId(projectId)) {
            if (a.archivedAt() != null) continue;
            boolean overdue = a.dueDate() != null && a.dueDate().isBefore(today)
                    && (a.status() == ActionItemStatus.OPEN || a.status() == ActionItemStatus.IN_PROGRESS || a.status() == ActionItemStatus.OVERDUE);
            if (overdueOnly && !overdue) continue;
            rows.add(new MeetingActionReportRow(a.id(), a.meetingId(), a.title(), a.status().name(), a.dueDate(), overdue));
        }
        return rows;
    }
    @Transactional(readOnly=true)
    public List<MinutesStatusRow> minutesStatus(UUID projectId) {
        authorization.requireReportView(projectId);
        List<MinutesStatusRow> rows = new ArrayList<>();
        for (var m : meetings.findByProjectId(projectId)) {
            for (var min : minutes.findByMeetingId(m.id())) {
                rows.add(new MinutesStatusRow(m.id(), min.id(), min.status().name()));
            }
        }
        return rows;
    }
    @Transactional(readOnly=true)
    public List<CommentActivityRow> commentActivity(UUID projectId) {
        authorization.requireReportView(projectId);
        return threads.findByProjectId(projectId).stream()
                .map(t -> new CommentActivityRow(t.id(), t.targetType(), t.targetId(), t.status().name())).toList();
    }
}
