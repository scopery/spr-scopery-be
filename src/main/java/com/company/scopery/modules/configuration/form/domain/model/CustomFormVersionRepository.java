package com.company.scopery.modules.configuration.form.domain.model;
import java.util.*; import java.util.UUID;
public interface CustomFormVersionRepository {
    CustomFormVersion save(CustomFormVersion v);
    Optional<CustomFormVersion> findById(UUID id);
    List<CustomFormVersion> findByFormId(UUID formId);
    int nextVersionNumber(UUID formId);
}
