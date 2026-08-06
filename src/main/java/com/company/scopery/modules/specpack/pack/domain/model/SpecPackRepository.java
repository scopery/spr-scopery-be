package com.company.scopery.modules.specpack.pack.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.specpack.pack.domain.enums.SpecPackStatus;
import com.company.scopery.modules.specpack.pack.domain.enums.SpecPackType;

import java.util.Optional;
import java.util.UUID;

public interface SpecPackRepository {
    SpecPack save(SpecPack specPack);
    Optional<SpecPack> findById(UUID id);
    PageResult<SpecPack> findAllByProject(UUID projectId, String keyword, SpecPackType packType, SpecPackStatus status, PageQuery pageQuery);
}
