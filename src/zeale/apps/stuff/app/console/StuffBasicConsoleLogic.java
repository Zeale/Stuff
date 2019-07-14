package zeale.apps.stuff.app.console;

import static javafx.scene.paint.Color.BLUE;
import static javafx.scene.paint.Color.GOLD;
import static javafx.scene.paint.Color.RED;

import org.alixia.chatroom.api.printables.StyledPrintable;
import org.alixia.javalibrary.commands.GenericCommandManager;
import org.alixia.javalibrary.commands.StringCommand;
import org.alixia.javalibrary.commands.processing.StringCommandParser;
import org.alixia.javalibrary.strings.matching.Matching;

import javafx.scene.paint.Color;
import zeale.apps.tools.console.logic.ConsoleLogic;
import zeale.apps.tools.console.std.StandardConsole;
import zeale.apps.tools.console.std.StandardConsole.StandardConsoleUserInput;

public final class StuffBasicConsoleLogic implements ConsoleLogic<StandardConsoleUserInput>, StyledPrintable {

	private final GenericCommandManager<StandardConsoleUserInput> inputHandler = new GenericCommandManager<>();
	private final StringCommandManager commandHandler = new StringCommandManager("");

	private static final Color DEFAULT_RESPONSE_COLOR = Color.BLACK;

	private abstract class StuffCmd extends zeale.apps.stuff.app.console.StringCommand<StandardConsoleUserInput> {

		{
			commandHandler.addCommand(this);
		}

		public StuffCmd(boolean ignoreCase, String... matches) {
			super(ignoreCase, matches);
		}

		public StuffCmd(Matching matching) {
			super(matching);
		}

		public StuffCmd(String... matches) {
			super(matches);
		}

	}

	{
		// Commands don't start with a newline
		new StuffCmd("gc", "garbage-collect", "clean", "cleanup") {

			@Override
			public void act(ParsedObjectCommand<StandardConsoleUserInput> data) {
				println("Performing program cleanup...");
				long bytes = Runtime.getRuntime().freeMemory();
				System.gc();
				long res = Runtime.getRuntime().freeMemory() - bytes;
				println("Cleanup complete. Freed: " + res + " bytes.");
			}
		};
	}

	private boolean printCaret;

	private final StandardConsole console;

	public StuffBasicConsoleLogic(StandardConsole console) {
		this.console = console;
	}

	private void printCmdErrMessage() {
		print("Command not found. Type ", RED);
		print("help", GOLD);
		println(" for a list of commands.", RED);
	}

	private void printCmdErrMessage(String cmd) {
		print("Command, ", RED);
		print(cmd, GOLD);
		print(", not found. Type ", RED);
		print("help", GOLD);
		println(" for a list of commands.", RED);
	}

	private final StringCommandParser parser = new StringCommandParser("");

	@Override
	public void handle(StandardConsoleUserInput input) {
		printCaret = true;

		if (input.text.isEmpty())
			printCmdErrMessage();
		else {
			console.println(input.text);
			if (!inputHandler.run(input)) {// Run raw input through raw input handlers.
				StringCommand parsedCmd = parser.parse(input.text);// Attempt to parse the cmd.
				if (parsedCmd == null)// If cmd couldn't be parsed, we can't send it to the cmd handlers, so show
										// unknown cmd err msg.
					printCmdErrMessage();
				// Cmd could be parsed. Run parsed cmd through cmd handlers and show cmd not
				// found err if needed.
				else if (!commandHandler.run(new ParsedObjectCommand<>(parsedCmd, input)))
					printCmdErrMessage(parsedCmd.command);
			}
		}
		console.println();
	}

	@Override
	public void print(String text, Color color, boolean bold, boolean italicized) {
		if (printCaret) {
			console.print("> ", DEFAULT_RESPONSE_COLOR);
			printCaret = false;
		}
		console.print(text, color, bold, italicized);
	}

	@Override
	public void print(String text) {
		StyledPrintable.super.print(text, DEFAULT_RESPONSE_COLOR);
	}

	@Override
	public void println() {
		StyledPrintable.super.println();
		printCaret = true;
	}

}
