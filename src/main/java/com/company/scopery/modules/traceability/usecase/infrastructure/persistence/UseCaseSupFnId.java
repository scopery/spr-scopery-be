package com.company.scopery.modules.traceability.usecase.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UseCaseSupFnId implements Serializable {

    @Column(name = "use_case_id", nullable = false)
    private UUID useCaseId;

    @Column(name = "function_id", nullable = false)
    private UUID functionId;
}
