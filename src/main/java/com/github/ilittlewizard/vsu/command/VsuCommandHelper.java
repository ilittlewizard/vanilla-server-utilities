package com.github.ilittlewizard.vsu.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class VsuCommandHelper {
    private VsuCommandHelper() {

    }
    public static <T> LiteralArgumentBuilder<ServerCommandSource> registerPropertyCommand(
            String propertyName,
            ArgumentType<T> propertyArgumentType,
            Class<T> propertyClass,
            Consumer<T> setter,
            Predicate<T> validator,
            String validatorText,
            Supplier<String> getter) {
        return literal(propertyName)
                .then(argument("value", propertyArgumentType)
                        .executes(src -> {
                            T value = src.getArgument("value", propertyClass);
                            if(!validator.test(value)) {
                                src.getSource().sendError(Text.of(validatorText));
                            } else {
                                setter.accept(value);
                                Text feedback = Text.of(propertyName + " has been set to: " + getter.get());
                                src.getSource().sendFeedback(feedback, false);
                            }
                            return 0;
                        }))
                .executes(src -> {
                    Text feedback = Text.of(propertyName + " is currently set to: " + getter.get());
                    src.getSource().sendFeedback(feedback, false);
                    return 0;
                });
    }

    public static <T> LiteralArgumentBuilder<ServerCommandSource> registerBooleanPropertyCommand(
            String propertyName,
            Consumer<Boolean> setter,
            Supplier<Boolean> getter) {
        return registerPropertyCommand(
                propertyName,
                BoolArgumentType.bool(),
                Boolean.class,
                setter,
                x -> true,
                null,
                () -> getter.get().toString()
        );
    }

    public static <T> LiteralArgumentBuilder<ServerCommandSource> registerIntegerPropertyCommand(
            String propertyName,
            Consumer<Integer> setter,
            Predicate<Integer> validator,
            String validatorText,
            Supplier<Integer> getter) {
        return registerPropertyCommand(
                propertyName,
                IntegerArgumentType.integer(),
                Integer.class,
                setter,
                validator,
                validatorText,
                () -> getter.get().toString()
        );
    }
}
