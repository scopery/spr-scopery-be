-- Many-to-many: Requirement (SATISFIED_BY) Function
-- Note: requirement_id has no DB FK because requirements_requirement is in a separate module
CREATE TABLE app_requirement_function (
    requirement_id UUID         NOT NULL,
    function_id    UUID         NOT NULL REFERENCES app_functional_item(id) ON DELETE CASCADE,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by     VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    CONSTRAINT pk_app_req_fn PRIMARY KEY (requirement_id, function_id)
);
CREATE INDEX idx_app_req_fn_req ON app_requirement_function(requirement_id);
CREATE INDEX idx_app_req_fn_fn  ON app_requirement_function(function_id);
