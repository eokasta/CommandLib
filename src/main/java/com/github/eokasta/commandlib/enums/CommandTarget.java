package com.github.eokasta.commandlib.enums;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public enum CommandTarget {

    ALL, CONSOLE, PLAYER;

    public static boolean verify(CommandTarget target, CommandSender sender) {
        return (target == CommandTarget.ALL)
                || (target == CommandTarget.CONSOLE && sender instanceof ConsoleCommandSender)
                || (target == CommandTarget.PLAYER && sender instanceof Player);
    }

    public static CommandTarget targetFromSender(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender)
            return CommandTarget.CONSOLE;

        if (sender instanceof Player)
            return CommandTarget.PLAYER;

        return CommandTarget.ALL;
    }

}
