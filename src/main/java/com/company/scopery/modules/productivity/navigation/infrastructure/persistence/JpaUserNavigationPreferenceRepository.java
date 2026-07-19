package com.company.scopery.modules.productivity.navigation.infrastructure.persistence;
import com.company.scopery.modules.productivity.navigation.domain.model.*;
import com.company.scopery.modules.productivity.navigation.infrastructure.mapper.NavigationPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaUserNavigationPreferenceRepository implements UserNavigationPreferenceRepository {
    private final SpringDataUserNavigationPreferenceJpaRepository springData; private final NavigationPersistenceMapper mapper;
    public JpaUserNavigationPreferenceRepository(SpringDataUserNavigationPreferenceJpaRepository springData, NavigationPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public UserNavigationPreference save(UserNavigationPreference p) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(p))); }
    @Override public Optional<UserNavigationPreference> findByWorkspaceAndUser(UUID workspaceId, UUID userId) {
        return springData.findByWorkspaceIdAndUserId(workspaceId, userId).map(mapper::toDomain);
    }
}
