package dev.wuffs.bcc;

import com.google.gson.Gson;
import dev.wuffs.bcc.data.BetterStatus;
import dev.wuffs.bcc.data.BetterStatusServerHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod(Constants.MOD_ID)
public class BCCForge {
    
    public BCCForge(IEventBus modEventBus, ModContainer modContainer) {
        Constants.LOG.info("Better Compatibility Checker starting");
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.CONFIG);
        modEventBus.addListener(this::doCommonSetup);
//        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }

    private void doCommonSetup(final FMLCommonSetupEvent event) {
        Constants.LOG.info("Better Compatibility Checker setup");
        if(Config.useMetadata.get()) {
            Path metaFile = FMLPaths.CONFIGDIR.get().resolve("metadata.json");
            if(!Files.exists(metaFile)) {
                Constants.LOG.error("No metadata.json found, falling back to config values");
            }else {
                try {
                    Constants.LOG.info("Loading metadata.json");
                    Metadata metadata = new Gson().fromJson(Files.newBufferedReader(metaFile), Metadata.class);
                    BetterStatusServerHolder.INSTANCE.setStatus(new BetterStatus(
                            metadata.id,
                            metadata.name,
                            metadata.version.name,
                            metadata.version.id,
                            metadata.version.type,
                            true
                    ));
                    return;
                }catch(IOException e) {
                    Constants.LOG.error("Failed to read metadata.json", e);
                }
            }
        }

        BetterStatusServerHolder.INSTANCE.setStatus(new BetterStatus(
                Config.modpackProjectID.get(),
                Config.modpackName.get(),
                Config.modpackVersion.get(),
                -1,
                "unknown",
                false
        ));
    }
}