package com.company.scopery.modules.knowledge.shared.listeners;

import com.company.scopery.modules.iam.right.domain.model.IamRight;
import com.company.scopery.modules.iam.right.domain.model.IamRightRepository;
import com.company.scopery.modules.iam.right.domain.valueobject.IamRightCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Order(105)
public class KnowledgePermissionInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(KnowledgePermissionInitializer.class);
    private static final String MODULE = "KNOWLEDGE";

    private final IamRightRepository rightRepository;

    public KnowledgePermissionInitializer(IamRightRepository rightRepository) {
        this.rightRepository = rightRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        int seeded = 0;
        for (RightDef def : RIGHTS) {
            IamRightCode code = IamRightCode.of(def.code());
            if (!rightRepository.existsByCode(code)) {
                rightRepository.save(IamRight.create(code, def.name(), def.description(), MODULE));
                seeded++;
            }
        }
        if (seeded > 0) {
            log.info("[KnowledgePermissionSeed] Seeded {} new knowledge IAM rights", seeded);
        }
    }

    private record RightDef(String code, String name, String description) {}

    private static final List<RightDef> RIGHTS = List.of(
            new RightDef("KNOWLEDGE_SEARCH",
                    "Knowledge Search",
                    "Run hybrid BM25+KNN semantic search over workspace knowledge"),
            new RightDef("KNOWLEDGE_SOURCE_VIEW",
                    "Knowledge Source View",
                    "View knowledge source metadata and index status"),
            new RightDef("KNOWLEDGE_CHUNK_VIEW_CONTENT",
                    "Knowledge Chunk View Content",
                    "View raw chunk text content (admin/debug)"),
            new RightDef("KNOWLEDGE_REINDEX_SOURCE",
                    "Knowledge Reindex Source",
                    "Trigger re-indexing for a single knowledge source"),
            new RightDef("KNOWLEDGE_REINDEX_PROJECT",
                    "Knowledge Reindex Project",
                    "Trigger re-indexing for all sources in a project"),
            new RightDef("KNOWLEDGE_INDEX_STATUS_VIEW",
                    "Knowledge Index Status View",
                    "View knowledge index health and job status"),
            new RightDef("KNOWLEDGE_RETRIEVAL_DEBUG",
                    "Knowledge Retrieval Debug",
                    "View BM25/KNN rank scores and retrieval debug trace"),
            new RightDef("KNOWLEDGE_GRAPH_VIEW",
                    "Knowledge Graph View",
                    "View permission-filtered knowledge graph nodes and edges")
    );
}
