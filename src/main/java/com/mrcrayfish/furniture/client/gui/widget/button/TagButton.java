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
    private static final ResourceLocation TABS = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/container/creative_inventory/tabs.png");

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
        int width = this.toggled ? 32 : 28;
        int textureX = 26;
        int textureY = this.toggled ? 32 : 0;
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        // Draw rotated texture using blit with rotation (swap width/height for 90-degree rotation)
        graphics.blit(TABS, this.getX(), this.getY(), textureX, textureY, 26, width);
        graphics.renderItem(this.stack, this.getX() + 8, this.getY() + 5);
        RenderSystem.disableBlend();
    }

    public void updateState() {
        this.toggled = this.category.isEnabled();
    }
}
