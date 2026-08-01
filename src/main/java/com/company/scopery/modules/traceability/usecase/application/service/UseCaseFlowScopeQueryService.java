package com.company.scopery.modules.traceability.usecase.application.service;

import com.company.scopery.modules.traceability.apiendpoint.domain.model.RegistryApiEndpoint;
import com.company.scopery.modules.traceability.apiendpoint.domain.model.RegistryApiEndpointRepository;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponent;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponentRepository;
import com.company.scopery.modules.traceability.dataentity.domain.model.RegistryDataEntity;
import com.company.scopery.modules.traceability.dataentity.domain.model.RegistryDataEntityRepository;
import com.company.scopery.modules.traceability.funcitemanchor.domain.enums.AnchorNodeType;
import com.company.scopery.modules.traceability.funcitemanchor.domain.model.FunctionalItemAnchor;
import com.company.scopery.modules.traceability.funcitemanchor.domain.model.FunctionalItemAnchorRepository;
import com.company.scopery.modules.traceability.functionapi.domain.model.FunctionApi;
import com.company.scopery.modules.traceability.functionapi.domain.model.FunctionApiRepository;
import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItem;
import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItemRepository;
import com.company.scopery.modules.traceability.functionscreen.domain.model.FunctionScreen;
import com.company.scopery.modules.traceability.functionscreen.domain.model.FunctionScreenRepository;
import com.company.scopery.modules.traceability.screen.domain.model.RegistryScreen;
import com.company.scopery.modules.traceability.screen.domain.model.RegistryScreenRepository;
import com.company.scopery.modules.traceability.screencomponent.domain.model.ScreenComponent;
import com.company.scopery.modules.traceability.screencomponent.domain.model.ScreenComponentRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.usecase.application.query.GetPrimaryFunctionChangeImpactQuery;
import com.company.scopery.modules.traceability.usecase.application.query.GetUseCaseFlowScopeQuery;
import com.company.scopery.modules.traceability.usecase.application.query.ListUseCaseMentionOptionsQuery;
import com.company.scopery.modules.traceability.usecase.application.response.PrimaryFunctionChangeImpactResponse;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseFlowScopeResponse;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseMentionOptionsResponse;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCase;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseFlowRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseFlowStep;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseFlowStepRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Orchestrates Function-scoped flow mention reads via domain repositories.
 * No EntityManager / native SQL in this layer.
 */
@Service
public class UseCaseFlowScopeQueryService {

    private final UseCaseRepository useCaseRepo;
    private final FunctionalItemRepository functionalItemRepo;
    private final FunctionScreenRepository functionScreenRepo;
    private final FunctionApiRepository functionApiRepo;
    private final ScreenComponentRepository screenComponentRepo;
    private final FunctionalItemAnchorRepository anchorRepo;
    private final RegistryScreenRepository screenRepo;
    private final RegistryAppComponentRepository componentRepo;
    private final RegistryApiEndpointRepository apiRepo;
    private final RegistryDataEntityRepository entityRepo;
    private final UseCaseFlowRepository flowRepo;
    private final UseCaseFlowStepRepository stepRepo;
    private final TraceabilityAuthorizationService authorization;
    private final ObjectMapper objectMapper;

