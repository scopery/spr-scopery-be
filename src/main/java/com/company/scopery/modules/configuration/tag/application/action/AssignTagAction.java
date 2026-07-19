package com.company.scopery.modules.configuration.tag.application.action;
import com.company.scopery.modules.configuration.shared.activity.ConfigurationActivityLogger;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.constant.*;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import com.company.scopery.modules.configuration.tag.application.response.TagAssignmentResponse;
import com.company.scopery.modules.configuration.tag.domain.model.*;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.configuration.tag.application.command.AssignTagCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class AssignTagAction {
    private final TagDefinitionRepository tags; private final TagAssignmentRepository assignments;
    private final ConfigurationAuthorizationService authorization; private final ConfigurationActivityLogger activityLogger;
    public AssignTagAction(TagDefinitionRepository tags, TagAssignmentRepository assignments,
                           ConfigurationAuthorizationService authorization, ConfigurationActivityLogger activityLogger) {
        this.tags=tags; this.assignments=assignments; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public TagAssignmentResponse execute(AssignTagCommand c) {
        authorization.requireFieldUpdate(c.workspaceId());
        tags.findByIdAndWorkspaceId(c.tagId(), c.workspaceId()).orElseThrow(() -> ConfigurationExceptions.tagNotFound(c.tagId()));
        var a = assignments.save(TagAssignment.create(c.workspaceId(), c.tagId(), c.objectType(), c.targetId()));
        activityLogger.logSuccess(ConfigurationEntityTypes.TAG_ASSIGNMENT, a.id(), ConfigurationActivityActions.TAG_ASSIGNED, "Tag assigned");
        return TagAssignmentResponse.from(a);
    }
}
