package io.wispforest.owowhatsthis;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class NumberFormatter {

    private static final Int2ObjectMap<String> METRIC_PREFIXES = new Int2ObjectOpenHashMap<>(
            Map.of(
                    -6, "u",
                    -3, "m",
                    0, "",
                    3, "k",
                    6, "M",
                    9, "G",
                    12, "T"
            )
    );

    public static String time(long time) {
        var formatted = DurationFormatUtils.formatDuration(time * 1000, "H':'m':'ss");
        while (formatted.startsWith("0:")) formatted = formatted.substring(2);
        return formatted;
    }

    public static String quantity(double quantity, String unit) {
        if (Double.isInfinite(quantity)) return "∞ " + unit;
        if (Double.isNaN(quantity)) return "NaN " + unit;

        int order = 0;

        if (quantity != 0) {
            while (Math.abs(quantity) >= 1000d) {
                quantity /= 1000d;
                order += 3;
            }

            while (Math.abs(quantity) < 1d) {
                quantity *= 1000;
                order -= 3;
            }
        }

        return new BigDecimal(quantity).setScale(OwoWhatsThis.CONFIG.decimalPlaces(), RoundingMode.HALF_UP).toPlainString()
                + METRIC_PREFIXES.getOrDefault(order, "")
                + unit;
    }

}
