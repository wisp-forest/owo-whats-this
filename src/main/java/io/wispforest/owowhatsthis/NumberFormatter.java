package io.wispforest.owowhatsthis;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.text.MutableText;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberFormatter {

    private static final Int2ObjectMap<String> METRIC_PREFIXES = new Int2ObjectOpenHashMap<>(
            ImmutableMap.<Integer, String>builder()
                    .put(-30, "q").put(-27, "r")
                    .put(-24, "y").put(-21, "z")
                    .put(-18, "a").put(-15, "f")
                    .put(-12, "p").put(-9, "n")
                    .put(-6, "μ").put(-3, "m")
                    .put(0, "")
                    .put(3, "k").put(6, "M")
                    .put(9, "G").put(12, "T")
                    .put(15, "P").put(18, "E")
                    .put(21, "Z").put(24, "Y")
                    .put(27, "R").put(30, "Q")
                    .build()
    );

    public static String time(long time) {
        var formatted = DurationFormatUtils.formatDuration(time * 1000, "H':'m':'ss");
        while (formatted.startsWith("0:")) formatted = formatted.substring(2);
        return formatted;
    }

    public static MutableText quantityText(double quantity, String unit) {
        return MutableText.of(new QuantityTextContent(quantity, unit));
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
