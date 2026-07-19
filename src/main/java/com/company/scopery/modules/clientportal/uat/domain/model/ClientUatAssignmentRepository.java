package com.company.scopery.modules.clientportal.uat.domain.model;
import java.util.*;
public interface ClientUatAssignmentRepository {
    ClientUatAssignment save(ClientUatAssignment entity);
    List<ClientUatAssignment> findByProjectId(UUID projectId);
}
