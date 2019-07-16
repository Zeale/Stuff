package zeale.apps.stuff.app.console;

import static javafx.scene.paint.Color.GOLD;
import static javafx.scene.paint.Color.RED;

import org.alixia.chatroom.api.printables.StyledPrintable;
import org.alixia.javalibrary.commands.GenericCommandManager;
import org.alixia.javalibrary.commands.StringCommand;
import org.alixia.javalibrary.commands.processing.StringCommandParser;
import org.alixia.javalibrary.strings.matching.Matching;

import javafx.scene.paint.Color;
import zeale.apps.stuff.api.logging.Logging;
import zeale.apps.tools.console.logic.ConsoleLogic;
import zeale.apps.tools.console.std.StandardConsole;
import zeale.apps.tools.console.std.StandardConsole.StandardConsoleUserInput;

public final class StuffBasicConsoleLogic implements ConsoleLogic<StandardConsoleUserInput>, StyledPrintable {

	private final GenericCommandManager<StandardConsoleUserInput> inputHandler = new GenericCommandManager<>();
	private final StringCommandManager commandHandler = new StringCommandManager("");
	private final HelpBook helpBook = new HelpBook();

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
		helpBook.addCommand("help", "Lists available commands with information about them.",
				"help [page|[\\]command-name]", "?");
		new StuffCmd("help", "?") {

			@Override
			public void act(ParsedObjectCommand<StandardConsoleUserInput> data) {
				int page = 1;
				FIRST_ARG: if (data.getArgs().length == 1) {
					String arg;
					if (!data.getArgs()[0].startsWith("\\")) {
						try {
							page = Integer.parseInt(data.getArgs()[0]);
							break FIRST_ARG;
						} catch (NumberFormatException e) {
							arg = data.getArgs()[0];
						}
					} else
						arg = data.getArgs()[0].substring(1);
					helpBook.print(StuffBasicConsoleLogic.this, arg, true);
					return;
				} else if (data.getArgs().length > 1)
					err("Illegal number of arguments for command: " + data.cmd());
				try {
					helpBook.print(StuffBasicConsoleLogic.this, page);
				} catch (HelpPageException e) {
					err("Invalid page specified: " + e.page + ". The last page is " + e.maxPage + ".");
				}
			}
		};

		helpBook.addCommand("garbage-collect", "Attempts to free RAM being used by the application.", "garbage-collect",
				"gc", "clean", "cleanup");
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

		helpBook.addCommand("clear-screen", "Clears the console.", "clear-screen", "cls", "clear");
		new StuffCmd("clear", "cls", "clear-screen") {

			@Override
			public void act(ParsedObjectCommand<StandardConsoleUserInput> data) {
				console.clear();
			}
		};
	}

	private boolean printCaret;

	private final StandardConsole console;

	public StuffBasicConsoleLogic(StandardConsole console) {
		this.console = console;
	}

	private void err(String error) {
		Logging.err(error);
	}

	private void wrn(String warning) {
		Logging.wrn(warning);
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
			console.println(input.text.trim());
			if (!inputHandler.run(input)) {// Run raw input through raw input handlers.
				StringCommand parsedCmd = parser.parse(input.text.trim());// Attempt to parse the cmd.
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
