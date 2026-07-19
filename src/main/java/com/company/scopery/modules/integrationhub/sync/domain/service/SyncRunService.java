package com.company.scopery.modules.integrationhub.sync.domain.service;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncCursor;
public final class SyncRunService {
    private SyncRunService(){}
    /** Cursor updates only after successful processing. */
    public static SyncCursor advanceCursorOnSuccess(SyncCursor cursor, String value, boolean success) {
        if (!success) return cursor;
        return cursor.advance(value);
    }
}
