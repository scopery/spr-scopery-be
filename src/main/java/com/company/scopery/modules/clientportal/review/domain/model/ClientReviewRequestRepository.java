package com.company.scopery.modules.clientportal.review.domain.model;
import java.util.*;
public interface ClientReviewRequestRepository {
    ClientReviewRequest save(ClientReviewRequest e);
    Optional<ClientReviewRequest> findByIdAndProjectId(UUID id, UUID projectId);
    List<ClientReviewRequest> findByProjectId(UUID projectId);
}
