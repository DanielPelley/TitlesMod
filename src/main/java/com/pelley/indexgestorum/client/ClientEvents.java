package com.pelley.indexgestorum.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.pelley.indexgestorum.IndexGestorum;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

public final class ClientEvents
{
    public static final KeyMapping OPEN_TITLES = new KeyMapping(
            "key.index_gestorum.open_titles",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            "key.categories.index_gestorum");

    private ClientEvents() {}

    @Mod.EventBusSubscriber(modid = IndexGestorum.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class ModBus
    {
        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent event)
        {
            event.register(OPEN_TITLES);
        }
    }

    @Mod.EventBusSubscriber(modid = IndexGestorum.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static final class ForgeBus
    {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event)
        {
            if (event.phase != TickEvent.Phase.END) return;

            while (OPEN_TITLES.consumeClick())
            {
                Minecraft mc = Minecraft.getInstance();
                if (mc.screen == null) mc.setScreen(new TitlesScreen());
            }
        }
    }
}
