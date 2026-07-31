CREATE TABLE quality_nfr_specification (
    requirement_id          UUID          PRIMARY KEY REFERENCES requirements_requirement(id),
    quality_attribute       VARCHAR(50)   NOT NULL,
    metric_name             VARCHAR(100),
    comparison_operator     VARCHAR(20),
    target_value            DECIMAL(20,4),
    secondary_target_value  DECIMAL(20,4),
    unit                    VARCHAR(50),
    measurement_window      VARCHAR(100),
    environment             VARCHAR(100),
    verification_frequency  VARCHAR(50),
    configuration_json      TEXT,
    created_at              TIMESTAMPTZ   NOT NULL,
    updated_at              TIMESTAMPTZ   NOT NULL
);
