package com.mrcrayfish.furniture.common.mail;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;

/**
 * Author: MrCrayfish
 */
public class Mail implements INBTSerializable<CompoundTag> {
    private String note;
    private ItemStack stack;
    private String sender;

    public Mail(String note, ItemStack stack, String sender) {
        this.note = note;
        this.stack = stack;
        this.sender = sender;
    }

    public Mail(CompoundTag compound, HolderLookup.Provider provider) {
        this.deserializeNBT(provider, compound);
    }

    public String getNote() {
        return this.note;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public String getSender() {
        return this.sender;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag compound = new CompoundTag();
        compound.putString("Note", this.note);
        compound.put("Item", this.stack.save(provider));
        compound.putString("Sender", this.sender);
        return compound;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compound) {
        this.note = compound.getString("Note");
        this.stack = ItemStack.parseOptional(provider, compound.getCompound("Item"));
        this.sender = compound.getString("Sender");
    }
}

