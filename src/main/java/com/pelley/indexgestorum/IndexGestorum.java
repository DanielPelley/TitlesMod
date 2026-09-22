package com.pelley.indexgestorum;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(IndexGestorum.MODID)
public class IndexGestorum
{
    // Must match mod_id in gradle.properties
    public static final String MODID = "index_gestorum";
    public static final Logger LOGGER = LogUtils.getLogger();

    public IndexGestorum(FMLJavaModLoadingContext context)
    {
        context.getModEventBus().addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        LOGGER.info("Index Gestorum loaded");
    }
}
