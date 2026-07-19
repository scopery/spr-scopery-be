package com.company.scopery.modules.configuration.form.domain.model;
import java.util.*; import java.util.UUID;
public interface CustomFormFieldRepository {
    CustomFormField save(CustomFormField f);
    Optional<CustomFormField> findById(UUID id);
    List<CustomFormField> findByVersionId(UUID versionId);
    void delete(UUID id);
}
