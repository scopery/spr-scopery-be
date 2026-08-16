package com.company.scopery.modules.traceability.shared.constant;

import com.company.scopery.platform.storage.StorageLocation;

public final class TraceabilityStorageLocations {

    /** Screen mockup / wireframe images attached to a screen spec. Max 5 MB. */
    public static final StorageLocation SCREEN_MOCKUP =
            StorageLocation.of("traceability", "screens", "mockups");

    /** Component screenshots attached to a component definition. Max 5 MB. */
    public static final StorageLocation COMPONENT_SCREENSHOT =
            StorageLocation.of("traceability", "components", "screenshots");

    /** Requirement attachment images. Max 5 MB. */
    public static final StorageLocation REQUIREMENT_IMAGE =
            StorageLocation.of("traceability", "requirements", "images");

    private TraceabilityStorageLocations() {}
}
