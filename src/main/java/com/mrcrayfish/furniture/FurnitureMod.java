package com.mrcrayfish.furniture;

import com.mrcrayfish.furniture.client.ClientHandler;
import com.mrcrayfish.furniture.common.CommonHandler;
import com.mrcrayfish.furniture.core.*;
import com.mrcrayfish.furniture.datagen.BlockTagGen;
import com.mrcrayfish.furniture.datagen.ItemTagGen;
import com.mrcrayfish.furniture.datagen.LootTableGen;
import com.mrcrayfish.furniture.datagen.RecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

@Mod(Reference.MOD_ID)
public class FurnitureMod {
    public static final Logger LOGGER = LoggerFactory.getLogger(Reference.MOD_ID);

    public FurnitureMod(IEventBus modEventBus, ModContainer modContainer, Dist dist) {
        ModBlocks.REGISTER.register(modEventBus);
        ModItems.REGISTER.register(modEventBus);
        ModEntities.REGISTER.register(modEventBus);
        ModBlockEntities.REGISTER.register(modEventBus);
        ModContainers.REGISTER.register(modEventBus);
        ModSounds.REGISTER.register(modEventBus);
        ModRecipeTypes.REGISTER.register(modEventBus);
        ModRecipeSerializers.REGISTER.register(modEventBus);
        ModCreativeTabs.REGISTER.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.CLIENT, FurnitureConfig.clientSpec);
        modContainer.registerConfig(ModConfig.Type.COMMON, FurnitureConfig.commonSpec);
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(this::onGatherData);
        if (dist == Dist.CLIENT) {
            modEventBus.addListener(ClientHandler::onRegisterBlockColors);
            modEventBus.addListener(ClientHandler::onRegisterItemColors);
            modEventBus.addListener(ClientHandler::onRegisterRenderers);
            modEventBus.addListener(ClientHandler::onRegisterGeometryLoaders);
        }
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(CommonHandler::setup);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(ClientHandler::setup);
    }

    private void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        BlockTagGen blockTagGen = new BlockTagGen(output, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), new RecipeGen(output));
        generator.addProvider(event.includeServer(), new LootTableGen(output));
        generator.addProvider(event.includeServer(), blockTagGen);
        generator.addProvider(event.includeServer(), new ItemTagGen(output, lookupProvider, blockTagGen.contentsGetter(), existingFileHelper));
    }
}
