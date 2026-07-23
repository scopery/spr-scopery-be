package com.company.scopery.modules.traceability.functionscreen.infrastructure.persistence;

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
public class FunctionScreenId implements Serializable {

    @Column(name = "function_id")
    private UUID functionId;

    @Column(name = "screen_id")
    private UUID screenId;
}
