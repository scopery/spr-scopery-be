package com.company.scopery.modules.servicesupport.knowledgelink.infrastructure.persistence;
import com.company.scopery.modules.servicesupport.knowledgelink.domain.model.SupportKnowledgeLink;
import com.company.scopery.modules.servicesupport.knowledgelink.domain.model.SupportKnowledgeLinkRepository;
import com.company.scopery.modules.servicesupport.knowledgelink.infrastructure.mapper.SupportKnowledgeLinkPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaSupportKnowledgeLinkRepository implements SupportKnowledgeLinkRepository {
    private final SpringDataSupportKnowledgeLinkJpaRepository spring;
    private final SupportKnowledgeLinkPersistenceMapper mapper;
    public JpaSupportKnowledgeLinkRepository(SpringDataSupportKnowledgeLinkJpaRepository spring, SupportKnowledgeLinkPersistenceMapper mapper){
        this.spring=spring; this.mapper=mapper;
    }
    @Override public SupportKnowledgeLink save(SupportKnowledgeLink d){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(d))); }
    @Override public Optional<SupportKnowledgeLink> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<SupportKnowledgeLink> findByWorkspaceId(UUID w){ return spring.findByWorkspaceId(w).stream().map(mapper::toDomain).toList(); }
    @Override public List<SupportKnowledgeLink> findBySupportCaseId(UUID c){ return spring.findBySupportCaseId(c).stream().map(mapper::toDomain).toList(); }
}
