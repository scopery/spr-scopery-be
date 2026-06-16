package com.company.scopery.modules.iam.right.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface IamRightRepository {
    IamRight save(IamRight right);
    Optional<IamRight> findById(UUID id);
    Optional<IamRight> findByCode(IamRightCode code);
    boolean existsByCode(IamRightCode code);
    Page<IamRight> findAll(String keyword, String module, IamRightStatus status, Pageable pageable);
}
