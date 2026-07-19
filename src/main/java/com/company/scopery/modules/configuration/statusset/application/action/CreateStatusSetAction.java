package com.company.scopery.modules.configuration.statusset.application.action;
import com.company.scopery.modules.configuration.shared.activity.ConfigurationActivityLogger;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.constant.*;
import com.company.scopery.modules.configuration.statusset.application.response.StatusSetResponse;
import com.company.scopery.modules.configuration.statusset.domain.model.*;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.configuration.statusset.application.command.CreateStatusSetCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateStatusSetAction {
    private final StatusSetRepository sets; private final ConfigurationAuthorizationService authorization; private final ConfigurationActivityLogger activityLogger;
    public CreateStatusSetAction(StatusSetRepository sets, ConfigurationAuthorizationService authorization, ConfigurationActivityLogger activityLogger) {
        this.sets=sets; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public StatusSetResponse execute(CreateStatusSetCommand c) {
        authorization.requireFieldCreate(c.workspaceId());
        var s = sets.save(StatusSet.create(c.workspaceId(), c.objectType(), c.setCode(), c.name()));
        activityLogger.logSuccess(ConfigurationEntityTypes.STATUS_SET, s.id(), ConfigurationActivityActions.STATUS_SET_CREATED, "Status set created");
        return StatusSetResponse.from(s);
    }
}
