package io.wispforest.owowhatsthis.client;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.ui.ConfigScreen;
import io.wispforest.owo.config.ui.OptionComponentFactory;
import io.wispforest.owo.config.ui.component.OptionComponent;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.VerticalFlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import io.wispforest.owowhatsthis.OwoWhatsThis;
import io.wispforest.owowhatsthis.client.component.ProviderConfigButton;
import io.wispforest.owowhatsthis.information.InformationProvider;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class OwoWhatsThisConfigScreen extends ConfigScreen {

    public OwoWhatsThisConfigScreen(@Nullable Screen parent) {
        super(DEFAULT_MODEL_ID, OwoWhatsThis.CONFIG, parent);

        this.extraFactories.put(option -> option.backingField().field().getName().equals("disabledProviders"), PROVIDER_CONFIG_FACTORY);
    }

    private static final OptionComponentFactory<Set<Identifier>> PROVIDER_CONFIG_FACTORY = (model, option) -> {
        var container = new ProviderConfigContainer(option);
        return new OptionComponentFactory.Result(container, container);
    };

    private static class ProviderConfigContainer extends VerticalFlowLayout implements OptionComponent {

        protected final Set<Identifier> backingSet;

        @SuppressWarnings("UnstableApiUsage")
        protected ProviderConfigContainer(Option<Set<Identifier>> option) {
            super(Sizing.fill(100), Sizing.content());
            this.backingSet = new HashSet<>(option.value());

            for (var targetType : OwoWhatsThis.TARGET_TYPES) {
                var layout = Containers.collapsible(
                        Sizing.content(), Sizing.content(),
                        Text.translatable("targetType." + OwoWhatsThis.TARGET_TYPES.getId(targetType).toTranslationKey()),
                        true
                );

                var applicableProviders = new ArrayList<InformationProvider<?, ?>>();
                for (var provider : OwoWhatsThis.INFORMATION_PROVIDERS) {
                    if (provider.applicableTargetType() == targetType) applicableProviders.add(provider);
                }

                var optionGrid = Containers.grid(Sizing.fill(100), Sizing.content(), MathHelper.ceilDiv(applicableProviders.size(), 2), 2);
                layout.child(optionGrid);

                for (int i = 0; i < applicableProviders.size(); i++) {
                    final var providerId = OwoWhatsThis.INFORMATION_PROVIDERS.getId(applicableProviders.get(i));

                    optionGrid.child(
                            Containers.horizontalFlow(Sizing.fill(50), Sizing.fixed(30)).<FlowLayout>configure(optionLayout -> {
                                optionLayout.padding(Insets.of(5)).verticalAlignment(VerticalAlignment.CENTER);

                                optionLayout.child(Components.label(Text.translatable("informationProvider." + providerId.toTranslationKey())))
                                        .child(new ProviderConfigButton()
                                                .onChanged(enabled -> {
                                                    if (enabled) this.backingSet.remove(providerId);
                                                    else this.backingSet.add(providerId);
                                                })
                                                .enabled(!this.backingSet.contains(providerId))
                                                .renderer(ButtonComponent.Renderer.flat(0, 0x77000000, 0))
                                                .positioning(Positioning.relative(100, 50))
                                                .margins(Insets.right(5)).sizing(Sizing.fixed(25), Sizing.fixed(15))
                                        );
                            }), i / 2, i % 2
                    );
                }

                this.child(layout);
            }
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public Object parsedValue() {
            return this.backingSet;
        }
    }
}
