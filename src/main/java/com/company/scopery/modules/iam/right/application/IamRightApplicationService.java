package com.company.scopery.modules.iam.right.application;

import com.company.scopery.common.pagination.PageRequestUtils;
import com.company.scopery.modules.iam.shared.constant.IamSortFields;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.shared.util.IamEnumParser;
import com.company.scopery.modules.iam.right.application.query.SearchIamRightQuery;
import com.company.scopery.modules.iam.right.application.response.IamRightResponse;
import com.company.scopery.modules.iam.right.domain.IamRight;
import com.company.scopery.modules.iam.right.domain.IamRightRepository;
import com.company.scopery.modules.iam.right.domain.IamRightStatus;
import com.company.scopery.platform.config.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class IamRightApplicationService {

    private final IamRightRepository iamRightRepository;

    public IamRightApplicationService(IamRightRepository iamRightRepository) {
        this.iamRightRepository = iamRightRepository;
    }

    @Transactional(readOnly = true)
    public IamRightResponse getRight(UUID id) {
        IamRight right = iamRightRepository.findById(id)
                .orElseThrow(() -> IamExceptions.iamRightNotFound(id));
        return IamRightResponse.from(right);
    }

    @Cacheable(value = CacheConfig.IAM_RIGHTS,
               key = "#query.keyword() + ':' + #query.module() + ':' + #query.status() + ':' + #query.page() + ':' + #query.size()")
    @Transactional(readOnly = true)
    public Page<IamRightResponse> searchRights(SearchIamRightQuery query) {
        IamRightStatus status = IamEnumParser.parseOptional(
                IamRightStatus.class, query.status(),
                IamErrorCatalog.INVALID_IAM_RIGHT_STATUS.code(), "status");
        var pageable = PageRequestUtils.of(query.page(), query.size(),
                Sort.by(Sort.Direction.ASC, IamSortFields.CODE));
        return iamRightRepository.findAll(query.keyword(), query.module(), status, pageable)
                .map(IamRightResponse::from);
    }
}
