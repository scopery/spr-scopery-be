package com.company.scopery.modules.airecommendation.domain.model;

import com.company.scopery.modules.airecommendation.domain.enums.AssessmentType;
import com.company.scopery.modules.airecommendation.domain.enums.ImpactDimension;
import com.company.scopery.modules.airecommendation.domain.enums.ImpactDirection;
import com.company.scopery.modules.airecommendation.domain.enums.ImpactSourceMethod;
import com.company.scopery.modules.airecommendation.domain.enums.QualitativeMagnitude;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AiSuggestionImpact(
        UUID id,
        UUID suggestionId,
        ImpactDimension dimension,
        ImpactDirection direction,
        AssessmentType assessmentType,
        BigDecimal numericValue,
        String unitCode,
        QualitativeMagnitude qualitativeMagnitude,
        ImpactSourceMethod sourceMethod,
        String calculationMethodCode,
        String assumptions,
        String sourceType,
        UUID sourceRefId,
        OffsetDateTime createdAt
) {}
