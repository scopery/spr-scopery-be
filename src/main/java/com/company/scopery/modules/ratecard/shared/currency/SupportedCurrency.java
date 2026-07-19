package com.company.scopery.modules.ratecard.shared.currency;

import com.company.scopery.modules.ratecard.shared.error.RateCardExceptions;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public enum SupportedCurrency {
    VND, USD, EUR, SGD, JPY, KRW, CNY;

    private static final Set<String> CODES = Arrays.stream(values())
            .map(Enum::name)
            .collect(Collectors.toUnmodifiableSet());

    public static String requireValid(String currencyCode) {
        if (currencyCode == null || currencyCode.isBlank()) {
            throw RateCardExceptions.invalidCurrency(currencyCode);
        }
        String normalized = currencyCode.trim().toUpperCase(Locale.ROOT);
        if (!CODES.contains(normalized)) {
            throw RateCardExceptions.invalidCurrency(currencyCode);
        }
        return normalized;
    }

    public static boolean isSupported(String currencyCode) {
        if (currencyCode == null || currencyCode.isBlank()) {
            return false;
        }
        return CODES.contains(currencyCode.trim().toUpperCase(Locale.ROOT));
    }
}
