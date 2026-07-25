package com.company.scopery.platform.bulkjob;

import com.company.scopery.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/bulk-jobs")
@Tag(name = "Platform - Bulk Jobs")
public class BulkJobController {

    private final BulkJobService bulkJobService;

    public BulkJobController(BulkJobService bulkJobService) {
        this.bulkJobService = bulkJobService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get bulk job status")
    public ApiResponse<BulkJobResponse> get(@PathVariable UUID id) {
        return bulkJobService.get(id)
                .map(job -> ApiResponse.success(BulkJobResponse.from(job)))
                .orElseThrow(() -> new com.company.scopery.common.exception.NotFoundException("Bulk job not found: " + id));
    }
}