    public UseCaseFlowScopeQueryService(
            UseCaseRepository useCaseRepo,
            FunctionalItemRepository functionalItemRepo,
            FunctionScreenRepository functionScreenRepo,
            FunctionApiRepository functionApiRepo,
            ScreenComponentRepository screenComponentRepo,
            FunctionalItemAnchorRepository anchorRepo,
            RegistryScreenRepository screenRepo,
            RegistryAppComponentRepository componentRepo,
            RegistryApiEndpointRepository apiRepo,
            RegistryDataEntityRepository entityRepo,
            UseCaseFlowRepository flowRepo,
            UseCaseFlowStepRepository stepRepo,
            TraceabilityAuthorizationService authorization,
            ObjectMapper objectMapper) {
        this.useCaseRepo = useCaseRepo;
        this.functionalItemRepo = functionalItemRepo;
        this.functionScreenRepo = functionScreenRepo;
        this.functionApiRepo = functionApiRepo;
        this.screenComponentRepo = screenComponentRepo;
        this.anchorRepo = anchorRepo;
        this.screenRepo = screenRepo;
        this.componentRepo = componentRepo;
        this.apiRepo = apiRepo;
        this.entityRepo = entityRepo;
        this.flowRepo = flowRepo;
        this.stepRepo = stepRepo;
        this.authorization = authorization;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public UseCaseFlowScopeResponse getFlowScope(GetUseCaseFlowScopeQuery query) {
        authorization.requireView(query.projectId());
        UseCase uc = requireUseCase(query.projectId(), query.useCaseId());

        if (uc.primaryFunctionId() == null) {
            return new UseCaseFlowScopeResponse(query.useCaseId(), null, List.of(), List.of(), List.of());
        }

        FunctionalItem fn = functionalItemRepo.findByIdAndProjectId(uc.primaryFunctionId(), query.projectId())
                .orElseThrow(() -> TraceabilityExceptions.functionalItemNotFound(uc.primaryFunctionId()));

        List<UUID> screenIds = functionScreenRepo.findByFunctionId(fn.id()).stream()
                .map(FunctionScreen::screenId)
                .toList();
        Map<UUID, RegistryScreen> screensById = indexScreens(screenIds);
        Map<UUID, Long> componentCounts = countComponentsByScreen(screenIds);

        List<UseCaseFlowScopeResponse.ScreenRef> screens = screenIds.stream()
                .map(screensById::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(RegistryScreen::code, Comparator.nullsLast(String::compareToIgnoreCase)))
                .map(s -> new UseCaseFlowScopeResponse.ScreenRef(
                        s.id(), s.code(), s.name(), componentCounts.getOrDefault(s.id(), 0L)))
                .toList();

        List<UUID> apiIds = functionApiRepo.findByFunctionId(fn.id()).stream()
                .map(FunctionApi::apiEndpointId)
                .toList();
        List<UseCaseFlowScopeResponse.SimpleRef> apis = apiRepo.findByIdIn(apiIds).stream()
                .sorted(Comparator.comparing(RegistryApiEndpoint::pathPattern, Comparator.nullsLast(String::compareToIgnoreCase)))
                .map(a -> new UseCaseFlowScopeResponse.SimpleRef(a.id(), apiLabel(a)))
                .toList();

        List<UUID> entityIds = anchorRepo
                .findByFunctionalItemIdAndNodeType(fn.id(), AnchorNodeType.DATA_ENTITY.name())
                .stream()
                .map(FunctionalItemAnchor::nodeId)
                .toList();
        List<UseCaseFlowScopeResponse.SimpleRef> entities = entityRepo.findByIdIn(entityIds).stream()
                .sorted(Comparator.comparing(RegistryDataEntity::name, Comparator.nullsLast(String::compareToIgnoreCase)))
                .map(e -> new UseCaseFlowScopeResponse.SimpleRef(e.id(), e.name()))
                .toList();

        return new UseCaseFlowScopeResponse(
                query.useCaseId(),
                new UseCaseFlowScopeResponse.FunctionRef(fn.id(), fn.code(), fn.title()),
                screens,
                apis,
                entities
        );
    }

    @Transactional(readOnly = true)
    public UseCaseMentionOptionsResponse listMentionOptions(ListUseCaseMentionOptionsQuery query) {
        authorization.requireView(query.projectId());
        UseCase uc = requireUseCase(query.projectId(), query.useCaseId());
        if (uc.primaryFunctionId() == null) {
            throw TraceabilityExceptions.useCaseFunctionRequired(query.useCaseId());
        }
        UUID fnId = uc.primaryFunctionId();
        int lim = Math.min(Math.max(query.limit(), 1), 50);
        String q = blankToNull(query.query());
        boolean browse = q == null || "browse".equalsIgnoreCase(query.mode());
        Set<String> types = parseTypes(query.typesCsv());

        if (query.screenId() != null && !functionScreenRepo.existsByFunctionIdAndScreenId(fnId, query.screenId())) {
            throw TraceabilityExceptions.useCaseScreenNotLinked(query.screenId());
        }

        List<UseCaseMentionOptionsResponse.MentionOption> items = new ArrayList<>();
        if (browse) {
            if (types.contains("COMPONENT") && query.screenId() != null) {
                items.addAll(componentOptions(fnId, query.screenId(), true, null, 8));
            }
            if (types.contains("SCREEN")) {
                items.addAll(screenOptions(fnId, null, 5));
            }
            if (types.contains("API") && items.size() < lim) {
                items.addAll(apiOptions(fnId, null, Math.min(5, lim - items.size())));
            }
            if (types.contains("ENTITY") && items.size() < lim) {
                items.addAll(entityOptions(fnId, null, Math.min(5, lim - items.size())));
            }
        } else {
            if (types.contains("COMPONENT") && items.size() < lim) {
                items.addAll(componentOptions(fnId, query.screenId(), false, q, lim - items.size()));
            }
            if (types.contains("SCREEN") && items.size() < lim) {
                items.addAll(screenOptions(fnId, q, lim - items.size()));
            }
            if (types.contains("API") && items.size() < lim) {
                items.addAll(apiOptions(fnId, q, lim - items.size()));
            }
            if (types.contains("ENTITY") && items.size() < lim) {
                items.addAll(entityOptions(fnId, q, lim - items.size()));
            }
        }

        if (items.size() > lim) {
            items = new ArrayList<>(items.subList(0, lim));
        }
        return new UseCaseMentionOptionsResponse(items, lim, browse ? "browse" : "search");
    }

    @Transactional(readOnly = true)
    public PrimaryFunctionChangeImpactResponse primaryFunctionChangeImpact(GetPrimaryFunctionChangeImpactQuery query) {
        authorization.requireView(query.projectId());
        UseCase uc = requireUseCase(query.projectId(), query.useCaseId());
        if (query.newFunctionId() == null) {
            throw TraceabilityExceptions.functionalItemNotFound(null);
        }
        functionalItemRepo.findByIdAndProjectId(query.newFunctionId(), query.projectId())
                .orElseThrow(() -> TraceabilityExceptions.functionalItemNotFound(query.newFunctionId()));

        Set<UUID> allowedScreens = functionScreenRepo.findByFunctionId(query.newFunctionId()).stream()
                .map(FunctionScreen::screenId).collect(Collectors.toSet());
        Set<UUID> allowedApis = functionApiRepo.findByFunctionId(query.newFunctionId()).stream()
                .map(FunctionApi::apiEndpointId).collect(Collectors.toSet());
        Set<UUID> allowedEntities = anchorRepo
                .findByFunctionalItemIdAndNodeType(query.newFunctionId(), AnchorNodeType.DATA_ENTITY.name())
                .stream().map(FunctionalItemAnchor::nodeId).collect(Collectors.toSet());
        Set<UUID> allowedComponents = screenComponentRepo.findByScreenIdIn(allowedScreens).stream()
                .map(ScreenComponent::componentId).collect(Collectors.toSet());

        List<PrimaryFunctionChangeImpactResponse.OutOfScopeMention> out = new ArrayList<>();
        for (var flow : flowRepo.findByUseCaseIdOrderByDisplayOrder(query.useCaseId())) {
            for (UseCaseFlowStep step : stepRepo.findByFlowIdOrderByDisplayOrder(flow.id())) {
                if (step.screenContextId() != null && !allowedScreens.contains(step.screenContextId())) {
                    out.add(new PrimaryFunctionChangeImpactResponse.OutOfScopeMention(
                            "SCREEN", step.screenContextId(), "Screen Context", step.screenContextId(), step.id()));
                }
                if (step.nextScreenId() != null && !allowedScreens.contains(step.nextScreenId())) {
                    out.add(new PrimaryFunctionChangeImpactResponse.OutOfScopeMention(
                            "SCREEN", step.nextScreenId(), "Next Screen", step.nextScreenId(), step.id()));
                }
                for (MentionRef ref : extractMentions(step.contentJson())) {
                    boolean inScope = switch (ref.entityType()) {
                        case "SCREEN" -> allowedScreens.contains(ref.entityId());
                        case "COMPONENT" -> allowedComponents.contains(ref.entityId());
                        case "API" -> allowedApis.contains(ref.entityId());
                        case "ENTITY" -> allowedEntities.contains(ref.entityId());
                        default -> false;
                    };
                    if (!inScope) {
                        out.add(new PrimaryFunctionChangeImpactResponse.OutOfScopeMention(
                                ref.entityType(), ref.entityId(), ref.label(), ref.screenId(), step.id()));
                    }
                }
            }
        }

        Map<String, PrimaryFunctionChangeImpactResponse.OutOfScopeMention> dedup = new LinkedHashMap<>();
        for (var m : out) {
            dedup.putIfAbsent(m.entityType() + ":" + m.entityId(), m);
        }
        return new PrimaryFunctionChangeImpactResponse(
                query.useCaseId(), uc.primaryFunctionId(), query.newFunctionId(), List.copyOf(dedup.values()));
    }

    // ---- scoped option builders (filter in-memory within Function scope) ----

    private List<UseCaseMentionOptionsResponse.MentionOption> componentOptions(
            UUID fnId, UUID preferScreenId, boolean onlyPreferScreen, String q, int limit) {
        List<UUID> linkedScreenIds = functionScreenRepo.findByFunctionId(fnId).stream()
                .map(FunctionScreen::screenId)
                .toList();
        if (linkedScreenIds.isEmpty() || limit <= 0) return List.of();

        List<UUID> screenIds = onlyPreferScreen && preferScreenId != null
                ? List.of(preferScreenId)
                : linkedScreenIds;

        List<ScreenComponent> links = screenComponentRepo.findByScreenIdIn(screenIds);
        Map<UUID, RegistryScreen> screens = indexScreens(screenIds);
        Set<UUID> componentIds = links.stream().map(ScreenComponent::componentId).collect(Collectors.toSet());
        Map<UUID, RegistryAppComponent> components = componentRepo.findByIdIn(componentIds).stream()
                .collect(Collectors.toMap(RegistryAppComponent::id, c -> c, (a, b) -> a));

        record Row(UUID componentId, String label, UUID screenId, String screenName, int boost) {}
        List<Row> rows = new ArrayList<>();
        for (ScreenComponent link : links) {
            RegistryAppComponent c = components.get(link.componentId());
            RegistryScreen s = screens.get(link.screenId());
            if (c == null || s == null) continue;
            if (q != null && !matches(q, c.name(), c.code())) continue;
            int boost = preferScreenId != null && preferScreenId.equals(s.id()) ? 0 : 1;
            rows.add(new Row(c.id(), c.name(), s.id(), s.name(), boost));
        }
        return rows.stream()
                .sorted(Comparator.comparingInt(Row::boost).thenComparing(Row::label, String.CASE_INSENSITIVE_ORDER))
                .limit(limit)
                .map(r -> new UseCaseMentionOptionsResponse.MentionOption(
                        "COMPONENT", r.componentId(), r.label(), r.screenName(), r.screenId(), r.screenId()))
                .toList();
    }

    private List<UseCaseMentionOptionsResponse.MentionOption> screenOptions(UUID fnId, String q, int limit) {
        List<UUID> screenIds = functionScreenRepo.findByFunctionId(fnId).stream()
                .map(FunctionScreen::screenId).toList();
        return screenRepo.findByIdIn(screenIds).stream()
                .filter(s -> q == null || matches(q, s.name(), s.code()))
                .sorted(Comparator.comparing(RegistryScreen::code, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .limit(limit)
                .map(s -> new UseCaseMentionOptionsResponse.MentionOption(
                        "SCREEN", s.id(), s.name(), null, null, s.id()))
                .toList();
    }

    private List<UseCaseMentionOptionsResponse.MentionOption> apiOptions(UUID fnId, String q, int limit) {
        List<UUID> apiIds = functionApiRepo.findByFunctionId(fnId).stream()
                .map(FunctionApi::apiEndpointId).toList();
        return apiRepo.findByIdIn(apiIds).stream()
                .filter(a -> q == null || matches(q, a.name(), a.pathPattern(), a.method()))
                .sorted(Comparator.comparing(RegistryApiEndpoint::pathPattern, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .limit(limit)
                .map(a -> new UseCaseMentionOptionsResponse.MentionOption(
                        "API", a.id(), apiLabel(a), null, null, null))
                .toList();
    }

    private List<UseCaseMentionOptionsResponse.MentionOption> entityOptions(UUID fnId, String q, int limit) {
        List<UUID> entityIds = anchorRepo
                .findByFunctionalItemIdAndNodeType(fnId, AnchorNodeType.DATA_ENTITY.name())
                .stream().map(FunctionalItemAnchor::nodeId).toList();
        return entityRepo.findByIdIn(entityIds).stream()
                .filter(e -> q == null || matches(q, e.name(), e.code()))
                .sorted(Comparator.comparing(RegistryDataEntity::name, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .limit(limit)
                .map(e -> new UseCaseMentionOptionsResponse.MentionOption(
                        "ENTITY", e.id(), e.name(), null, null, null))
                .toList();
    }

    private Map<UUID, RegistryScreen> indexScreens(List<UUID> screenIds) {
        return screenRepo.findByIdIn(screenIds).stream()
                .collect(Collectors.toMap(RegistryScreen::id, s -> s, (a, b) -> a));
    }

    private Map<UUID, Long> countComponentsByScreen(List<UUID> screenIds) {
        if (screenIds.isEmpty()) return Map.of();
        return screenComponentRepo.findByScreenIdIn(screenIds).stream()
                .collect(Collectors.groupingBy(ScreenComponent::screenId, Collectors.counting()));
    }

    private UseCase requireUseCase(UUID projectId, UUID useCaseId) {
        return useCaseRepo.findByIdAndProjectId(useCaseId, projectId)
                .orElseThrow(() -> TraceabilityExceptions.useCaseNotFound(useCaseId));
    }

    private static String apiLabel(RegistryApiEndpoint a) {
        if (a.name() != null && !a.name().isBlank()) return a.name();
        return (a.method() == null ? "" : a.method()) + " " + (a.pathPattern() == null ? "" : a.pathPattern());
    }

    private static boolean matches(String q, String... fields) {
        String needle = q.toLowerCase(Locale.ROOT);
        for (String f : fields) {
            if (f != null && f.toLowerCase(Locale.ROOT).contains(needle)) return true;
        }
        return false;
    }

    private List<MentionRef> extractMentions(String contentJson) {
        if (contentJson == null || contentJson.isBlank()) return List.of();
        try {
            JsonNode root = objectMapper.readTree(contentJson);
            if (root == null || !"doc".equals(textOrNull(root.get("type")))) return List.of();
            List<MentionRef> out = new ArrayList<>();
            walkMentions(root.get("content"), out);
            return out;
        } catch (Exception ignored) {
            return List.of();
        }
    }

    private void walkMentions(JsonNode nodes, List<MentionRef> out) {
        if (nodes == null || !nodes.isArray()) return;
        for (JsonNode node : nodes) {
            if ("mention".equals(textOrNull(node.get("type")))) {
                JsonNode attrs = node.get("attrs");
                if (attrs == null) continue;
                String type = textOrNull(attrs.get("entityType"));
                String id = textOrNull(attrs.get("entityId"));
                if (type == null || id == null) continue;
                try {
                    out.add(new MentionRef(
                            type.toUpperCase(Locale.ROOT),
                            UUID.fromString(id),
                            textOrNull(attrs.get("label")),
                            parseUuidOrNull(textOrNull(attrs.get("screenId")))
                    ));
                } catch (IllegalArgumentException ignored) {
                    // skip malformed id
                }
            }
            if (node.has("content")) {
                walkMentions(node.get("content"), out);
            }
        }
    }

    private static Set<String> parseTypes(String typesCsv) {
        if (typesCsv == null || typesCsv.isBlank()) {
            return new HashSet<>(Arrays.asList("COMPONENT", "SCREEN", "API", "ENTITY"));
        }
        return Arrays.stream(typesCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> s.toUpperCase(Locale.ROOT))
                .map(s -> "API_ENDPOINT".equals(s) ? "API" : s)
                .map(s -> "DATA_ENTITY".equals(s) ? "ENTITY" : s)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private record MentionRef(String entityType, UUID entityId, String label, UUID screenId) {}

    private static String blankToNull(String q) {
        return q == null || q.isBlank() ? null : q.trim();
    }

    private static String textOrNull(JsonNode n) {
        return n == null || n.isNull() ? null : n.asText();
    }

    private static UUID parseUuidOrNull(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return UUID.fromString(s);
        } catch (Exception e) {
            return null;
        }
    }
}
