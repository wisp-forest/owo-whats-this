package io.wispforest.owowhatsthis.client;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.ui.ConfigScreen;
import io.wispforest.owo.config.ui.OptionComponentFactory;
import io.wispforest.owo.config.ui.component.ConfigToggleButton;
import io.wispforest.owo.config.ui.component.OptionComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.VerticalFlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import io.wispforest.owowhatsthis.OwoWhatsThis;
import io.wispforest.owowhatsthis.TooltipObjectManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

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

            for (var targetType : TooltipObjectManager.sortedTargetTypes()) {
                var layout = Containers.collapsible(
                        Sizing.content(), Sizing.content(),
                        Text.translatable("targetType." + OwoWhatsThis.TARGET_TYPES.getId(targetType).toTranslationKey()),
                        true
                );

                for (var provider : OwoWhatsThis.INFORMATION_PROVIDERS) {
                    if (provider.applicableTargetType() != targetType) continue;
                    final var providerId = OwoWhatsThis.INFORMATION_PROVIDERS.getId(provider);

                    layout.child(
                            Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(32)).<FlowLayout>configure(optionLayout -> {
                                optionLayout.padding(Insets.of(5)).verticalAlignment(VerticalAlignment.CENTER);

                                optionLayout.child(Components.label(Text.translatable("informationProvider." + providerId.toTranslationKey())))
                                        .child(new ConfigToggleButton()
                                                .enabled(!this.backingSet.contains(providerId))
                                                .onPress(buttonComponent -> {
                                                    if (this.backingSet.contains(providerId)) this.backingSet.remove(providerId);
                                                    else this.backingSet.add(providerId);
                                                })
                                                .positioning(Positioning.relative(100, 50))
                                                .margins(Insets.right(5)).horizontalSizing(Sizing.fixed(120))
                                        );
                            })

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
