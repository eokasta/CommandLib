package com.github.eokasta.commandlib;

import com.github.eokasta.commandlib.annotations.CommandInformation;
import com.github.eokasta.commandlib.providers.Command;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;

@RequiredArgsConstructor
@Getter
public class CommandManager {

    private final JavaPlugin plugin;
    private final HashMap<String, Command> registeredCommands = new HashMap<>();

    public void registerCommand(Command command) {
        final Class<?> clazz = command.getClass();
        if (!clazz.isAnnotationPresent(CommandInformation.class)) {
            throw new IllegalArgumentException("This class has no CommandInformation.");
        }

        final CommandInformation commandInformation = clazz.getAnnotation(CommandInformation.class);
        try {
            final Field fieldCommandMap = plugin.getServer().getClass().getDeclaredField("commandMap");
            fieldCommandMap.setAccessible(true);

            final CommandMap commandMap = (CommandMap) fieldCommandMap.get(plugin.getServer());

            if (commandInformation.name().length > 0)
                command.setName(commandInformation.name()[0]);
            if (commandInformation.name().length > 1)
                command.getAliases().addAll(Arrays.asList(commandInformation.name()));
            if (!commandInformation.usage().isEmpty())
                command.setUsage(commandInformation.usage());
            if (!commandInformation.description().isEmpty())
                command.setDescription(commandInformation.description());
            if (!commandInformation.permission().isEmpty())
                command.setPermission(commandInformation.permission());

            command.setTarget(commandInformation.target());

            commandMap.register(command.getName(), command);
            registeredCommands.put(command.getName(), command);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void registerCommands(Command... commands) {
        for (Command command : commands)
            registerCommand(command);
    }

    public void unregisterCommand(Command command) {
        try {
            final Object result = getPrivateField(plugin.getServer().getPluginManager(), "commandMap");
            final SimpleCommandMap commandMap = (SimpleCommandMap) result;
            final Object map = getPrivateField(commandMap, "knownCommands");
            final HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
            knownCommands.remove(command.getName());
            registeredCommands.remove(command.getName());

            for (String alias : command.getAliases())
                if (knownCommands.containsKey(alias) && knownCommands.get(alias).toString().contains(plugin.getName()))
                    knownCommands.remove(alias);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public static void registerCommands(JavaPlugin javaPlugin, Command... clCommands) {
        for (Command clCommand : clCommands)
            registerCommand(javaPlugin, clCommand);
    }

    @Deprecated
    public static void registerCommand(JavaPlugin javaPlugin, Command command) {
        final Class<?> clazz = command.getClass();
        if (!clazz.isAnnotationPresent(CommandInformation.class)) {
            throw new IllegalArgumentException("This class has no CommandInformation.");
        }

        final CommandInformation commandInformation = clazz.getAnnotation(CommandInformation.class);
        try {
            final Field fieldCommandMap = javaPlugin.getServer().getClass().getDeclaredField("commandMap");
            fieldCommandMap.setAccessible(true);

            final CommandMap commandMap = (CommandMap) fieldCommandMap.get(javaPlugin.getServer());

            if (commandInformation.name().length > 0)
                command.setName(commandInformation.name()[0]);
            if (commandInformation.name().length > 1)
                command.getAliases().addAll(Arrays.asList(commandInformation.name()));
            if (!commandInformation.usage().isEmpty())
                command.setUsage(commandInformation.usage());
            if (!commandInformation.description().isEmpty())
                command.setDescription(commandInformation.description());
            if (!commandInformation.permission().isEmpty())
                command.setPermission(commandInformation.permission());

            command.setOnlyPlayer(commandInformation.onlyPlayer());
            command.setTarget(commandInformation.target());

            commandMap.register(command.getName(), command);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public static void unregisterBukkitCommand(JavaPlugin javaPlugin, Command cmd) {
        try {
            final Object result = getPrivateField(javaPlugin.getServer().getPluginManager(), "commandMap");
            final SimpleCommandMap commandMap = (SimpleCommandMap) result;
            final Object map = getPrivateField(commandMap, "knownCommands");
            final HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
            knownCommands.remove(cmd.getName());

            for (String alias : cmd.getAliases())
                if (knownCommands.containsKey(alias) && knownCommands.get(alias).toString().contains(javaPlugin.getName()))
                    knownCommands.remove(alias);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object getPrivateField(Object object, String field)
            throws SecurityException,
            NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        Field objectField = clazz.getDeclaredField(field);
        objectField.setAccessible(true);
        Object result = objectField.get(object);
        objectField.setAccessible(false);
        return result;
    }


}
