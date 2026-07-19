package com.company.scopery.modules.scope.mapping.application.action;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.scope.deliverable.domain.model.DeliverableRepository;
import com.company.scopery.modules.scope.mapping.application.command.CreateDeliverableTaskMappingCommand;
import com.company.scopery.modules.scope.mapping.application.response.DeliverableTaskMappingResponse;
import com.company.scopery.modules.scope.mapping.domain.enums.MappingType;
import com.company.scopery.modules.scope.mapping.domain.model.DeliverableTaskMapping;
import com.company.scopery.modules.scope.mapping.domain.model.DeliverableTaskMappingRepository;
import com.company.scopery.modules.scope.shared.activity.ScopeActivityLogger;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.constant.ScopeActivityActions;
import com.company.scopery.modules.scope.shared.constant.ScopeEntityTypes;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import com.company.scopery.modules.scope.shared.util.ScopeEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateDeliverableTaskMappingAction {
    private final DeliverableRepository deliverables;
    private final TaskRepository tasks;
    private final DeliverableTaskMappingRepository mappings;
    private final ScopeAuthorizationService authorization;
    private final ScopeActivityLogger activityLogger;
    public CreateDeliverableTaskMappingAction(DeliverableRepository deliverables, TaskRepository tasks,
                                              DeliverableTaskMappingRepository mappings,
                                              ScopeAuthorizationService authorization, ScopeActivityLogger activityLogger) {
        this.deliverables = deliverables; this.tasks = tasks; this.mappings = mappings;
        this.authorization = authorization; this.activityLogger = activityLogger;
    }
    @Transactional
    public DeliverableTaskMappingResponse execute(CreateDeliverableTaskMappingCommand command) {
        authorization.requireDeliverableUpdate(command.projectId());
        deliverables.findByIdAndProjectId(command.deliverableId(), command.projectId())
                .orElseThrow(() -> ScopeExceptions.deliverableNotFound(command.deliverableId()));
        tasks.findById(command.taskId())
                .filter(t -> t.projectId().equals(command.projectId()))
                .orElseThrow(() -> ScopeExceptions.taskNotFound(command.taskId()));
        MappingType type = command.mappingType() == null || command.mappingType().isBlank()
                ? MappingType.SUPPORTING
                : ScopeEnumParser.parseRequired(MappingType.class, command.mappingType(), "mappingType");
        DeliverableTaskMapping saved = mappings.save(DeliverableTaskMapping.create(
                command.deliverableId(), command.projectId(), command.taskId(), type));
        activityLogger.logSuccess(ScopeEntityTypes.TASK_MAPPING, saved.id(), ScopeActivityActions.TASK_MAPPING_CREATED,
                "Task mapping created");
        return DeliverableTaskMappingResponse.from(saved);
    }
}
