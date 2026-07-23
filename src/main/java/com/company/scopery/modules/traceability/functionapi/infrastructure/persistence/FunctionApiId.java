package com.company.scopery.modules.traceability.functionapi.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FunctionApiId implements Serializable {

    @Column(name = "function_id")
    private UUID functionId;

    @Column(name = "api_endpoint_id")
    private UUID apiEndpointId;
}
