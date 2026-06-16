package com.company.scopery.modules.iam.grant.application;

import com.company.scopery.common.pagination.PageRequestUtils;
import com.company.scopery.modules.iam.grant.application.command.AddIamGrantRightCommand;
import com.company.scopery.modules.iam.grant.application.command.CreateIamAccessGrantCommand;
import com.company.scopery.modules.iam.grant.application.command.RemoveIamGrantRightCommand;
import com.company.scopery.modules.iam.grant.application.query.SearchIamAccessGrantQuery;
import com.company.scopery.modules.iam.grant.application.response.IamAccessGrantResponse;
import com.company.scopery.modules.iam.grant.application.response.IamAccessGrantRightResponse;
import com.company.scopery.modules.iam.grant.domain.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.IamAccessGrantRepository;
import com.company.scopery.modules.iam.grant.domain.IamAccessGrantRight;
import com.company.scopery.modules.iam.grant.domain.IamAccessGrantRightRepository;
import com.company.scopery.modules.iam.grant.domain.IamAccessGrantStatus;
import com.company.scopery.modules.iam.grant.domain.IamGrantEffect;
import com.company.scopery.modules.iam.grant.domain.IamGrantScopeType;
import com.company.scopery.modules.iam.grant.domain.IamSubjectType;
import com.company.scopery.modules.iam.resource.domain.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.IamAuthResourceRepository;
import com.company.scopery.modules.iam.resource.domain.IamResourceStatus;
import com.company.scopery.modules.iam.right.domain.IamRight;
import com.company.scopery.modules.iam.right.domain.IamRightRepository;
import com.company.scopery.modules.iam.right.domain.IamRightStatus;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.constant.IamSortFields;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.shared.util.IamEnumParser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class IamAccessGrantApplicationService {

    private final IamAccessGrantRepository grantRepository;
    private final IamAccessGrantRightRepository grantRightRepository;
    private final IamAuthResourceRepository resourceRepository;
    private final IamRightRepository rightRepository;
    private final IamActivityLogger activityLogger;

    public IamAccessGrantApplicationService(IamAccessGrantRepository grantRepository,
                                             IamAccessGrantRightRepository grantRightRepository,
                                             IamAuthResourceRepository resourceRepository,
                                             IamRightRepository rightRepository,
                                             IamActivityLogger activityLogger) {
        this.grantRepository = grantRepository;
        this.grantRightRepository = grantRightRepository;
        this.resourceRepository = resourceRepository;
        this.rightRepository = rightRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public IamAccessGrantResponse createGrant(CreateIamAccessGrantCommand command) {
        IamSubjectType subjectType = IamEnumParser.parseRequired(
                IamSubjectType.class, command.subjectType(),
                IamErrorCatalog.INVALID_IAM_SUBJECT_TYPE.code(), "subjectType");

        IamAuthResource resource = resourceRepository.findById(command.resourceId())
                .orElseThrow(() -> IamExceptions.iamAuthResourceNotFound(command.resourceId()));

        if (resource.status() != IamResourceStatus.ACTIVE) {
            throw IamExceptions.iamAuthResourceInactiveCannotBeUsed(resource.code().value());
        }

        if (grantRepository.existsBySubjectIdAndResourceId(command.subjectId(), command.resourceId())) {
            throw IamExceptions.iamAccessGrantAlreadyExists(command.subjectId(), command.resourceId());
        }

        IamGrantEffect effect = IamEnumParser.parseOptional(
                IamGrantEffect.class, command.effect(),
                IamErrorCatalog.INVALID_IAM_GRANT_EFFECT.code(), "effect");
        IamGrantScopeType scopeType = IamEnumParser.parseOptional(
                IamGrantScopeType.class, command.scopeType(),
                IamErrorCatalog.INVALID_IAM_GRANT_SCOPE_TYPE.code(), "scopeType");
        IamAccessGrant grant = IamAccessGrant.create(subjectType, command.subjectId(),
                command.resourceId(), command.roleId(),
                effect == null ? IamGrantEffect.ALLOW : effect,
                scopeType, command.scopeRefId(), command.workspaceId(), command.grantedBy());
        IamAccessGrant saved = grantRepository.save(grant);

        activityLogger.logSuccess(IamEntityTypes.IAM_ACCESS_GRANT, saved.id(),
                IamActivityActions.CREATE_IAM_ACCESS_GRANT,
                "Access grant created for subject " + saved.subjectId() + " on resource " + saved.resourceId());

        return IamAccessGrantResponse.from(saved);
    }

    @Transactional
    public IamAccessGrantResponse revokeGrant(UUID id) {
        IamAccessGrant grant = findOrThrow(id);
        IamAccessGrant saved = grantRepository.save(grant.revoke());

        activityLogger.logSuccess(IamEntityTypes.IAM_ACCESS_GRANT, saved.id(),
                IamActivityActions.REVOKE_IAM_ACCESS_GRANT,
                "Access grant revoked: " + saved.id());

        return IamAccessGrantResponse.from(saved);
    }

    @Transactional
    public IamAccessGrantRightResponse addRight(AddIamGrantRightCommand command) {
        IamAccessGrant grant = findOrThrow(command.grantId());

        if (grant.status() == IamAccessGrantStatus.REVOKED) {
            throw IamExceptions.iamAccessGrantRevokedCannotBeModified(grant.id());
        }

        IamRight right = rightRepository.findById(command.rightId())
                .orElseThrow(() -> IamExceptions.iamRightNotFound(command.rightId()));

        if (right.status() != IamRightStatus.ACTIVE) {
            throw IamExceptions.rightInactiveCannotBeUsed(right.code().value());
        }

        if (grantRightRepository.existsByGrantIdAndRightId(command.grantId(), command.rightId())) {
            throw IamExceptions.iamAccessGrantRightAlreadyExists(command.grantId(), command.rightId());
        }

        IamAccessGrantRight grantRight = IamAccessGrantRight.create(command.grantId(), command.rightId());
        IamAccessGrantRight saved = grantRightRepository.save(grantRight);

        activityLogger.logSuccess(IamEntityTypes.IAM_ACCESS_GRANT, grant.id(),
                IamActivityActions.ADD_IAM_GRANT_RIGHT,
                "Right " + command.rightId() + " added to grant " + command.grantId());

        return IamAccessGrantRightResponse.from(saved);
    }

    @Transactional
    public void removeRight(RemoveIamGrantRightCommand command) {
        IamAccessGrant grant = findOrThrow(command.grantId());

        if (grant.status() == IamAccessGrantStatus.REVOKED) {
            throw IamExceptions.iamAccessGrantRevokedCannotBeModified(grant.id());
        }

        if (!grantRightRepository.existsByGrantIdAndRightId(command.grantId(), command.rightId())) {
            throw IamExceptions.iamAccessGrantRightNotFound(command.grantId(), command.rightId());
        }

        grantRightRepository.deleteByGrantIdAndRightId(command.grantId(), command.rightId());

        activityLogger.logSuccess(IamEntityTypes.IAM_ACCESS_GRANT, grant.id(),
                IamActivityActions.REMOVE_IAM_GRANT_RIGHT,
                "Right " + command.rightId() + " removed from grant " + command.grantId());
    }

    @Transactional(readOnly = true)
    public IamAccessGrantResponse getGrant(UUID id) {
        return IamAccessGrantResponse.from(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<IamAccessGrantRightResponse> getGrantRights(UUID grantId) {
        findOrThrow(grantId);
        return grantRightRepository.findByGrantId(grantId).stream()
                .map(IamAccessGrantRightResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<IamAccessGrantResponse> searchGrants(SearchIamAccessGrantQuery query) {
        IamAccessGrantStatus status = IamEnumParser.parseOptional(
                IamAccessGrantStatus.class, query.status(),
                IamErrorCatalog.INVALID_IAM_ACCESS_GRANT_STATUS.code(), "status");
        var pageable = PageRequestUtils.of(query.page(), query.size(),
                Sort.by(Sort.Direction.DESC, IamSortFields.CREATED_AT));
        return grantRepository.findAll(query.subjectId(), query.resourceId(), status, pageable)
                .map(IamAccessGrantResponse::from);
    }

    private IamAccessGrant findOrThrow(UUID id) {
        return grantRepository.findById(id)
                .orElseThrow(() -> IamExceptions.iamAccessGrantNotFound(id));
    }
}
