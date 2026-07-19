package com.company.scopery.modules.airecommendation.application.service;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.modules.airecommendation.application.compatibility.Phase21SuggestionCompatibilityQueryService;
import com.company.scopery.modules.airecommendation.application.port.RecommendationEvidenceAccessValidator;
import com.company.scopery.modules.airecommendation.application.query.ListEntitySuggestionsQuery;
import com.company.scopery.modules.airecommendation.application.query.ListProjectSuggestionsQuery;
import com.company.scopery.modules.airecommendation.application.response.SuggestionDetailResponse;
import com.company.scopery.modules.airecommendation.application.response.SuggestionSummaryResponse;
import com.company.scopery.modules.airecommendation.domain.enums.AccessValidationResult;
import com.company.scopery.modules.airecommendation.domain.enums.SuggestionStatus;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestion;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionEvidence;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionImpact;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionItem;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionReview;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionEvidenceRepository;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionImpactRepository;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionItemRepository;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionRepository;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionReviewRepository;
import com.company.scopery.modules.airecommendation.domain.value.SuggestionRef;
import com.company.scopery.modules.airecommendation.shared.error.AiRecommendationExceptions;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SuggestionQueryService {

    private final AiSuggestionRepository suggestionRepository;
    private final AiSuggestionItemRepository itemRepository;
    private final AiSuggestionEvidenceRepository evidenceRepository;
    private final AiSuggestionImpactRepository impactRepository;
    private final AiSuggestionReviewRepository reviewRepository;
    private final Phase21SuggestionCompatibilityQueryService phase21QueryService;
    private final RecommendationEvidenceAccessValidator evidenceAccessValidator;

    public SuggestionQueryService(AiSuggestionRepository suggestionRepository,
                                   AiSuggestionItemRepository itemRepository,
                                   AiSuggestionEvidenceRepository evidenceRepository,
                                   AiSuggestionImpactRepository impactRepository,
                                   AiSuggestionReviewRepository reviewRepository,
                                   Phase21SuggestionCompatibilityQueryService phase21QueryService,
                                   RecommendationEvidenceAccessValidator evidenceAccessValidator) {
        this.suggestionRepository = suggestionRepository;
        this.itemRepository = itemRepository;
        this.evidenceRepository = evidenceRepository;
        this.impactRepository = impactRepository;
        this.reviewRepository = reviewRepository;
        this.phase21QueryService = phase21QueryService;
        this.evidenceAccessValidator = evidenceAccessValidator;
    }

    @Transactional(readOnly = true)
    public PageResponse<SuggestionSummaryResponse> listForProject(ListProjectSuggestionsQuery q) {
        List<SuggestionStatus> statusFilter = parseStatusFilter(q.statusFilter());
        Page<AiSuggestion> page = suggestionRepository.findByProjectWithFilters(
                q.workspaceId(), q.projectId(), statusFilter,
                q.severityFilter(), q.packCode(), q.type(), q.targetEntityType(),
                q.includeExpired(), q.pageable());

        List<SuggestionSummaryResponse> items = new ArrayList<>(
                page.getContent().stream().map(this::toSummary).toList());

        if (q.includeLegacyPhase21()) {
            Page<AiSuggestion> p21Page = phase21QueryService.listForProject(
                    q.projectId(), q.workspaceId(), q.pageable());
            items.addAll(p21Page.getContent().stream().map(this::toSummary).toList());
        }

        return new PageResponse<>(items, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isFirst(), page.isLast());
    }

    @Transactional(readOnly = true)
    public PageResponse<SuggestionSummaryResponse> listForEntity(ListEntitySuggestionsQuery q) {
        Page<AiSuggestion> page = suggestionRepository.findByEntity(
                q.workspaceId(), q.entityType(), q.entityId(), q.projectId(), q.pageable());
        return PageResponse.from(page.map(this::toSummary));
    }

    @Transactional(readOnly = true)
    public SuggestionDetailResponse getDetail(String rawRef, UUID workspaceId, UUID actorId) {
        SuggestionRef ref = SuggestionRef.parse(rawRef);

        AiSuggestion suggestion;
        if (ref.isPhase43()) {
            suggestion = suggestionRepository.findById(ref.uuid())
                    .filter(s -> workspaceId.equals(s.workspaceId()))
                    .orElseThrow(() -> AiRecommendationExceptions.suggestionNotFound(rawRef));
        } else {
            suggestion = phase21QueryService.getByLegacyId(ref.uuid())
                    .orElseThrow(() -> AiRecommendationExceptions.suggestionNotFound(rawRef));
        }

        List<AiSuggestionItem> items = itemRepository.findBySuggestionId(suggestion.id());
        List<AiSuggestionEvidence> rawEvidence = evidenceRepository.findBySuggestionId(suggestion.id());
        List<AiSuggestionImpact> impacts = impactRepository.findBySuggestionId(suggestion.id());
        List<AiSuggestionReview> reviews = reviewRepository.findBySuggestionId(suggestion.id());

        // Redact evidence where access is denied
        List<SuggestionDetailResponse.EvidenceResponse> filteredEvidence = rawEvidence.stream()
                .filter(e -> {
                    AccessValidationResult result = evidenceAccessValidator.validate(actorId, e);
                    return result == AccessValidationResult.ALLOWED;
                })
                .map(this::toEvidenceResponse)
                .toList();

        return new SuggestionDetailResponse(
                toSummary(suggestion),
                suggestion.reason(),
                items.stream().map(this::toItemResponse).toList(),
                filteredEvidence,
                impacts.stream().map(this::toImpactResponse).toList(),
                reviews.stream().map(this::toReviewResponse).toList(),
                null,
                null);
    }

    private SuggestionSummaryResponse toSummary(AiSuggestion s) {
        return new SuggestionSummaryResponse(
                (s.sourceSystem() != null && s.sourceSystem().name().equals("PHASE21") ? "p21:" : "p43:") + s.id(),
                s.sourceSystem() != null ? s.sourceSystem().name() : null,
                s.projectId() != null ? s.projectId().toString() : null,
                s.suggestionType(),
                s.category(),
                s.severity() != null ? s.severity().name() : null,
                s.status() != null ? s.status().name() : null,
                s.title(),
                s.summary(),
                s.targetEntityType() != null ? new SuggestionSummaryResponse.TargetInfo(
                        s.targetEntityType(),
                        s.targetEntityId() != null ? s.targetEntityId().toString() : null,
                        s.targetVersionToken(),
                        null) : null,
                s.confidenceValue() != null ? new SuggestionSummaryResponse.ConfidenceInfo(
                        s.confidenceMethod() != null ? s.confidenceMethod().name() : null,
                        s.confidenceValue().doubleValue(),
                        s.confidenceLabel() != null ? s.confidenceLabel().name() : null) : null,
                s.riskLevel(),
                s.occurrenceCount(),
                s.createdAt(),
                s.expiresAt(),
                s.version());
    }

    private SuggestionDetailResponse.SuggestionItemResponse toItemResponse(AiSuggestionItem item) {
        return new SuggestionDetailResponse.SuggestionItemResponse(
                item.id().toString(), item.ordinal(),
                item.operation() != null ? item.operation().name() : null,
                item.targetEntityType(),
                item.targetEntityId() != null ? item.targetEntityId().toString() : null,
                item.schemaCode(), item.schemaVersion(), item.proposedPayload(),
                item.requiredTargetCapabilityCode(), item.confirmationRequired(),
                item.baselineImpact() != null ? item.baselineImpact().name() : null);
    }

    private SuggestionDetailResponse.EvidenceResponse toEvidenceResponse(AiSuggestionEvidence e) {
        return new SuggestionDetailResponse.EvidenceResponse(
                e.id().toString(), e.ordinal(),
                e.evidenceType() != null ? e.evidenceType().name() : null,
                e.supportStrength() != null ? e.supportStrength().name() : null,
                e.sourceType(),
                e.sourceRefId() != null ? e.sourceRefId().toString() : null,
                e.title(), e.quotedFragment(), e.appRoute(),
                e.accessValidationResult() != null ? e.accessValidationResult().name() : null);
    }

    private SuggestionDetailResponse.ImpactResponse toImpactResponse(AiSuggestionImpact i) {
        return new SuggestionDetailResponse.ImpactResponse(
                i.dimension() != null ? i.dimension().name() : null,
                i.direction() != null ? i.direction().name() : null,
                i.assessmentType() != null ? i.assessmentType().name() : null,
                i.numericValue() != null ? i.numericValue().doubleValue() : null,
                i.unitCode(),
                i.qualitativeMagnitude() != null ? i.qualitativeMagnitude().name() : null,
                i.sourceMethod() != null ? i.sourceMethod().name() : null);
    }

    private SuggestionDetailResponse.ReviewResponse toReviewResponse(AiSuggestionReview r) {
        return new SuggestionDetailResponse.ReviewResponse(
                r.id().toString(),
                r.actorId() != null ? r.actorId().toString() : null,
                r.decision() != null ? r.decision().name() : null,
                r.fromStatus() != null ? r.fromStatus().name() : null,
                r.toStatus() != null ? r.toStatus().name() : null,
                r.reasonCode(), r.comment(), r.createdAt());
    }

    private List<SuggestionStatus> parseStatusFilter(List<String> raw) {
        if (raw == null || raw.isEmpty()) return List.of();
        return raw.stream().map(s -> {
            try {
                return SuggestionStatus.valueOf(s.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }).filter(java.util.Objects::nonNull).collect(Collectors.toList());
    }
}
