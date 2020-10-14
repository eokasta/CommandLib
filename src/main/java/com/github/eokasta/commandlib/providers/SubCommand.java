package com.github.eokasta.commandlib.providers;

import com.github.eokasta.commandlib.enums.CommandTarget;
import com.github.eokasta.commandlib.exceptions.CommandLibException;
import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@Data
public abstract class SubCommand {

    private Command command;
    private String usage;
    private String permission;
    private String description;
    private String[] name;
    @Deprecated
    private boolean onlyPlayer;
    private CommandTarget target;

    private String noPermissionMessage = "&cYou don't have permission!";

    public void setOnlyPlayer(boolean b) {
        onlyPlayer = b;
        target = b ? CommandTarget.PLAYER : CommandTarget.ALL;
    }

    public String format(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public String format(String string, Object... args) {
        return format(String.format(string, args));
    }

    public void message(String string) {
        command.getSender().sendMessage(format(string));
    }

    public void message(String string, Object... args) {
        message(format(string, args));
    }

    public abstract void execute(CommandSender commandSender, String[] args) throws CommandLibException;
}