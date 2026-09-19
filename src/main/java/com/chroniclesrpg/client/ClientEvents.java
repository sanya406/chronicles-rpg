package com.chroniclesrpg.client;

import com.chroniclesrpg.ChroniclesRpg;
import com.chroniclesrpg.client.screen.CharacterScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChroniclesRpg.MODID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onKeyInput(net.minecraftforge.client.event.InputEvent.Key event) {
        if (KeyBindings.CHARACTER_SCREEN_KEY.consumeClick()) {
            Minecraft.getInstance().setScreen(new CharacterScreen());
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            // Дополнительная логика тика клиента
        }
    }
}
