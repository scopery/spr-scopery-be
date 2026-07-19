package com.company.scopery.modules.configuration.fieldvalue.application.action;
import com.company.scopery.modules.configuration.customfield.domain.model.CustomFieldDefinitionRepository;
import com.company.scopery.modules.configuration.fieldvalue.application.response.CustomFieldValueResponse;
import com.company.scopery.modules.configuration.fieldvalue.domain.model.*;
import com.company.scopery.modules.configuration.fieldvalue.application.command.UpsertFieldValuesCommand;
import com.company.scopery.modules.configuration.shared.activity.ConfigurationActivityLogger;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.constant.*;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Component
public class UpsertFieldValuesAction {
    private final CustomFieldDefinitionRepository fields; private final CustomFieldValueRepository values;
    private final ConfigurationAuthorizationService authorization; private final ConfigurationActivityLogger activityLogger;
    public UpsertFieldValuesAction(CustomFieldDefinitionRepository fields, CustomFieldValueRepository values,
                                   ConfigurationAuthorizationService authorization, ConfigurationActivityLogger activityLogger) {
        this.fields=fields; this.values=values; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public List<CustomFieldValueResponse> execute(UpsertFieldValuesCommand c) {
        authorization.requireFieldUpdate(c.workspaceId());
        List<CustomFieldValueResponse> out = new ArrayList<>();
        for (var item : c.values()) {
            var field = fields.findByIdAndWorkspaceId(item.fieldId(), c.workspaceId()).orElseThrow(() -> ConfigurationExceptions.fieldNotFound(item.fieldId()));
            if (!field.objectTypeCode().equals(c.objectType())) throw ConfigurationExceptions.validationFailed("Field objectType mismatch");
            var existing = values.findByFieldAndTarget(item.fieldId(), c.targetId());
            var v = values.save(CustomFieldValue.upsert(
                    existing.map(CustomFieldValue::id).orElse(null), c.workspaceId(), c.objectType(), c.targetId(), item.fieldId(),
                    item.valueText(), item.valueLongText(), item.valueNumber(), item.valueDecimal(), item.valueBoolean(),
                    item.valueDate(), item.valueDatetime(), item.valueJson(), item.valueOptionIds(),
                    existing.map(CustomFieldValue::createdAt).orElse(null)));
            activityLogger.logSuccess(ConfigurationEntityTypes.FIELD_VALUE, v.id(), ConfigurationActivityActions.VALUE_UPDATED, "Field value upserted");
            out.add(CustomFieldValueResponse.from(v));
        }
        return out;
    }
}
