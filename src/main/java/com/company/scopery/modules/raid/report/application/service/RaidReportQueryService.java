package com.company.scopery.modules.raid.report.application.service;
import com.company.scopery.modules.raid.decision.domain.model.DecisionRecordRepository;
import com.company.scopery.modules.raid.raidaction.domain.model.RaidActionRepository;
import com.company.scopery.modules.raid.raiditem.application.response.RaidItemResponse;
import com.company.scopery.modules.raid.raiditem.domain.enums.RaidItemType;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItem;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItemRepository;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service
public class RaidReportQueryService {
    private final RaidItemRepository items; private final RaidActionRepository actions;
    private final DecisionRecordRepository decisions; private final RaidAuthorizationService authorization;
    public RaidReportQueryService(RaidItemRepository items, RaidActionRepository actions,
                                  DecisionRecordRepository decisions, RaidAuthorizationService authorization) {
        this.items=items; this.actions=actions; this.decisions=decisions; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public Map<String, Object> summary(UUID projectId) {
        authorization.requireView(projectId);
        List<RaidItem> all = items.findByProjectId(projectId);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("total", all.size());
        Map<String, Long> byType = new LinkedHashMap<>();
        for (RaidItemType t : RaidItemType.values()) {
            byType.put(t.name(), all.stream().filter(i -> i.type() == t).count());
        }
        out.put("byType", byType);
        out.put("openCount", all.stream().filter(i -> "OPEN".equals(i.status().name()) || "ESCALATED".equals(i.status().name())).count());
        out.put("decisionCount", decisions.findByProjectId(projectId).size());
        return out;
    }
    @Transactional(readOnly=true)
    public List<RaidItemResponse> byType(UUID projectId, RaidItemType type) {
        authorization.requireView(projectId);
        return items.findByProjectIdAndType(projectId, type).stream().map(RaidItemResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public List<Map<String, Object>> actions(UUID projectId) {
        authorization.requireView(projectId);
        List<Map<String, Object>> rows = new ArrayList<>();
        for (RaidItem item : items.findByProjectId(projectId)) {
            actions.findByRaidItemId(item.id()).forEach(a -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("raidItemId", item.id()); m.put("raidItemTitle", item.title()); m.put("actionId", a.id());
                m.put("title", a.title()); m.put("status", a.status().name()); m.put("dueDate", a.dueDate());
                rows.add(m);
            });
        }
        return rows;
    }
    @Transactional(readOnly=true)
    public List<Map<String, Object>> decisionLog(UUID projectId) {
        authorization.requireDecisionView(projectId);
        return decisions.findByProjectId(projectId).stream().map(d -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", d.id()); m.put("title", d.title()); m.put("category", d.category().name());
            m.put("status", d.status().name()); m.put("outcome", d.outcome()); m.put("decidedAt", d.decidedAt());
            return m;
        }).toList();
    }
}
