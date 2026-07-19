package com.company.scopery.modules.integrationhub.exportprofile.infrastructure.persistence;
import com.company.scopery.modules.integrationhub.exportprofile.domain.model.ExportProfile;
import com.company.scopery.modules.integrationhub.exportprofile.domain.model.ExportProfileRepository;
import com.company.scopery.modules.integrationhub.exportprofile.infrastructure.mapper.ExportProfilePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaExportProfileRepository implements ExportProfileRepository {
    private final SpringDataExportProfileJpaRepository spring;
    private final ExportProfilePersistenceMapper mapper;
    public JpaExportProfileRepository(SpringDataExportProfileJpaRepository spring, ExportProfilePersistenceMapper mapper){
        this.spring = spring; this.mapper = mapper;
    }
    @Override public ExportProfile save(ExportProfile p){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(p))); }
    @Override public Optional<ExportProfile> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public Optional<ExportProfile> findByWorkspaceIdAndProfileCode(UUID workspaceId, String profileCode){ return spring.findByWorkspaceIdAndProfileCode(workspaceId, profileCode).map(mapper::toDomain); }
    @Override public List<ExportProfile> findByWorkspaceId(UUID workspaceId){ return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList(); }
    @Override public boolean existsByWorkspaceIdAndProfileCode(UUID workspaceId, String profileCode){ return spring.existsByWorkspaceIdAndProfileCode(workspaceId, profileCode); }
}
