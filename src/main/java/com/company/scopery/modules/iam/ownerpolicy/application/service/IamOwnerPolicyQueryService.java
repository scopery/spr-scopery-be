package com.company.scopery.modules.iam.ownerpolicy.application.service;

import com.company.scopery.modules.iam.ownerpolicy.application.response.IamOwnerPolicyResponse;
import com.company.scopery.modules.iam.ownerpolicy.domain.model.IamOwnerPolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IamOwnerPolicyQueryService {

    private final IamOwnerPolicyRepository repository;

    public IamOwnerPolicyQueryService(IamOwnerPolicyRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<IamOwnerPolicyResponse> listAll() {
        return repository.findAll().stream().map(IamOwnerPolicyResponse::from).toList();
    }
}
