package io.wispforest.owowhatsthis.client;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.ui.ConfigScreen;
import io.wispforest.owo.config.ui.OptionComponentFactory;
import io.wispforest.owo.config.ui.component.OptionComponent;
import io.wispforest.owo.config.ui.component.SearchAnchorComponent;
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
import io.wispforest.owowhatsthis.OwoWhatsThisConfigModel.ProviderState;
import io.wispforest.owowhatsthis.client.component.ProviderConfigButton;
import io.wispforest.owowhatsthis.information.InformationProvider;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OwoWhatsThisConfigScreen extends ConfigScreen {

    public OwoWhatsThisConfigScreen(@Nullable Screen parent) {
        super(DEFAULT_MODEL_ID, OwoWhatsThis.CONFIG, parent);

        this.extraFactories.put(option -> option.backingField().field().getName().equals("disabledProviders"), PROVIDER_CONFIG_FACTORY);
    }

    private static final OptionComponentFactory<Map<Identifier, Boolean>> PROVIDER_CONFIG_FACTORY = (model, option) -> {
        var container = new ProviderConfigContainer(option);
        return new OptionComponentFactory.Result(container, container);
    };

    private static class ProviderConfigContainer extends VerticalFlowLayout implements OptionComponent {

        protected final Map<Identifier, Boolean> backingMap;

        @SuppressWarnings("UnstableApiUsage")
        protected ProviderConfigContainer(Option<Map<Identifier, Boolean>> option) {
            super(Sizing.fill(100), Sizing.content());
            this.backingMap = new HashMap<>(option.value());

            for (var targetType : OwoWhatsThis.TARGET_TYPE) {
                var layout = Containers.collapsible(
                        Sizing.content(), Sizing.content(),
                        Text.translatable("targetType." + OwoWhatsThis.TARGET_TYPE.getId(targetType).toTranslationKey()),
                        true
                );

                var applicableProviders = new ArrayList<InformationProvider<?, ?>>();
                for (var provider : OwoWhatsThis.INFORMATION_PROVIDER) {
                    if (provider.applicableTargetType() == targetType) applicableProviders.add(provider);
                }

                var optionGrid = Containers.grid(Sizing.fill(100), Sizing.content(), MathHelper.ceilDiv(applicableProviders.size(), 2), 2);
                layout.child(optionGrid);

                for (int i = 0; i < applicableProviders.size(); i++) {
                    final var providerId = OwoWhatsThis.INFORMATION_PROVIDER.getId(applicableProviders.get(i));

                    optionGrid.child(
                            Containers.horizontalFlow(Sizing.fill(50), Sizing.fixed(30)).<FlowLayout>configure(optionLayout -> {
                                optionLayout.padding(Insets.of(5)).verticalAlignment(VerticalAlignment.CENTER);

                                final var providerName = Text.translatable("informationProvider." + providerId.toTranslationKey());

                                optionLayout.child(new SearchAnchorComponent(optionLayout, Option.Key.ROOT, providerName::getString));
                                optionLayout.child(Components.label(providerName))
                                        .child(new ProviderConfigButton().<ProviderConfigButton>configure(button -> {
                                                    button.onChanged(state -> {
                                                        if (state == ProviderState.ENABLED) this.backingMap.remove(providerId);
                                                        else this.backingMap.put(providerId, state == ProviderState.WHEN_SNEAKING);

                                                        if (state == ProviderState.WHEN_SNEAKING) {
                                                            button.tooltip(Text.translatable("text.owo-whats-this.config.provider_toggle.when_sneaking.tooltip"));
                                                        } else {
                                                            button.tooltip((List<TooltipComponent>) null);
                                                        }
                                                    });
                                                    button.init(this.backingMap.containsKey(providerId) ?
                                                            !this.backingMap.get(providerId)
                                                                    ? ProviderState.DISABLED
                                                                    : ProviderState.WHEN_SNEAKING
                                                            : ProviderState.ENABLED);
                                                    button.renderer(ButtonComponent.Renderer.flat(0, 0x77000000, 0));
                                                    button.positioning(Positioning.relative(100, 50));
                                                    button.margins(Insets.right(5)).sizing(Sizing.fixed(25), Sizing.fixed(15));
                                                })
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
            return this.backingMap;
        }
    }
}
