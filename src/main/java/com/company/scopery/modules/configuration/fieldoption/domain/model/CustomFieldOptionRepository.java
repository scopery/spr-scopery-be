package com.company.scopery.modules.configuration.fieldoption.domain.model;
import java.util.*; import java.util.UUID;
public interface CustomFieldOptionRepository {
    CustomFieldOption save(CustomFieldOption o);
    Optional<CustomFieldOption> findById(UUID id);
    List<CustomFieldOption> findByFieldId(UUID fieldId);
}
