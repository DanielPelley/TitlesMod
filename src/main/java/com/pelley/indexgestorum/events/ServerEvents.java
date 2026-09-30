package com.pelley.indexgestorum.events;
import com.pelley.indexgestorum.IndexGestorum;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraft.network.chat.Component;

@EventBusSubscriber(modid = IndexGestorum.MODID , bus = EventBusSubscriber.Bus.FORGE)
public class ServerEvents {
    // This class will handle server events such as player join, leave, and other server-related events.
    // You can add methods here to listen for specific events and perform actions accordingly.

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if(event.getEntity() instanceof Creeper && event.getSource().getEntity() instanceof ServerPlayer player) {
            // Perform actions when a player is hit
            player.sendSystemMessage(Component.literal("Youve hit a creeper!"));
        }
    }

} 