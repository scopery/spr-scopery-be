package com.company.scopery.modules.configuration.statusset.application.service;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import com.company.scopery.modules.configuration.statusset.application.response.*;
import com.company.scopery.modules.configuration.statusset.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class StatusSetQueryService {
    private final StatusSetRepository sets; private final StatusValueRepository values; private final ConfigurationAuthorizationService authorization;
    public StatusSetQueryService(StatusSetRepository sets, StatusValueRepository values, ConfigurationAuthorizationService authorization) {
        this.sets=sets; this.values=values; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<StatusSetResponse> list(UUID workspaceId) {
        authorization.requireFieldView(workspaceId);
        return sets.findByWorkspaceId(workspaceId).stream().map(StatusSetResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public List<StatusValueResponse> listValues(UUID workspaceId, UUID setId) {
        authorization.requireFieldView(workspaceId);
        sets.findByIdAndWorkspaceId(setId, workspaceId).orElseThrow(() -> ConfigurationExceptions.statusSetNotFound(setId));
        return values.findBySetId(setId).stream().map(StatusValueResponse::from).toList();
    }
}
