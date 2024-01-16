package io.wispforest.owowhatsthis;

import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.SerializationAttribute;
import io.wispforest.owo.serialization.StructEndec;
import io.wispforest.owo.serialization.endec.StructEndecBuilder;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.TextContent;

import java.util.Optional;

public record QuantityTextContent(double quantity, String unit) implements TextContent {

    public static final StructEndec<QuantityTextContent> ENDEC = StructEndecBuilder.of(
            Endec.DOUBLE.fieldOf("quantity", QuantityTextContent::quantity),
            Endec.STRING.fieldOf("unit", QuantityTextContent::unit),
            QuantityTextContent::new
    );

    public static final Type<QuantityTextContent> TYPE = new Type<>(
            ENDEC.mapCodec(SerializationAttribute.HUMAN_READABLE),
            OwoWhatsThis.id("quantity").toString()
    );

    @Override
    public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> visitor, Style style) {
        return visitor.accept(style, NumberFormatter.quantity(this.quantity, this.unit));
    }

    @Override
    public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
        return visitor.accept(NumberFormatter.quantity(this.quantity, this.unit));
    }

    @Override
    public Type<?> getType() {
        return TYPE;
    }

}
