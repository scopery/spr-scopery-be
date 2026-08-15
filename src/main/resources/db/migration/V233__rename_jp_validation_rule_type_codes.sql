UPDATE app_registry_validation_rule_type
SET code = 'POSTAL_CODE', name = 'Postal Code'
WHERE code = 'POSTAL_CODE_JP' AND workspace_id IS NULL AND is_system = TRUE;

UPDATE app_registry_validation_rule_type
SET code = 'PHONE_NUMBER', name = 'Phone Number'
WHERE code = 'PHONE_NUMBER_JP' AND workspace_id IS NULL AND is_system = TRUE;
