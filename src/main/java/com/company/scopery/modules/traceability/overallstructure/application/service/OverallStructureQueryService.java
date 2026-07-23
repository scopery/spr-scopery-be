package com.company.scopery.modules.traceability.overallstructure.application.service;

import com.company.scopery.modules.traceability.apiendpoint.domain.model.RegistryApiEndpoint;
import com.company.scopery.modules.traceability.apiendpoint.domain.model.RegistryApiEndpointRepository;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponent;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponentRepository;
import com.company.scopery.modules.traceability.appmodule.domain.model.RegistryAppModule;
import com.company.scopery.modules.traceability.appmodule.domain.model.RegistryAppModuleRepository;
import com.company.scopery.modules.traceability.functionapi.domain.model.FunctionApi;
import com.company.scopery.modules.traceability.functionapi.domain.model.FunctionApiRepository;
import com.company.scopery.modules.traceability.functionscreen.domain.model.FunctionScreen;
import com.company.scopery.modules.traceability.functionscreen.domain.model.FunctionScreenRepository;
import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItem;
import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItemRepository;
import com.company.scopery.modules.traceability.overallstructure.application.response.ApiRef;
import com.company.scopery.modules.traceability.overallstructure.application.response.CandidatesResponse;
import com.company.scopery.modules.traceability.overallstructure.application.response.ComponentRef;
import com.company.scopery.modules.traceability.overallstructure.application.response.EntityRef;
import com.company.scopery.modules.traceability.overallstructure.application.response.FunctionStructure;
import com.company.scopery.modules.traceability.overallstructure.application.response.ModuleStructure;
import com.company.scopery.modules.traceability.overallstructure.application.response.OverallStructureResponse;
import com.company.scopery.modules.traceability.overallstructure.application.response.ScreenRef;
import com.company.scopery.modules.traceability.dataentity.domain.model.RegistryDataEntity;
import com.company.scopery.modules.traceability.dataentity.domain.model.RegistryDataEntityRepository;
import com.company.scopery.modules.traceability.screen.domain.model.RegistryScreen;
import com.company.scopery.modules.traceability.screen.domain.model.RegistryScreenRepository;
import com.company.scopery.modules.traceability.screencomponent.domain.model.ScreenComponent;
import com.company.scopery.modules.traceability.screencomponent.domain.model.ScreenComponentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OverallStructureQueryService {

    private final RegistryAppModuleRepository moduleRepo;
    private final FunctionalItemRepository functionalItemRepo;
    private final FunctionScreenRepository functionScreenRepo;
    private final FunctionApiRepository functionApiRepo;
    private final ScreenComponentRepository screenComponentRepo;
    private final RegistryScreenRepository screenRepo;
    private final RegistryApiEndpointRepository apiEndpointRepo;
    private final RegistryAppComponentRepository componentRepo;
    private final RegistryDataEntityRepository dataEntityRepo;

    public OverallStructureQueryService(RegistryAppModuleRepository moduleRepo,
                                        FunctionalItemRepository functionalItemRepo,
                                        FunctionScreenRepository functionScreenRepo,
                                        FunctionApiRepository functionApiRepo,
                                        ScreenComponentRepository screenComponentRepo,
                                        RegistryScreenRepository screenRepo,
                                        RegistryApiEndpointRepository apiEndpointRepo,
                                        RegistryAppComponentRepository componentRepo,
                                        RegistryDataEntityRepository dataEntityRepo) {
        this.moduleRepo = moduleRepo;
        this.functionalItemRepo = functionalItemRepo;
        this.functionScreenRepo = functionScreenRepo;
        this.functionApiRepo = functionApiRepo;
        this.screenComponentRepo = screenComponentRepo;
        this.screenRepo = screenRepo;
        this.apiEndpointRepo = apiEndpointRepo;
        this.componentRepo = componentRepo;
        this.dataEntityRepo = dataEntityRepo;
    }

    @Transactional(readOnly = true)
    public OverallStructureResponse getStructure(UUID applicationId) {
        List<RegistryAppModule> modules = moduleRepo.findByApplicationId(applicationId);
        if (modules.isEmpty()) {
            return new OverallStructureResponse(applicationId, List.of());
        }

        Set<UUID> moduleIds = modules.stream().map(RegistryAppModule::id).collect(Collectors.toSet());

        // Bulk load all functional items for these modules
        List<FunctionalItem> allFunctions = functionalItemRepo.findByModuleIdIn(moduleIds);
        Map<UUID, List<FunctionalItem>> functionsByModule = allFunctions.stream()
                .collect(Collectors.groupingBy(FunctionalItem::moduleId));

        Set<UUID> functionIds = allFunctions.stream().map(FunctionalItem::id).collect(Collectors.toSet());

        // Bulk load function-screen and function-api links
        List<FunctionScreen> allFunctionScreens = functionIds.isEmpty()
                ? List.of() : functionScreenRepo.findByFunctionIdIn(functionIds);
        List<FunctionApi> allFunctionApis = functionIds.isEmpty()
                ? List.of() : functionApiRepo.findByFunctionIdIn(functionIds);

        Map<UUID, List<FunctionScreen>> screenLinksByFunction = allFunctionScreens.stream()
                .collect(Collectors.groupingBy(FunctionScreen::functionId));
        Map<UUID, List<FunctionApi>> apiLinksByFunction = allFunctionApis.stream()
                .collect(Collectors.groupingBy(FunctionApi::functionId));

        // Collect all linked screen IDs, then bulk load screens
        Set<UUID> linkedScreenIds = allFunctionScreens.stream()
                .map(FunctionScreen::screenId).collect(Collectors.toSet());
        Set<UUID> linkedApiIds = allFunctionApis.stream()
                .map(FunctionApi::apiEndpointId).collect(Collectors.toSet());

        Map<UUID, RegistryScreen> screenById = screenRepo.findByApplicationId(applicationId).stream()
                .collect(Collectors.toMap(RegistryScreen::id, s -> s));
        Map<UUID, RegistryApiEndpoint> apiById = apiEndpointRepo.findByApplicationId(applicationId).stream()
                .collect(Collectors.toMap(RegistryApiEndpoint::id, a -> a));

        // Bulk load screen-component links for the linked screens
        List<ScreenComponent> allScreenComponents = linkedScreenIds.isEmpty()
                ? List.of() : screenComponentRepo.findByScreenIdIn(linkedScreenIds);
        Map<UUID, List<ScreenComponent>> componentLinksByScreen = allScreenComponents.stream()
                .collect(Collectors.groupingBy(ScreenComponent::screenId));

        Set<UUID> linkedComponentIds = allScreenComponents.stream()
                .map(ScreenComponent::componentId).collect(Collectors.toSet());
        Map<UUID, RegistryAppComponent> componentById = componentRepo.findByApplicationId(applicationId).stream()
                .filter(c -> linkedComponentIds.contains(c.id()))
                .collect(Collectors.toMap(RegistryAppComponent::id, c -> c));

        List<ModuleStructure> moduleStructures = modules.stream().map(module -> {
            List<FunctionalItem> moduleFunctions = functionsByModule.getOrDefault(module.id(), List.of());

            List<FunctionStructure> functionStructures = moduleFunctions.stream().map(fn -> {
                List<ScreenRef> screenRefs = screenLinksByFunction.getOrDefault(fn.id(), List.of()).stream()
                        .filter(link -> screenById.containsKey(link.screenId()))
                        .map(link -> {
                            RegistryScreen screen = screenById.get(link.screenId());
                            List<ScreenComponent> compLinks = componentLinksByScreen.getOrDefault(screen.id(), List.of());
                            List<ComponentRef> componentRefs = compLinks.stream()
                                    .filter(cl -> componentById.containsKey(cl.componentId()))
                                    .map(cl -> ComponentRef.from(componentById.get(cl.componentId()), cl.displayOrder()))
                                    .sorted(java.util.Comparator.comparingInt(ComponentRef::displayOrder))
                                    .toList();
                            return ScreenRef.from(screen, componentRefs);
                        })
                        .toList();

                List<ApiRef> apiRefs = apiLinksByFunction.getOrDefault(fn.id(), List.of()).stream()
                        .filter(link -> apiById.containsKey(link.apiEndpointId()))
                        .map(link -> ApiRef.from(apiById.get(link.apiEndpointId())))
                        .toList();

                return FunctionStructure.from(fn, screenRefs, apiRefs);
            }).toList();

            List<EntityRef> entityRefs = dataEntityRepo.findByApplicationIdAndModuleId(applicationId, module.id())
                    .stream().map(EntityRef::from).toList();

            return ModuleStructure.from(module, functionStructures, entityRefs);
        }).toList();

        return new OverallStructureResponse(applicationId, moduleStructures);
    }

    @Transactional(readOnly = true)
    public CandidatesResponse getCandidates(UUID applicationId) {
        List<CandidatesResponse.ScreenCandidate> screens = screenRepo.findByApplicationId(applicationId)
                .stream().map(CandidatesResponse.ScreenCandidate::from).toList();

        List<CandidatesResponse.ApiCandidate> apis = apiEndpointRepo.findByApplicationId(applicationId)
                .stream().map(CandidatesResponse.ApiCandidate::from).toList();

        List<CandidatesResponse.ComponentCandidate> components = componentRepo.findByApplicationId(applicationId)
                .stream().map(CandidatesResponse.ComponentCandidate::from).toList();

        return new CandidatesResponse(applicationId, screens, apis, components);
    }
}
