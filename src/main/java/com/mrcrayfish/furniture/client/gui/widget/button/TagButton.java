package com.mrcrayfish.furniture.client.gui.widget.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.furniture.client.event.CreativeScreenEvents;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class TagButton extends Button {
    private static final ResourceLocation SELECTED_SPRITE = ResourceLocation.fromNamespaceAndPath("minecraft", "container/creative_inventory/tab_top_selected_2");
    private static final ResourceLocation UNSELECTED_SPRITE = ResourceLocation.fromNamespaceAndPath("minecraft", "container/creative_inventory/tab_top_unselected_2");

    private final CreativeScreenEvents.TagFilter category;
    private final ItemStack stack;
    private boolean toggled;

    public TagButton(int x, int y, CreativeScreenEvents.TagFilter category, OnPress onPress) {
        super(x, y, 32, 26, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
        this.category = category;
        this.stack = category.getIcon();
        this.toggled = category.isEnabled();
        this.setTooltip(Tooltip.create(category.getName()));
    }

    public CreativeScreenEvents.TagFilter getCategory() {
        return this.category;
    }

    @Override
    public void onPress() {
        this.toggled = !this.toggled;
        this.category.setEnabled(this.toggled);
        super.onPress();
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        ResourceLocation sprite = this.toggled ? SELECTED_SPRITE : UNSELECTED_SPRITE;
        int height = this.toggled ? 32 : 28;
        graphics.blitSprite(sprite, this.getX(), this.getY(), 26, height);
        graphics.renderItem(this.stack, this.getX() + 8, this.getY() + 5);
        RenderSystem.disableBlend();
    }

    public void updateState() {
        this.toggled = this.category.isEnabled();
    }
}
