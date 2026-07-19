package com.company.scopery.modules.trust.privacy.infrastructure.persistence;
import com.company.scopery.modules.trust.privacy.domain.model.*;
import com.company.scopery.modules.trust.privacy.infrastructure.mapper.PrivacyExportPackagePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaPrivacyExportPackageRepository implements PrivacyExportPackageRepository {
    private final SpringDataPrivacyExportPackageJpaRepository spring; private final PrivacyExportPackagePersistenceMapper mapper;
    public JpaPrivacyExportPackageRepository(SpringDataPrivacyExportPackageJpaRepository spring, PrivacyExportPackagePersistenceMapper mapper){this.spring=spring;this.mapper=mapper;}
    @Override public PrivacyExportPackage save(PrivacyExportPackage p){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(p))); }
    @Override public Optional<PrivacyExportPackage> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<PrivacyExportPackage> findByWorkspaceId(UUID workspaceId){ return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList(); }
}
