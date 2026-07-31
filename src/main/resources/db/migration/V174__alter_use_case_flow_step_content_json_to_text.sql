ALTER TABLE app_use_case_flow_step
    ALTER COLUMN content_json TYPE TEXT USING content_json::TEXT;
