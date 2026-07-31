CREATE TABLE app_use_case_supporting_function (
    use_case_id   UUID        NOT NULL REFERENCES app_use_case(id) ON DELETE CASCADE,
    function_id   UUID        NOT NULL REFERENCES app_functional_item(id) ON DELETE CASCADE,
    relation_type VARCHAR(30) NOT NULL DEFAULT 'USES',
    display_order INTEGER     NOT NULL DEFAULT 0,
    CONSTRAINT pk_app_uc_sup_fn PRIMARY KEY (use_case_id, function_id),
    CONSTRAINT ck_app_uc_sup_fn_type CHECK (relation_type IN ('USES'))
);
CREATE INDEX idx_app_uc_sup_fn_uc ON app_use_case_supporting_function(use_case_id);
CREATE INDEX idx_app_uc_sup_fn_fn ON app_use_case_supporting_function(function_id);
