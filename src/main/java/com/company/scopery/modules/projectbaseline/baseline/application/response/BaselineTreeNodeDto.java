package com.company.scopery.modules.projectbaseline.baseline.application.response;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record BaselineTreeNodeDto(UUID id, String type, String code, String name, Map<String, Object> meta, List<BaselineTreeNodeDto> children) {}
