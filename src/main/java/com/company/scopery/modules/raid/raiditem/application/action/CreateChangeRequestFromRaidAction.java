package com.company.scopery.modules.raid.raiditem.application.action;

import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.projectbaseline.changerequest.application.action.CreateChangeRequestAction;
import com.company.scopery.modules.projectbaseline.changerequest.application.command.CreateChangeRequestCommand;
import com.company.scopery.modules.raid.raiditem.application.response.CreateChangeRequestFromRaidResponse;
import com.company.scopery.modules.raid.raiditem.application.response.RaidItemResponse;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItemRepository;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class CreateChangeRequestFromRaidAction {
    private final ProjectRepository projects;
    private final RaidItemRepository items;
    private final CreateChangeRequestAction createChangeRequestAction;
    private final RaidAuthorizationService authorization;

    public CreateChangeRequestFromRaidAction(ProjectRepository projects, RaidItemRepository items,
                                             CreateChangeRequestAction createChangeRequestAction,
                                             RaidAuthorizationService authorization) {
        this.projects = projects;
        this.items = items;
        this.createChangeRequestAction = createChangeRequestAction;
        this.authorization = authorization;
    }

    @Transactional
    public CreateChangeRequestFromRaidResponse execute(UUID projectId, UUID itemId) {
        authorization.requireConvert(projectId);
        var project = projects.findById(projectId).orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        var item = items.findByIdAndProjectId(itemId, projectId).orElseThrow(() -> RaidExceptions.itemNotFound(itemId));
        String code = "RAID-CR-" + item.id().toString().substring(0, 8).toUpperCase();
        var cr = createChangeRequestAction.execute(new CreateChangeRequestCommand(
                project.id(), code, "RAID: " + item.title(), item.description(), "RISK_RESPONSE", "MEDIUM",
                project.currentBaselineId(), "Created from RAID item " + item.id()));
        item = items.save(item.withLinkedChangeRequest(cr.id()));
        return new CreateChangeRequestFromRaidResponse(RaidItemResponse.from(item), cr.id());
    }
}
