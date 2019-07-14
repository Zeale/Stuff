package zeale.apps.stuff.app.console;

import static javafx.scene.paint.Color.GOLD;
import static javafx.scene.paint.Color.RED;

import org.alixia.chatroom.api.printables.StyledPrintable;
import org.alixia.javalibrary.commands.GenericCommandManager;
import org.alixia.javalibrary.commands.StringCommand;
import org.alixia.javalibrary.commands.processing.StringCommandParser;

import zeale.apps.tools.console.logic.ConsoleLogic;
import zeale.apps.tools.console.std.StandardConsole;
import zeale.apps.tools.console.std.StandardConsole.StandardConsoleUserInput;

public final class StuffBasicConsoleLogic implements ConsoleLogic<StandardConsoleUserInput> {

	private final GenericCommandManager<StandardConsoleUserInput> inputHandler = new GenericCommandManager<>();
	private final StringCommandManager commandHandler = new StringCommandManager("");

	private final StandardConsole console;

	public StuffBasicConsoleLogic(StandardConsole console) {
		this.console = console;
	}

	private static void printCmdErrMessage(StyledPrintable console) {
		console.print("Command not found. Type ", RED);
		console.print("help", GOLD);
		console.println(" for a list of commands.", RED);
	}

	private static void printCmdErrMessage(StyledPrintable console, String cmd) {
		console.print("Command, ", RED);
		console.print(cmd, GOLD);
		console.print(", not found. Type ", RED);
		console.print("help", GOLD);
		console.println(" for a list of commands.", RED);
	}

	private final StringCommandParser parser = new StringCommandParser("");

	@Override
	public void handle(StandardConsoleUserInput input) {
		if (!inputHandler.run(input)) {// Run raw input through raw input handlers.
			StringCommand parsedCmd = parser.parse(input.text);// Attempt to parse the cmd.
			if (parsedCmd == null)// If cmd couldn't be parsed, we can't send it to the cmd handlers, so show
									// unknown cmd err msg.
				printCmdErrMessage(console);
			// Cmd could be parsed. Run parsed cmd through cmd handlers and show cmd not
			// found err if needed.
			else if (!commandHandler.run(new ParsedObjectCommand<>(parsedCmd, input)))
				printCmdErrMessage(console, parsedCmd.command);
		}
	}
}
