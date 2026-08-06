package com.company.scopery.modules.specpack.blockimport.application.action;

import com.company.scopery.modules.specpack.blockimport.application.response.ImportPreviewResponse;
import com.company.scopery.modules.specpack.blockimport.application.service.SpecPackBlockJsonParser;
import com.company.scopery.modules.specpack.blockimport.application.service.SpecPackBlockSchemaValidator;
import com.company.scopery.modules.specpack.blockimport.application.service.SpecPackImportPreviewBuilder;
import com.company.scopery.modules.specpack.blockimport.domain.enums.BlockMergeMode;
import com.company.scopery.modules.specpack.blockimport.domain.model.BlockImportItem;
import com.company.scopery.modules.specpack.blockimport.domain.model.ImportPreview;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PreviewJsonImportAction {

    private final SpecPackBlockJsonParser parser;
    private final SpecPackBlockSchemaValidator validator;
    private final SpecPackImportPreviewBuilder previewBuilder;

    public PreviewJsonImportAction(SpecPackBlockJsonParser parser,
                                   SpecPackBlockSchemaValidator validator,
                                   SpecPackImportPreviewBuilder previewBuilder) {
        this.parser = parser;
        this.validator = validator;
        this.previewBuilder = previewBuilder;
    }

    public ImportPreviewResponse execute(byte[] payload, UUID specPackId, BlockMergeMode mergeMode) {
        SpecPackBlockJsonParser.ParseResult parsed = parser.parse(payload);
        List<BlockImportItem> validated = validator.validate(parsed.items());
        ImportPreview preview = previewBuilder.build(parsed.schemaVersion(), validated, specPackId, mergeMode);
        return ImportPreviewResponse.from(preview);
    }
}
