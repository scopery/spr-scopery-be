package com.company.scopery.common.response;

import java.util.List;

public record ErrorResponse(
        boolean success,
        int status,
        String error,
        String errorCode,
        String message,
        List<String> details,
        String path,
        String traceId,
        String timestamp
) {
}