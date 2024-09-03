package io.wispforest.owowhatsthis;

import io.wispforest.endec.Endec;
import io.wispforest.endec.SerializationAttributes;
import io.wispforest.endec.SerializationContext;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.CodecUtils;
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
            CodecUtils.toMapCodec(ENDEC, SerializationContext.attributes(SerializationAttributes.HUMAN_READABLE)),
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
