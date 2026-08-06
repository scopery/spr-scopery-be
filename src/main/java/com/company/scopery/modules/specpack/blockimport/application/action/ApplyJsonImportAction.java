package com.company.scopery.modules.specpack.blockimport.application.action;

import com.company.scopery.modules.specpack.block.domain.enums.ChangeSource;
import com.company.scopery.modules.specpack.blockimport.application.response.ImportPreviewResponse;
import com.company.scopery.modules.specpack.blockimport.application.service.SpecPackBlockJsonParser;
import com.company.scopery.modules.specpack.blockimport.application.service.SpecPackBlockSchemaValidator;
import com.company.scopery.modules.specpack.blockimport.application.service.SpecPackImportPreviewBuilder;
import com.company.scopery.modules.specpack.blockimport.application.service.SpecPackMergeResolver;
import com.company.scopery.modules.specpack.blockimport.domain.enums.BlockMergeMode;
import com.company.scopery.modules.specpack.blockimport.domain.model.BlockImportItem;
import com.company.scopery.modules.specpack.blockimport.domain.model.ImportPreview;
import com.company.scopery.modules.specpack.shared.activity.SpecPackActivityLogger;
import com.company.scopery.modules.specpack.shared.constant.SpecPackActivityActions;
import com.company.scopery.modules.specpack.shared.constant.SpecPackEntityTypes;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
public class ApplyJsonImportAction {

    private final SpecPackBlockJsonParser parser;
    private final SpecPackBlockSchemaValidator validator;
    private final SpecPackImportPreviewBuilder previewBuilder;
    private final SpecPackMergeResolver mergeResolver;
    private final SpecPackActivityLogger activityLogger;

    public ApplyJsonImportAction(SpecPackBlockJsonParser parser,
                                 SpecPackBlockSchemaValidator validator,
                                 SpecPackImportPreviewBuilder previewBuilder,
                                 SpecPackMergeResolver mergeResolver,
                                 SpecPackActivityLogger activityLogger) {
        this.parser = parser;
        this.validator = validator;
        this.previewBuilder = previewBuilder;
        this.mergeResolver = mergeResolver;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ImportPreviewResponse execute(byte[] payload, UUID specPackId, BlockMergeMode mergeMode) {
        SpecPackBlockJsonParser.ParseResult parsed = parser.parse(payload);
        List<BlockImportItem> validated = validator.validate(parsed.items());
        ImportPreview preview = previewBuilder.build(parsed.schemaVersion(), validated, specPackId, mergeMode);

        int applied = mergeResolver.apply(preview.items(), specPackId, ChangeSource.JSON_IMPORT);

        activityLogger.logSuccess(SpecPackEntityTypes.SPEC_PACK, specPackId,
                SpecPackActivityActions.BLOCK_JSON_IMPORTED,
                "Applied JSON import to pack " + specPackId + ": " + applied + " blocks processed");

        return ImportPreviewResponse.from(preview);
    }
}
