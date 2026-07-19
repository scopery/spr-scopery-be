package com.company.scopery.modules.trust.shared.domain;

import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SensitiveFieldMasker {
    private static final Pattern EMAIL = Pattern.compile("[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}");
    private static final Pattern PHONE_LIKE = Pattern.compile("(?<!\\d)(\\+?\\d[\\d\\s().-]{7,}\\d)(?!\\d)");

    private SensitiveFieldMasker() {}

    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "REDACTED";
        int at = email.indexOf('@');
        String local = email.substring(0, at);
        String domain = email.substring(at);
        if (local.isEmpty()) return "***" + domain;
        return local.charAt(0) + "***" + domain;
    }

    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) return "***";
        return "***" + phone.substring(phone.length() - 4);
    }

    public static String hideFinancial(String raw) {
        return "HIDDEN";
    }

    public static String redact() {
        return "REDACTED";
    }

    /** Mask emails/phones embedded in search titles/snippets (Phase 38 read-path masking). */
    public static String maskSearchText(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }
        String masked = replaceAll(EMAIL, text, SensitiveFieldMasker::maskEmail);
        return replaceAll(PHONE_LIKE, masked, SensitiveFieldMasker::maskPhone);
    }

    public static boolean changed(String original, String masked) {
        return !Objects.equals(original, masked);
    }

    private static String replaceAll(Pattern pattern, String input, Function<String, String> replacer) {
        Matcher matcher = pattern.matcher(input);
        StringBuilder out = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(out, Matcher.quoteReplacement(replacer.apply(matcher.group())));
        }
        matcher.appendTail(out);
        return out.toString();
    }
}
