package com.company.scopery.modules.workspace.joinrequest.infrastructure.persistence;

import com.company.scopery.modules.workspace.joinrequest.domain.model.WorkspaceJoinRequest;
import com.company.scopery.modules.workspace.joinrequest.domain.model.WorkspaceJoinRequestRepository;
import com.company.scopery.modules.workspace.joinrequest.infrastructure.mapper.WorkspaceJoinRequestPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaWorkspaceJoinRequestRepository implements WorkspaceJoinRequestRepository {

    private final SpringDataWorkspaceJoinRequestJpaRepository springDataRepository;
    private final WorkspaceJoinRequestPersistenceMapper mapper;

    public JpaWorkspaceJoinRequestRepository(SpringDataWorkspaceJoinRequestJpaRepository springDataRepository,
                                              WorkspaceJoinRequestPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public WorkspaceJoinRequest save(WorkspaceJoinRequest request) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toJpaEntity(request)));
    }

    @Override
    public Optional<WorkspaceJoinRequest> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<WorkspaceJoinRequest> findPendingByWorkspaceAndUser(UUID workspaceId, UUID userId) {
        return springDataRepository.findPendingByWorkspaceAndUser(workspaceId, userId)
                .map(mapper::toDomain);
    }

    @Override
    public List<WorkspaceJoinRequest> findByWorkspaceId(UUID workspaceId, String status) {
        if (status != null && !status.isBlank()) {
            return springDataRepository.findByWorkspaceIdAndStatus(workspaceId, status)
                    .stream().map(mapper::toDomain).toList();
        }
        return springDataRepository.findByWorkspaceId(workspaceId)
                .stream().map(mapper::toDomain).toList();
    }
}
