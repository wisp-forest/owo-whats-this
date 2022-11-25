package io.wispforest.owowhatsthis;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import io.wispforest.owo.text.CustomTextContent;
import io.wispforest.owo.text.CustomTextContentSerializer;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;

import java.util.Optional;

public record QuantityTextContent(double quantity, String unit) implements CustomTextContent {

    @Override
    public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> visitor, Style style) {
        return visitor.accept(style, NumberFormatter.quantity(this.quantity, this.unit));
    }

    @Override
    public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
        return visitor.accept(NumberFormatter.quantity(this.quantity, this.unit));
    }

    @Override
    public CustomTextContentSerializer<?> serializer() {
        return Serializer.INSTANCE;
    }

    public enum Serializer implements CustomTextContentSerializer<QuantityTextContent> {
        INSTANCE;

        @Override
        public QuantityTextContent deserialize(JsonObject obj, JsonDeserializationContext ctx) {
            return new QuantityTextContent(
                    obj.get("quantity").getAsDouble(),
                    obj.get("unit").getAsString()
            );
        }

        @Override
        public void serialize(QuantityTextContent content, JsonObject obj, JsonSerializationContext ctx) {
            obj.addProperty("quantity", content.quantity);
            obj.addProperty("unit", content.unit);
        }
    }
}
