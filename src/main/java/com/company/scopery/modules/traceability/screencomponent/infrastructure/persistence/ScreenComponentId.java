package com.company.scopery.modules.traceability.screencomponent.infrastructure.persistence;

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
public class ScreenComponentId implements Serializable {

    @Column(name = "screen_id")
    private UUID screenId;

    @Column(name = "component_id")
    private UUID componentId;
}
