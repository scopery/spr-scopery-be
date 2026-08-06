package com.company.scopery.modules.specpack.blockimport.application.action;

import com.company.scopery.modules.specpack.blockimport.application.response.ImportPreviewResponse;
import com.company.scopery.modules.specpack.blockimport.application.service.SpecPackBlockJsonParser;
import com.company.scopery.modules.specpack.blockimport.application.service.SpecPackBlockSchemaValidator;
import com.company.scopery.modules.specpack.blockimport.domain.model.BlockImportItem;
import com.company.scopery.modules.specpack.blockimport.domain.model.ImportPreview;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ValidateJsonImportAction {

    private final SpecPackBlockJsonParser parser;
    private final SpecPackBlockSchemaValidator validator;

    public ValidateJsonImportAction(SpecPackBlockJsonParser parser,
                                    SpecPackBlockSchemaValidator validator) {
        this.parser = parser;
        this.validator = validator;
    }

    public ImportPreviewResponse execute(byte[] payload) {
        SpecPackBlockJsonParser.ParseResult parsed = parser.parse(payload);
        List<BlockImportItem> validated = validator.validate(parsed.items());

        long valid = validated.stream().filter(BlockImportItem::isValid).count();
        long invalid = validated.size() - valid;

        ImportPreview preview = new ImportPreview(parsed.schemaVersion(),
                validated.size(), (int) valid, (int) invalid,
                0, 0, 0, validated);

        return ImportPreviewResponse.from(preview);
    }
}
