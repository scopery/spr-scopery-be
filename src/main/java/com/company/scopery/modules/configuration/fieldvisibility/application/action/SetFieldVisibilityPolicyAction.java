package com.company.scopery.modules.configuration.fieldvisibility.application.action;
import com.company.scopery.modules.configuration.customfield.domain.model.CustomFieldDefinitionRepository;
import com.company.scopery.modules.configuration.fieldvisibility.application.command.SetFieldVisibilityPolicyCommand;
import com.company.scopery.modules.configuration.fieldvisibility.application.response.FieldVisibilityPolicyResponse;
import com.company.scopery.modules.configuration.fieldvisibility.domain.model.*;
import com.company.scopery.modules.configuration.shared.activity.ConfigurationActivityLogger;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.constant.*;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class SetFieldVisibilityPolicyAction {
    private final CustomFieldDefinitionRepository fields; private final FieldVisibilityPolicyRepository policies;
    private final ConfigurationAuthorizationService authorization; private final ConfigurationActivityLogger activityLogger;
    public SetFieldVisibilityPolicyAction(CustomFieldDefinitionRepository fields, FieldVisibilityPolicyRepository policies,
                                          ConfigurationAuthorizationService authorization, ConfigurationActivityLogger activityLogger) {
        this.fields=fields; this.policies=policies; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public FieldVisibilityPolicyResponse execute(SetFieldVisibilityPolicyCommand c) {
        authorization.requireFieldUpdate(c.workspaceId());
        fields.findByIdAndWorkspaceId(c.fieldId(), c.workspaceId()).orElseThrow(() -> ConfigurationExceptions.fieldNotFound(c.fieldId()));
        var policy = policies.findByFieldAndAudience(c.fieldId(), c.audienceType())
                .map(p -> p.withVisible(Boolean.TRUE.equals(c.visible())))
                .orElseGet(() -> FieldVisibilityPolicy.create(c.workspaceId(), c.fieldId(), c.audienceType(), Boolean.TRUE.equals(c.visible())));
        var saved = policies.save(policy);
        activityLogger.logSuccess(ConfigurationEntityTypes.FIELD_VISIBILITY_POLICY, saved.id(), ConfigurationActivityActions.VISIBILITY_POLICY_SET, "Field visibility policy set");
        return FieldVisibilityPolicyResponse.from(saved);
    }
}
