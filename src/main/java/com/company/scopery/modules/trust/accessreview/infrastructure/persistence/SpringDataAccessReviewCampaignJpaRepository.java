package com.company.scopery.modules.trust.accessreview.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataAccessReviewCampaignJpaRepository extends JpaRepository<AccessReviewCampaignJpaEntity, UUID> {
    List<AccessReviewCampaignJpaEntity> findByWorkspaceId(UUID workspaceId);
}
