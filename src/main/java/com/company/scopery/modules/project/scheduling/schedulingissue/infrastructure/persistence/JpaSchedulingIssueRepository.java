package com.company.scopery.modules.project.scheduling.schedulingissue.infrastructure.persistence;

import com.company.scopery.modules.project.scheduling.schedulingissue.domain.enums.*;
import com.company.scopery.modules.project.scheduling.schedulingissue.domain.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

interface SpringDataSchedulingIssueJpaRepository extends JpaRepository<SchedulingIssueJpaEntity, UUID> {
    List<SchedulingIssueJpaEntity> findAllByScheduleRunId(UUID runId);
}
@Repository
public class JpaSchedulingIssueRepository implements SchedulingIssueRepository {
    private final SpringDataSchedulingIssueJpaRepository repository;
    public JpaSchedulingIssueRepository(SpringDataSchedulingIssueJpaRepository repository){this.repository=repository;}
    public List<SchedulingIssue> saveAll(List<SchedulingIssue> v){return repository.saveAllAndFlush(v.stream().map(this::entity).toList()).stream().map(this::domain).toList();}
    public List<SchedulingIssue> findAllByScheduleRunId(UUID r){return repository.findAllByScheduleRunId(r).stream().map(this::domain).toList();}
    private SchedulingIssue domain(SchedulingIssueJpaEntity e){return new SchedulingIssue(e.getId(),e.getScheduleRunId(),e.getProjectId(),e.getTaskId(),e.getUserId(),e.getWorkspaceMemberId(),SchedulingIssueType.valueOf(e.getIssueType()),SchedulingIssueSeverity.valueOf(e.getSeverity()),e.getMessage(),e.getIssueDate(),e.getDetailsJson(),e.getCreatedAt(),e.getUpdatedAt());}
    private SchedulingIssueJpaEntity entity(SchedulingIssue d){SchedulingIssueJpaEntity e=new SchedulingIssueJpaEntity();e.setId(d.id());e.setScheduleRunId(d.scheduleRunId());e.setProjectId(d.projectId());e.setTaskId(d.taskId());e.setUserId(d.userId());e.setWorkspaceMemberId(d.workspaceMemberId());e.setIssueType(d.issueType().name());e.setSeverity(d.severity().name());e.setMessage(d.message());e.setIssueDate(d.issueDate());e.setDetailsJson(d.detailsJson());if(d.createdAt()!=null)e.setCreatedAt(d.createdAt());return e;}
}
