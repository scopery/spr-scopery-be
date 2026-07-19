package com.company.scopery.modules.configuration.statusset.application.action;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import com.company.scopery.modules.configuration.statusset.application.response.StatusValueResponse;
import com.company.scopery.modules.configuration.statusset.domain.model.*;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.configuration.statusset.application.command.CreateStatusValueCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateStatusValueAction {
    private final StatusSetRepository sets; private final StatusValueRepository values; private final ConfigurationAuthorizationService authorization;
    public CreateStatusValueAction(StatusSetRepository sets, StatusValueRepository values, ConfigurationAuthorizationService authorization) {
        this.sets=sets; this.values=values; this.authorization=authorization;
    }
    @Transactional
    public StatusValueResponse execute(CreateStatusValueCommand c) {
        authorization.requireFieldUpdate(c.workspaceId());
        sets.findByIdAndWorkspaceId(c.setId(), c.workspaceId()).orElseThrow(() -> ConfigurationExceptions.statusSetNotFound(c.setId()));
        return StatusValueResponse.from(values.save(StatusValue.create(c.setId(), c.code(), c.label(), c.domainCategory(), c.sortOrder() == null ? 0 : c.sortOrder())));
    }
}
