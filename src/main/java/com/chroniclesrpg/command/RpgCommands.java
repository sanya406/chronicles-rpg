package com.chroniclesrpg.command;

import com.chroniclesrpg.data.ClassDetector;
import com.chroniclesrpg.data.RpgData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class RpgCommands {
    private RpgCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> d) {
        d.register(Commands.literal("rpg")
            .then(Commands.literal("stats").executes(ctx -> stats(ctx.getSource())))
            .then(Commands.literal("syncclass").executes(ctx -> syncClass(ctx.getSource())))
            .then(Commands.literal("addxp")
                .requires(s -> s.hasPermission(2))
                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                    .executes(ctx -> addXp(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "amount")))))
            .then(Commands.literal("reset")
                .requires(s -> s.hasPermission(2))
                .executes(ctx -> reset(ctx.getSource())))
        );
    }

    private static ServerPlayer player(CommandSourceStack s) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        return s.getPlayerOrException();
    }

    private static int stats(CommandSourceStack s) throws Exception {
        ServerPlayer p = player(s);
        RpgData data = RpgData.get(p);
        String detected = ClassDetector.detect(p);
        data.setClassId(detected);
        s.sendSuccess(() -> Component.literal(
                "=== Chronicles RPG ===\n" +
                "Класс: " + ClassDetector.displayName(data.classId()) + "\n" +
                "Уровень: " + data.level() + "\n" +
                "XP: " + data.xp() + "/" + data.xpToNext() + "\n" +
                "Skill Points: " + data.skillPoints()), false);
        return 1;
    }

    private static int syncClass(CommandSourceStack s) throws Exception {
        ServerPlayer p = player(s);
        RpgData data = RpgData.get(p);
        data.setClassId(ClassDetector.detect(p));
        s.sendSuccess(() -> Component.literal("Класс: " + ClassDetector.displayName(data.classId())), false);
        return 1;
    }

    private static int addXp(CommandSourceStack s, int amount) throws Exception {
        ServerPlayer p = player(s);
        RpgData.get(p).addXp(p, amount);
        s.sendSuccess(() -> Component.literal("Получено RPG XP: " + amount), false);
        return 1;
    }

    private static int reset(CommandSourceStack s) throws Exception {
        ServerPlayer p = player(s);
        RpgData data = RpgData.get(p);
        data.deserializeNBT(new net.minecraft.nbt.CompoundTag());
        s.sendSuccess(() -> Component.literal("RPG-прогресс сброшен."), false);
        return 1;
    }
}
