package com.chroniclesrpg.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class RpgCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("rpg")
            .then(Commands.literal("stats").executes(ctx -> {
                try {
                    return stats(ctx.getSource());
                } catch (CommandSyntaxException e) {
                    ctx.getSource().sendFailure(Component.literal("Ошибка выполнения команды: " + e.getMessage()));
                    return 0;
                }
            }))
            .then(Commands.literal("syncclass").executes(ctx -> {
                try {
                    return syncClass(ctx.getSource());
                } catch (CommandSyntaxException e) {
                    ctx.getSource().sendFailure(Component.literal("Ошибка выполнения команды: " + e.getMessage()));
                    return 0;
                }
            }))
            .then(Commands.literal("addxp")
                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                    .executes(ctx -> {
                        try {
                            return addXp(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "amount"));
                        } catch (CommandSyntaxException e) {
                            ctx.getSource().sendFailure(Component.literal("Ошибка выполнения команды: " + e.getMessage()));
                            return 0;
                        }
                    })))
            .then(Commands.literal("reset").executes(ctx -> {
                try {
                    return reset(ctx.getSource());
                } catch (CommandSyntaxException e) {
                    ctx.getSource().sendFailure(Component.literal("Ошибка выполнения команды: " + e.getMessage()));
                    return 0;
                }
            }))
        );
    }

    private static int stats(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        source.sendSuccess(() -> Component.literal("Статистика RPG для " + player.getName().getString()), false);
        return 1;
    }

    private static int syncClass(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        source.sendSuccess(() -> Component.literal("Класс синхронизирован для " + player.getName().getString()), false);
        return 1;
    }

    private static int addXp(CommandSourceStack source, int amount) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        source.sendSuccess(() -> Component.literal("Добавлено " + amount + " XP игроку " + player.getName().getString()), false);
        return 1;
    }

    private static int reset(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        source.sendSuccess(() -> Component.literal("Прогресс RPG сброшен для " + player.getName().getString()), false);
        return 1;
    }
}
