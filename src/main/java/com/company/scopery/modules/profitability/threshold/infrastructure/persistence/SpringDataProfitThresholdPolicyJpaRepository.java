package com.company.scopery.modules.profitability.threshold.infrastructure.persistence;

import org.springframework.data.repository.NoRepositoryBean;

// @NoRepositoryBean prevents Spring Data from registering a duplicate bean for this alias.
// Use SpringDataProfitThresholdJpaRepository for actual injection.
@NoRepositoryBean
public interface SpringDataProfitThresholdPolicyJpaRepository extends SpringDataProfitThresholdJpaRepository {
}
