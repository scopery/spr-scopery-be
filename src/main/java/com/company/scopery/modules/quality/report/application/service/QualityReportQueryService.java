package com.company.scopery.modules.quality.report.application.service;
import com.company.scopery.modules.quality.defect.domain.model.DefectRepository;
import com.company.scopery.modules.quality.release.domain.model.ReleasePackageRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.testrun.domain.model.TestRunRepository;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service
public class QualityReportQueryService {
    private final DefectRepository defects; private final TestRunRepository testRuns;
    private final ReleasePackageRepository releases; private final QualityAuthorizationService authorization;
    public QualityReportQueryService(DefectRepository defects, TestRunRepository testRuns, ReleasePackageRepository releases, QualityAuthorizationService authorization) {
        this.defects=defects; this.testRuns=testRuns; this.releases=releases; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public Map<String, Object> qualityDashboard(UUID projectId) {
        authorization.requireQualityView(projectId);
        var defectList = defects.findByProjectId(projectId);
        var runs = testRuns.findByProjectId(projectId);
        var releaseList = releases.findByProjectId(projectId);
        long openDefects = defectList.stream().filter(d -> !List.of("CLOSED","REJECTED","ARCHIVED","VERIFIED").contains(d.status().name())).count();
        long blockers = defects.findOpenBlockers(projectId).size();
        return Map.of("openDefects", openDefects, "openBlockers", blockers, "testRuns", runs.size(), "releases", releaseList.size());
    }
    @Transactional(readOnly=true)
    public Map<String, Object> defectsReport(UUID projectId) {
        authorization.requireDefectView(projectId);
        var list = defects.findByProjectId(projectId);
        Map<String, Long> bySeverity = new LinkedHashMap<>();
        for (var d : list) bySeverity.merge(d.severity().name(), 1L, Long::sum);
        return Map.of("total", list.size(), "bySeverity", bySeverity, "openBlockers", defects.findOpenBlockers(projectId).size());
    }
    @Transactional(readOnly=true)
    public Map<String, Object> releaseReadiness(UUID projectId) {
        authorization.requireReleaseView(projectId);
        return Map.of("releases", releases.findByProjectId(projectId).stream().map(r -> Map.of(
                "id", r.id().toString(), "code", r.code(), "status", r.status().name(),
                "readinessStatus", r.readinessStatus()==null?"":r.readinessStatus().name())).toList());
    }
    @Transactional(readOnly=true)
    public Map<String, Object> testExecution(UUID projectId) {
        authorization.requireTestView(projectId);
        return Map.of("runs", testRuns.findByProjectId(projectId).stream().map(r -> Map.of(
                "id", r.id().toString(), "name", r.name(), "status", r.status().name())).toList());
    }
}
