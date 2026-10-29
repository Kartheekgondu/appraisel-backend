package com.uxinterfacely.appraisel.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public final class MoneyUtils {

    private MoneyUtils() {
    }

    public static BigDecimal round(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException(
                    "Amount cannot be null");
        }

        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static String formatINR(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException(
                    "Amount cannot be null");
        }

        NumberFormat formatter =
                NumberFormat.getCurrencyInstance(
                        Locale.forLanguageTag("en-IN"));

        return formatter.format(amount);
    }
}