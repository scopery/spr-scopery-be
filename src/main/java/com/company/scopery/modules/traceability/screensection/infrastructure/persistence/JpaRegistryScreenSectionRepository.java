package com.company.scopery.modules.traceability.screensection.infrastructure.persistence;
import com.company.scopery.modules.traceability.screensection.domain.model.*;
import com.company.scopery.modules.traceability.screensection.infrastructure.mapper.RegistryScreenSectionPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaRegistryScreenSectionRepository implements RegistryScreenSectionRepository {
    private final SpringDataRegistryScreenSectionJpaRepository springData;
    private final RegistryScreenSectionPersistenceMapper mapper;
    public JpaRegistryScreenSectionRepository(SpringDataRegistryScreenSectionJpaRepository springData, RegistryScreenSectionPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public RegistryScreenSection save(RegistryScreenSection e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<RegistryScreenSection> findByIdAndWorkspaceId(UUID id, UUID workspaceId) {
        return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain);
    }
    @Override public List<RegistryScreenSection> findByScreenId(UUID screenId) {
        return springData.findByScreenIdOrderByDisplayOrderAsc(screenId).stream().map(mapper::toDomain).toList();
    }
    @Override public void delete(UUID id, UUID workspaceId) { springData.deleteByIdAndWorkspaceId(id, workspaceId); }
}
