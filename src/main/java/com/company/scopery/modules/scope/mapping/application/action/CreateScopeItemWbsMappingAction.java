package com.company.scopery.modules.scope.mapping.application.action;
import com.company.scopery.modules.project.wbs.domain.model.WbsNodeRepository;
import com.company.scopery.modules.scope.mapping.application.command.CreateScopeItemWbsMappingCommand;
import com.company.scopery.modules.scope.mapping.application.response.ScopeItemWbsMappingResponse;
import com.company.scopery.modules.scope.mapping.domain.enums.MappingType;
import com.company.scopery.modules.scope.mapping.domain.model.ScopeItemWbsMapping;
import com.company.scopery.modules.scope.mapping.domain.model.ScopeItemWbsMappingRepository;
import com.company.scopery.modules.scope.scopeitem.domain.model.ScopeItemRepository;
import com.company.scopery.modules.scope.shared.activity.ScopeActivityLogger;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.constant.ScopeActivityActions;
import com.company.scopery.modules.scope.shared.constant.ScopeEntityTypes;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import com.company.scopery.modules.scope.shared.util.ScopeEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateScopeItemWbsMappingAction {
    private final ScopeItemRepository items;
    private final WbsNodeRepository wbsNodes;
    private final ScopeItemWbsMappingRepository mappings;
    private final ScopeAuthorizationService authorization;
    private final ScopeActivityLogger activityLogger;
    public CreateScopeItemWbsMappingAction(ScopeItemRepository items, WbsNodeRepository wbsNodes,
                                           ScopeItemWbsMappingRepository mappings, ScopeAuthorizationService authorization,
                                           ScopeActivityLogger activityLogger) {
        this.items = items; this.wbsNodes = wbsNodes; this.mappings = mappings;
        this.authorization = authorization; this.activityLogger = activityLogger;
    }
    @Transactional
    public ScopeItemWbsMappingResponse execute(CreateScopeItemWbsMappingCommand command) {
        authorization.requireScopeUpdate(command.projectId());
        items.findByIdAndProjectId(command.scopeItemId(), command.projectId())
                .orElseThrow(() -> ScopeExceptions.itemNotFound(command.scopeItemId()));
        var wbs = wbsNodes.findById(command.wbsNodeId())
                .filter(n -> n.projectId().equals(command.projectId()))
                .orElseThrow(() -> ScopeExceptions.wbsNodeNotFound(command.wbsNodeId()));
        MappingType type = ScopeEnumParser.parseRequired(MappingType.class, command.mappingType(), "mappingType");
        ScopeItemWbsMapping saved = mappings.save(ScopeItemWbsMapping.create(
                command.scopeItemId(), command.projectId(), wbs.id(), type));
        activityLogger.logSuccess(ScopeEntityTypes.WBS_MAPPING, saved.id(), ScopeActivityActions.WBS_MAPPING_CREATED,
                "WBS mapping created");
        return ScopeItemWbsMappingResponse.from(saved);
    }
}
