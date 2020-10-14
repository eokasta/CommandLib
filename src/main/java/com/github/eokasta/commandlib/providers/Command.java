package com.github.eokasta.commandlib.providers;

import com.github.eokasta.commandlib.annotations.SubCommandInformation;
import com.github.eokasta.commandlib.enums.CommandTarget;
import com.github.eokasta.commandlib.exceptions.CommandLibException;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Map;

@Getter
public abstract class Command extends BukkitCommand {

    private final Map<String, SubCommand> subCommands = Maps.newHashMap();

    private CommandSender sender;
    @Deprecated
    private boolean onlyPlayer;
    @Setter
    private CommandTarget target;
    private Player player;

    @Setter
    private String permission;
    @Setter
    private String noPermissionMessage = "&cYou don't have permission.";
    @Setter
    private String notAPlayerMessage = "&cOnly players can execute this command.";
    @Setter
    private String notAConsoleMessage = "&cOnly console can execute this command.";

    public Command(JavaPlugin javaPlugin) {
        super("");
    }

    public void registerSubCommand(SubCommand subCommand) {
        final Class<?> clazz = subCommand.getClass();
        if (!clazz.isAnnotationPresent(SubCommandInformation.class)) {
            throw new IllegalArgumentException("This class has no SubCommandInformation.");
        }

        final SubCommandInformation subCommandInformation = clazz.getAnnotation(SubCommandInformation.class);
        subCommand.setCommand(this);

        if (subCommandInformation.name().length > 0)
            subCommand.setName(subCommandInformation.name());
        if (!subCommandInformation.usage().isEmpty())
            subCommand.setUsage(subCommandInformation.usage());
        if (!subCommandInformation.description().isEmpty())
            subCommand.setDescription(subCommandInformation.description());
        if (!subCommandInformation.permission().isEmpty())
            subCommand.setPermission(subCommandInformation.permission());

        subCommand.setOnlyPlayer(subCommandInformation.onlyPlayer());
        subCommand.setTarget(subCommandInformation.target());

        for (final String aliases : subCommand.getName())
            subCommands.put(aliases, subCommand);

    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        try {
            this.sender = sender;
            if (sender instanceof Player)
                this.player = (Player) sender;

            if (permission != null && !sender.hasPermission(permission)) {
                message(noPermissionMessage);
                return true;
            }

            if (target == CommandTarget.PLAYER && !(sender instanceof Player)) {
                message(notAPlayerMessage);
                return true;
            }

            if (target == CommandTarget.CONSOLE && !(sender instanceof ConsoleCommandSender)) {
                message(notAConsoleMessage);
                return true;
            }

            if (args.length > 0 && subCommands.containsKey(args[0].toLowerCase())) {
                final SubCommand subCommand = subCommands.get(args[0].toLowerCase());
                if (subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission())) {
                    message(subCommand.getNoPermissionMessage());
                    return true;
                }

                if (subCommand.getTarget() == CommandTarget.PLAYER && !(sender instanceof Player)) {
                    message(notAPlayerMessage);
                    return true;
                }

                if (subCommand.getTarget() == CommandTarget.CONSOLE && !(sender instanceof ConsoleCommandSender)) {
                    message(notAConsoleMessage);
                    return true;
                }

                subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
                return true;
            }

            perform(sender, label, args);

            return true;
        } catch (CommandLibException commandLibException) {
            message(commandLibException.getMessage());
            return true;
        }
    }

    public String format(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public String format(String string, Object... args) {
        return format(String.format(string, args));
    }

    public void message(String string) {
        sender.sendMessage(format(string));
    }

    public void message(String string, Object... args) {
        message(format(string, args));
    }

    public void unformatMessage(String string) {
        sender.sendMessage(string);
    }

    public void unformatMessage(String string, Object... args) {
        sender.sendMessage(String.format(string, args));
    }

    public void setOnlyPlayer(boolean b) {
        onlyPlayer = b;
        target = b ? CommandTarget.PLAYER : CommandTarget.ALL;
    }

    public abstract void perform(CommandSender sender, String label, String[] args) throws CommandLibException;

}
