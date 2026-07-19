package com.company.scopery.modules.resourcecapacity.usercapacityprofile.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.enums.UserCapacityProfileStatus;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.model.UserCapacityProfile;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.model.UserCapacityProfileRepository;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.infrastructure.mapper.UserCapacityProfilePersistenceMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaUserCapacityProfileRepository implements UserCapacityProfileRepository {

    private final SpringDataUserCapacityProfileJpaRepository springDataRepository;
    private final UserCapacityProfilePersistenceMapper mapper;

    public JpaUserCapacityProfileRepository(SpringDataUserCapacityProfileJpaRepository springDataRepository,
                                            UserCapacityProfilePersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public UserCapacityProfile save(UserCapacityProfile profile) {
        UserCapacityProfileJpaEntity entity = mapper.toJpaEntity(profile);
        UserCapacityProfileJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<UserCapacityProfile> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<UserCapacityProfile> findActiveByWorkspaceMemberId(UUID workspaceMemberId) {
        return springDataRepository
                .findByWorkspaceMemberIdAndCapacityStatus(workspaceMemberId, UserCapacityProfileStatus.ACTIVE.name())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<UserCapacityProfile> findActiveByUserId(UUID userId) {
        return springDataRepository
                .findFirstByUserIdAndCapacityStatus(userId, UserCapacityProfileStatus.ACTIVE.name())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByWorkingCalendarId(UUID workingCalendarId) {
        return springDataRepository.existsByWorkingCalendarId(workingCalendarId);
    }

    @Override
    public PageResult<UserCapacityProfile> search(UUID workspaceId, UUID workspaceMemberId, UUID userId,
                                                   UserCapacityProfileStatus status, PageQuery pageQuery) {
        Specification<UserCapacityProfileJpaEntity> spec =
                buildSearchSpec(workspaceId, workspaceMemberId, userId, status);
        Pageable pageable = toPageable(pageQuery);
        Page<UserCapacityProfile> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }

    private Specification<UserCapacityProfileJpaEntity> buildSearchSpec(UUID workspaceId, UUID workspaceMemberId,
                                                                         UUID userId, UserCapacityProfileStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("workspaceId"), workspaceId));
            if (workspaceMemberId != null) {
                predicates.add(cb.equal(root.get("workspaceMemberId"), workspaceMemberId));
            }
            if (userId != null) {
                predicates.add(cb.equal(root.get("userId"), userId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("capacityStatus"), status.name()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
