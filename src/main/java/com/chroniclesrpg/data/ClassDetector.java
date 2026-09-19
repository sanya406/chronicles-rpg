package com.chroniclesrpg.data;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ClassDetector {
    private static final Map<String, String> CLASSES = new LinkedHashMap<>();
    static {
        CLASSES.put("vagabond", "Бродяга");
        CLASSES.put("rogue", "Негодяй");
        CLASSES.put("bounty_hunter", "Охотник за головами");
        CLASSES.put("forgotten_knight", "Забытый рыцарь");
        CLASSES.put("titan", "Титан");
        CLASSES.put("oni_slayer", "Они-убийца");
        CLASSES.put("exiled", "Сослан");
    }

    private ClassDetector() {}

    public static String detect(ServerPlayer player) {
        for (String id : CLASSES.keySet()) {
            Advancement adv = player.server.getAdvancements()
                    .getAdvancement(new ResourceLocation("dungeons_and_combat", id));
            if (adv != null) {
                AdvancementProgress progress = player.getAdvancements().getOrStartProgress(adv);
                if (progress.isDone()) return id;
            }
        }
        return "unknown";
    }

    public static String displayName(String id) {
        return CLASSES.getOrDefault(id, "Класс не выбран");
    }
}
