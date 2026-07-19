package com.company.scopery.modules.knowledge.source.infrastructure.sourceadapter;

import com.company.scopery.modules.collaboration.meeting.domain.model.Meeting;
import com.company.scopery.modules.collaboration.meeting.domain.model.MeetingRepository;
import com.company.scopery.modules.collaboration.minutes.domain.model.MeetingMinutes;
import com.company.scopery.modules.collaboration.minutes.domain.model.MeetingMinutesRepository;
import com.company.scopery.modules.knowledge.source.domain.enums.KnowledgeSourceType;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class MeetingMinuteKnowledgeSourceAdapter {

    private final MeetingMinutesRepository minutesRepository;
    private final MeetingRepository meetings;
    private final ProjectRepository projects;

    public MeetingMinuteKnowledgeSourceAdapter(MeetingMinutesRepository minutesRepository,
                                                MeetingRepository meetings,
                                                ProjectRepository projects) {
        this.minutesRepository = minutesRepository;
        this.meetings = meetings;
        this.projects = projects;
    }

    public Optional<KnowledgeSourceSnapshot> buildSnapshot(UUID projectId, UUID minutesId) {
        return minutesRepository.findByIdAndProjectId(minutesId, projectId).map(minutes -> {
            var project = projects.findById(minutes.projectId()).orElse(null);
            if (project == null) return null;

            var meeting = meetings.findByIdAndProjectId(minutes.meetingId(), projectId).orElse(null);

            String normalizedText = buildNormalizedText(minutes, meeting);
            boolean internalOnly = meeting == null || meeting.internalOnly();
            List<String> aclTokens = buildAclTokens(project.workspaceId(), minutes.projectId(), internalOnly);

            String title = meeting != null ? meeting.title() : "Meeting Minutes " + minutesId;
            String sourceVersion = minutes.version() + (minutes.updatedAt() != null ? "@" + minutes.updatedAt().toEpochMilli() : "");

            return new KnowledgeSourceSnapshot(
                    project.workspaceId(),
                    minutes.projectId(),
                    KnowledgeSourceType.MEETING_MINUTE,
                    minutesId,
                    stableVersionRef(minutesId, minutes.version()),
                    title,
                    "und",
                    resolveClassification(minutes, meeting),
                    normalizedText,
                    buildMetadata(minutes, meeting),
                    aclTokens,
                    sourceVersion,
                    "/meetings/" + minutes.meetingId() + "/minutes/" + minutesId,
                    minutes.updatedAt()
            );
        });
    }

    private String buildNormalizedText(MeetingMinutes minutes, Meeting meeting) {
        var sb = new StringBuilder();
        if (meeting != null) {
            appendSection(sb, "Meeting", meeting.title());
            if (meeting.startAt() != null) appendSection(sb, "Date", meeting.startAt().toString());
        }
        if (meeting != null && meeting.description() != null) {
            appendSection(sb, "Description", meeting.description());
        }
        appendSection(sb, "Summary", minutes.summary());
        appendSection(sb, "Decisions", minutes.decisionsSummary());
        appendSection(sb, "Actions", minutes.actionsSummary());
        if (minutes.clientVisibleSummary() != null && meeting != null && meeting.clientVisible()) {
            appendSection(sb, "Client Summary", minutes.clientVisibleSummary());
        }
        return sb.toString().strip();
    }

    private void appendSection(StringBuilder sb, String heading, String value) {
        if (value != null && !value.isBlank()) {
            sb.append(heading).append("\n").append(value).append("\n\n");
        }
    }

    private String resolveClassification(MeetingMinutes minutes, Meeting meeting) {
        if (meeting != null && meeting.clientVisible()) return "CLIENT_VISIBLE";
        return "INTERNAL";
    }

    private Map<String, Object> buildMetadata(MeetingMinutes minutes, Meeting meeting) {
        var map = new java.util.LinkedHashMap<String, Object>();
        map.put("meetingId", minutes.meetingId().toString());
        map.put("minutesStatus", minutes.status() != null ? minutes.status().name() : "");
        if (meeting != null) {
            map.put("meetingTitle", meeting.title());
            map.put("meetingStatus", meeting.status() != null ? meeting.status().name() : "");
            map.put("clientVisible", String.valueOf(meeting.clientVisible()));
            if (meeting.startAt() != null) map.put("meetingDate", meeting.startAt().toString());
        }
        if (minutes.documentVersionId() != null) {
            map.put("documentVersionId", minutes.documentVersionId().toString());
        }
        return Map.copyOf(map);
    }

    private List<String> buildAclTokens(UUID workspaceId, UUID projectId, boolean internalOnly) {
        List<String> tokens = new ArrayList<>();
        tokens.add("workspace:" + workspaceId);
        tokens.add("project:" + projectId);
        if (internalOnly) tokens.add("internal:true");
        tokens.sort(String::compareTo);
        return List.copyOf(tokens);
    }

    private UUID stableVersionRef(UUID minutesId, int version) {
        return UUID.nameUUIDFromBytes(("MEETING_MINUTE:" + minutesId + ":" + version).getBytes());
    }
}
