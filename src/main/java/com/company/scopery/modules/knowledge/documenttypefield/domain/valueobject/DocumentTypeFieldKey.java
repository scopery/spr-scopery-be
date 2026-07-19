package com.company.scopery.modules.knowledge.documenttypefield.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Field key normalized to camelCase or snake_case (lowercase start, alphanumerics + underscore).
 */
public final class DocumentTypeFieldKey {

    private static final Pattern VALID = Pattern.compile("^[a-z][a-zA-Z0-9_]{0,99}$");

    private final String value;

    private DocumentTypeFieldKey(String value) {
        this.value = value;
    }

    public static DocumentTypeFieldKey of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Field key is required");
        }
        String normalized = normalize(raw.trim());
        if (!VALID.matcher(normalized).matches()) {
            throw new IllegalArgumentException(
                    "Field key must be camelCase or snake_case starting with a lowercase letter: " + raw);
        }
        return new DocumentTypeFieldKey(normalized);
    }

    private static String normalize(String raw) {
        if (raw.contains("-") || raw.contains(" ")) {
            String[] parts = raw.toLowerCase().split("[-\\s]+");
            StringBuilder sb = new StringBuilder(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                if (parts[i].isEmpty()) {
                    continue;
                }
                sb.append(Character.toUpperCase(parts[i].charAt(0)));
                if (parts[i].length() > 1) {
                    sb.append(parts[i].substring(1));
                }
            }
            return sb.toString();
        }
        if (Character.isUpperCase(raw.charAt(0))) {
            return Character.toLowerCase(raw.charAt(0)) + raw.substring(1);
        }
        return raw;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentTypeFieldKey that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
