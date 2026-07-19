package com.company.scopery.modules.estimation.estimationrun.application.service;

import com.company.scopery.modules.estimation.estimationrun.application.response.EstimationRunResponse;
import com.company.scopery.modules.estimation.estimationrun.domain.model.EstimationRun;
import com.company.scopery.modules.estimation.estimationrun.domain.model.EstimationRunRepository;
import com.company.scopery.modules.estimation.phaserollup.application.response.PhaseEstimateRollupResponse;
import com.company.scopery.modules.estimation.phaserollup.domain.model.PhaseEstimateRollupRepository;
import com.company.scopery.modules.estimation.projectsummary.application.response.ProjectEstimateSummaryResponse;
import com.company.scopery.modules.estimation.projectsummary.domain.model.ProjectEstimateSummaryRepository;
import com.company.scopery.modules.estimation.shared.authorization.EstimationAuthorizationService;
import com.company.scopery.modules.estimation.shared.error.EstimationExceptions;
import com.company.scopery.modules.estimation.tasksnapshot.application.response.TaskEstimateSnapshotResponse;
import com.company.scopery.modules.estimation.tasksnapshot.domain.model.TaskEstimateSnapshotRepository;
import com.company.scopery.modules.estimation.wbsrollup.application.response.WbsEstimateRollupResponse;
import com.company.scopery.modules.estimation.wbsrollup.domain.model.WbsEstimateRollupRepository;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EstimationQueryService {

    private final EstimationRunRepository runs;
    private final TaskEstimateSnapshotRepository snapshots;
    private final WbsEstimateRollupRepository wbsRollups;
    private final PhaseEstimateRollupRepository phaseRollups;
    private final ProjectEstimateSummaryRepository summaries;
    private final ProjectRepository projects;
    private final EstimationAuthorizationService authorization;

    public EstimationQueryService(EstimationRunRepository runs,
                                  TaskEstimateSnapshotRepository snapshots,
                                  WbsEstimateRollupRepository wbsRollups,
                                  PhaseEstimateRollupRepository phaseRollups,
                                  ProjectEstimateSummaryRepository summaries,
                                  ProjectRepository projects,
                                  EstimationAuthorizationService authorization) {
        this.runs = runs;
        this.snapshots = snapshots;
        this.wbsRollups = wbsRollups;
        this.phaseRollups = phaseRollups;
        this.summaries = summaries;
        this.projects = projects;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<EstimationRunResponse> listRuns(UUID projectId) {
        authorization.requireView(projectId);
        return runs.findAllByProjectId(projectId).stream().map(EstimationRunResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public EstimationRunResponse getRun(UUID projectId, UUID runId) {
        authorization.requireView(projectId);
        return EstimationRunResponse.from(requireRun(projectId, runId));
    }

    @Transactional(readOnly = true)
    public EstimationRunResponse getCurrent(UUID projectId) {
        authorization.requireView(projectId);
        return EstimationRunResponse.from(resolveCurrentRun(projectId));
    }

    @Transactional(readOnly = true)
    public List<TaskEstimateSnapshotResponse> listTasks(UUID projectId, UUID runId) {
        authorization.requireTaskView(projectId);
        EstimationRun run = requireRun(projectId, runId);
        boolean rateDetail = authorization.hasRateDetailView(projectId);
        return snapshots.findAllByEstimationRunId(run.id()).stream()
                .map(s -> TaskEstimateSnapshotResponse.from(s, rateDetail))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskEstimateSnapshotResponse> listCurrentTasks(UUID projectId) {
        return listTasks(projectId, resolveCurrentRun(projectId).id());
    }

    @Transactional(readOnly = true)
    public List<WbsEstimateRollupResponse> listWbs(UUID projectId, UUID runId) {
        authorization.requireWbsView(projectId);
        EstimationRun run = requireRun(projectId, runId);
        return wbsRollups.findAllByEstimationRunId(run.id()).stream()
                .map(WbsEstimateRollupResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<WbsEstimateRollupResponse> listCurrentWbs(UUID projectId) {
        return listWbs(projectId, resolveCurrentRun(projectId).id());
    }

    @Transactional(readOnly = true)
    public List<PhaseEstimateRollupResponse> listPhases(UUID projectId, UUID runId) {
        authorization.requirePhaseView(projectId);
        EstimationRun run = requireRun(projectId, runId);
        return phaseRollups.findAllByEstimationRunId(run.id()).stream()
                .map(PhaseEstimateRollupResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<PhaseEstimateRollupResponse> listCurrentPhases(UUID projectId) {
        return listPhases(projectId, resolveCurrentRun(projectId).id());
    }

    @Transactional(readOnly = true)
    public ProjectEstimateSummaryResponse getSummary(UUID projectId, UUID runId) {
        authorization.requireSummaryView(projectId);
        EstimationRun run = requireRun(projectId, runId);
        return summaries.findByEstimationRunId(run.id())
                .map(ProjectEstimateSummaryResponse::from)
                .orElseThrow(() -> EstimationExceptions.runNotFound(runId));
    }

    @Transactional(readOnly = true)
    public ProjectEstimateSummaryResponse getCurrentSummary(UUID projectId) {
        return getSummary(projectId, resolveCurrentRun(projectId).id());
    }

    private EstimationRun requireRun(UUID projectId, UUID runId) {
        return runs.findById(runId)
                .filter(r -> r.projectId().equals(projectId))
                .orElseThrow(() -> EstimationExceptions.runNotFound(runId));
    }

    private EstimationRun resolveCurrentRun(UUID projectId) {
        Project project = projects.findById(projectId)
                .orElseThrow(() -> EstimationExceptions.runNotFound(projectId));
        return runs.findCurrent(projectId, project.currentEstimationRunId())
                .or(() -> runs.findLatestCompletedByProjectId(projectId))
                .orElseThrow(() -> EstimationExceptions.runNotFound(project.currentEstimationRunId()));
    }
}
