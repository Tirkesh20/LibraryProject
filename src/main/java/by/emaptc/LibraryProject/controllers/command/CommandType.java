package by.emaptc.LibraryProject.controllers.command;

import by.emaptc.LibraryProject.controllers.command.adminCommand.ChangeBehaviorCommand;
import by.emaptc.LibraryProject.controllers.command.adminCommand.DeleteUserCommand;
import by.emaptc.LibraryProject.controllers.command.adminCommand.ShowUserCommand;
import by.emaptc.LibraryProject.controllers.command.authCommand.LogOutCommand;
import by.emaptc.LibraryProject.controllers.command.authCommand.LoginCommand;
import by.emaptc.LibraryProject.controllers.command.authCommand.RegisterCommand;
import by.emaptc.LibraryProject.controllers.command.common.LanguageCommand;
import by.emaptc.LibraryProject.controllers.command.userCommand.IssueCommand;
import by.emaptc.LibraryProject.controllers.command.userCommand.ShowLiteraturesCommand;
import by.emaptc.LibraryProject.controllers.command.userCommand.ShowUserBooksCommand;

import java.util.HashMap;
import java.util.Map;

public enum CommandType {
    /**
     * needs database.
     *
     *
     * <p>
     * Admin commands.
     */
    SHOW_User(new ShowUserCommand()),
    EDIT_RATE(new ChangeBehaviorCommand()),
    DELETE_USER(new DeleteUserCommand()),

//    /**
//     * Common commands.
//     */
      LOGIN(new LoginCommand()),
      LOGOUT(new LogOutCommand()),
      COMMON_REGISTER(new RegisterCommand()),
      COMMON_CHANGE_LANGUAGE(new LanguageCommand()),

    /**
     * Client Command
     */

    COMMON_ISSUE(new IssueCommand()),
    COMMON_LITERATURES(new ShowLiteraturesCommand()),
    COMMON_USER_LITERATURES(new ShowUserBooksCommand()),
    COMMON_RETURN_ISSUE(new IssueCommand());

    private final Command command;

    CommandType(Command command) {
        this.command = command;
    }

    public Command getCurrentCommand() {
        return command;
    }

    private static final Map<String, CommandType> lookup = new HashMap<>();

    static {
        for (CommandType env : CommandType.values()) {
            lookup.put(env.toString(), env);
        }
    }

    public static CommandType getCommand(String type) {
        return lookup.get(type);
    }
}
