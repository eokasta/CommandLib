# CommandLib

* Create a new command:

```java
@CommandInformation(name = {"test", "teste"},
        usage = "/usage teste",
        description = "Command test for example.",
        permission = "command.example",
        target = CommandTarget.CONSOLE // only console
)
public class CommandTest extends Command {

    public CommandTest() {
        registerSubCommand(new ExampleSubCommand()); // register a subcommand in this command
    }

    @Override
    public void perform(CommandSender sender, String label, String[] args) throws CommandLibException {
        throw new CommandLibException(getUsage()); // getUsage formatted message (&)
    }
}
```

* Create a new subcommand:

```java
@SubCommandInformation(name = "example",
        description = "Example subCommand.",
        permission = "example.subcommand"
)
public class ExampleSubCommand extends SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.isOp())
            message("&aYou are an operator!");
        else
            message("&cYou not are an operator!");
    }
}
```

* Register command:

```java
public class YourMainPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        CommandManager.registerCommand(this, new CommandTest()); // It is not necessary to register with plugin.yml.
    }
}
```
