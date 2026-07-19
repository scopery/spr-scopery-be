package com.company.scopery.modules.configuration.form.domain.model;
import java.util.*; import java.util.UUID;
public interface CustomFormSectionRepository {
    CustomFormSection save(CustomFormSection s);
    Optional<CustomFormSection> findById(UUID id);
    List<CustomFormSection> findByVersionId(UUID versionId);
    void delete(UUID id);
}
