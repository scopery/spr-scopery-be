package com.company.scopery.modules.iam.user.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.model.IamUserRepository;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import com.company.scopery.modules.iam.user.infrastructure.mapper.IamUserPersistenceMapper;
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
public class JpaIamUserRepository implements IamUserRepository {

    private final SpringDataIamUserJpaRepository springDataRepository;
    private final IamUserPersistenceMapper mapper;

    public JpaIamUserRepository(SpringDataIamUserJpaRepository springDataRepository,
                                 IamUserPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public IamUser save(IamUser user) {
        IamUserJpaEntity entity = mapper.toJpaEntity(user);
        IamUserJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<IamUser> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<IamUser> findByUsername(Username username) {
        return springDataRepository.findByUsername(username.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<IamUser> findByEmail(EmailAddress email) {
        return springDataRepository.findByEmail(email.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsByUsername(Username username) {
        return springDataRepository.existsByUsername(username.value());
    }

    @Override
    public boolean existsByEmail(EmailAddress email) {
        return springDataRepository.existsByEmail(email.value());
    }

    @Override
    public PageResult<IamUser> findAll(String keyword, IamUserStatus status, PageQuery pageQuery) {
        Specification<IamUserJpaEntity> spec = buildSpec(keyword, status);
        Pageable pageable = toPageable(pageQuery);
        Page<IamUser> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }

    private Specification<IamUserJpaEntity> buildSpec(String keyword, IamUserStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("username")), like),
                        cb.like(cb.lower(root.get("email")), like),
                        cb.like(cb.lower(root.get("fullName")), like)
                ));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
