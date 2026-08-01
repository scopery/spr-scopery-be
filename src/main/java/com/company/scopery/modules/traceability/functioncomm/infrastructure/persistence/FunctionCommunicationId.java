package com.company.scopery.modules.traceability.functioncomm.infrastructure.persistence;

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
public class FunctionCommunicationId implements Serializable {

    @Column(name = "function_id")
    private UUID functionId;

    @Column(name = "communication_id")
    private UUID communicationId;
}
