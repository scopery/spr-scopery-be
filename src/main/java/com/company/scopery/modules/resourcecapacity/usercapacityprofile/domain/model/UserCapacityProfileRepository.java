package com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.enums.UserCapacityProfileStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserCapacityProfileRepository {

    UserCapacityProfile save(UserCapacityProfile profile);

    Optional<UserCapacityProfile> findById(UUID id);

    List<UserCapacityProfile> findActiveByWorkspaceMemberId(UUID workspaceMemberId);

    Optional<UserCapacityProfile> findActiveByUserId(UUID userId);

    boolean existsByWorkingCalendarId(UUID workingCalendarId);

    PageResult<UserCapacityProfile> search(UUID workspaceId, UUID workspaceMemberId, UUID userId,
                                           UserCapacityProfileStatus status, PageQuery pageQuery);
}
