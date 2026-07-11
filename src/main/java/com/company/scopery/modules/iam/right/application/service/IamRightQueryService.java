package com.company.scopery.modules.iam.right.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.shared.constant.IamSortFields;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.shared.util.IamEnumParser;
import com.company.scopery.modules.iam.right.application.query.SearchIamRightQuery;
import com.company.scopery.modules.iam.right.application.response.IamRightResponse;
import com.company.scopery.modules.iam.right.domain.model.IamRight;
import com.company.scopery.modules.iam.right.domain.model.IamRightRepository;
import com.company.scopery.modules.iam.right.domain.enums.IamRightStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class IamRightQueryService {

    private final IamRightRepository iamRightRepository;

    public IamRightQueryService(IamRightRepository iamRightRepository) {
        this.iamRightRepository = iamRightRepository;
    }

    @Transactional(readOnly = true)
    public IamRightResponse getRight(UUID id) {
        IamRight right = iamRightRepository.findById(id)
                .orElseThrow(() -> IamExceptions.iamRightNotFound(id));
        return IamRightResponse.from(right);
    }

    @Transactional(readOnly = true)
    public PageResult<IamRightResponse> searchRights(SearchIamRightQuery query) {
        IamRightStatus status = IamEnumParser.parseOptional(
                IamRightStatus.class, query.status(),
                IamErrorCatalog.INVALID_IAM_RIGHT_STATUS.code(), "status");
        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), IamSortFields.CODE, true);
        return iamRightRepository.findAll(query.keyword(), query.module(), status, pageQuery)
                .map(IamRightResponse::from);
    }
}
