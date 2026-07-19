package com.company.scopery.modules.clientportal.feedback.domain.model;
import java.util.*;
public interface ClientFeedbackRepository {
    ClientFeedback save(ClientFeedback entity);
    Optional<ClientFeedback> findByIdAndProjectId(UUID id, UUID projectId);
    List<ClientFeedback> findByProjectId(UUID projectId);
}
