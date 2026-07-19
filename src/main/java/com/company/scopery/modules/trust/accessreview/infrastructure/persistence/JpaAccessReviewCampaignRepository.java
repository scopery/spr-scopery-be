package com.company.scopery.modules.trust.accessreview.infrastructure.persistence;
import com.company.scopery.modules.trust.accessreview.domain.model.*;
import com.company.scopery.modules.trust.accessreview.infrastructure.mapper.AccessReviewPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaAccessReviewCampaignRepository implements AccessReviewCampaignRepository {
    private final SpringDataAccessReviewCampaignJpaRepository spring; private final AccessReviewPersistenceMapper mapper;
    public JpaAccessReviewCampaignRepository(SpringDataAccessReviewCampaignJpaRepository spring, AccessReviewPersistenceMapper mapper){this.spring=spring;this.mapper=mapper;}
    @Override public AccessReviewCampaign save(AccessReviewCampaign c){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(c))); }
    @Override public Optional<AccessReviewCampaign> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<AccessReviewCampaign> findByWorkspaceId(UUID workspaceId){ return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList(); }
}
