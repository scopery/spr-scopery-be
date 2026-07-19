package com.company.scopery.modules.trust.datasubject.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface DataSubjectIndexRepository {
    DataSubjectIndex save(DataSubjectIndex d);
    Optional<DataSubjectIndex> findById(UUID id);
    List<DataSubjectIndex> findByWorkspaceId(UUID workspaceId);
}
