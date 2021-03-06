package zeale.apps.stuff.app.console;

import static javafx.scene.paint.Color.DARKORANGE;
import static javafx.scene.paint.Color.GOLD;

import java.lang.reflect.InvocationTargetException;

import org.alixia.chatroom.api.printables.StyledPrintable;
import org.alixia.javalibrary.commands.GenericCommandManager;
import org.alixia.javalibrary.commands.StringCommand;
import org.alixia.javalibrary.commands.processing.StringCommandParser;
import org.alixia.javalibrary.strings.matching.Matching;

import javafx.scene.paint.Color;
import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.api.console.HelpBook;
import zeale.apps.stuff.api.console.HelpPageException;
import zeale.apps.stuff.api.console.ParsedObjectCommand;
import zeale.apps.stuff.api.console.HelpBook.CommandHelp;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.stuff.api.guis.windows.Window.WindowLoadFailureException;
import zeale.apps.stuff.api.logging.Logging;
import zeale.apps.tools.console.logic.ConsoleLogic;
import zeale.apps.tools.console.std.StandardConsole;
import zeale.apps.tools.console.std.StandardConsole.StandardConsoleUserInput;

public final class StuffBasicConsoleLogic implements ConsoleLogic<StandardConsoleUserInput>, StyledPrintable {

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

	private static final Color DEFAULT_RESPONSE_COLOR = Color.BLACK, DEFAULT_SYSTEM_MESSAGE_COLOR = DARKORANGE,
			DEFAULT_VALUE_COLOR = GOLD;
	private final GenericCommandManager<StandardConsoleUserInput> inputHandler = new GenericCommandManager<>();

	private final StringCommandManager commandHandler = new StringCommandManager("");

	private final HelpBook helpBook = new HelpBook(DEFAULT_SYSTEM_MESSAGE_COLOR, DEFAULT_VALUE_COLOR);

	{
		CommandHelp helpCommandHelp = helpBook.addCommand("help",
				"Lists available commands with information about them.", "help [page|['\\']command-name]", "?");
		new StuffCmd("help", "?") {

			@Override
			public void act(ParsedObjectCommand<StandardConsoleUserInput> data) {
				int page = 1;
				FIRST_ARG: if (data.getArgs().length == 1) {
					String arg;
					if (!data.getArgs()[0].startsWith("\\"))
						try {
							page = Integer.parseInt(data.getArgs()[0]);
							break FIRST_ARG;
						} catch (NumberFormatException e) {
							arg = data.getArgs()[0];
						}
					else
						arg = data.getArgs()[0].substring(1);
					if (!helpBook.print(StuffBasicConsoleLogic.this, arg, true, true))
						err("No command with the name or alias: \"" + arg + "\" was found.");
					return;
				} else if (data.getArgs().length > 1) {
					err("Illegal number of arguments for command: " + data.cmd());
					helpBook.print(StuffBasicConsoleLogic.this, helpCommandHelp);
					return;
				}
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

				print("Cleanup complete. Freed: " + res + " bytes.");
				if (res >= 1024l * 1024 * 1024 * 1024)
					print(" (" + res / 1024 / 1024 / 1024 / 1024 + "TB)");
				else if (res >= 1024 * 1024 * 1024)
					print(" (" + res / 1024 / 1024 / 1024 + "GB)");
				else if (res >= 1024 * 1024)
					print(" (" + res / 1024 / 1024 + "MB)");
				else if (res >= 1024)
					print(" (" + res / 1024 + "KB)");
				println();
			}
		};

		helpBook.addCommand("clear-screen", "Clears the console.", "clear-screen", "cls", "clear");
		new StuffCmd("clear", "cls", "clear-screen") {

			@Override
			public void act(ParsedObjectCommand<StandardConsoleUserInput> data) {
				console.clear();
			}
		};

		helpBook.addCommand("open-window", "Opens the specified window.",
				"open-window (['~']alias|absolute-window-reference)");
		new StuffCmd("open-window") {

			@Override
			public void act(ParsedObjectCommand<StandardConsoleUserInput> data) {
				if (data.getArgs().length != 1)
					err("Illegal number of arguments for command: " + data.cmd());
				else
					try {
						Stuff.displayWindow(
								(Window) Class.forName(data.getArgs()[0]).getDeclaredConstructor().newInstance());
						print("The task completed successfully.");
					} catch (InstantiationException e) {
						err("Cannot create an instance of that window directly. (Abstract Class)");
					} catch (IllegalAccessException e) {
						err("That window cannot be opened by this means. (Access Restrictions)");
					} catch (ClassNotFoundException e) {
						err("That window couldn't be found. (Class Not Found)");
					} catch (WindowLoadFailureException e) {
						err("An error occurred while launching that window. (Window Init Raised Error)");
					} catch (IllegalArgumentException e) {
						err("Opening that window is not yet supported; (window creation requires extraneous information of which there is no means for conveying). ([No Public No-arg]/Enum)");
					} catch (InvocationTargetException e) {
						err("An error was encountered while initializing the window. (Wrapped Exception)");
					} catch (NoSuchMethodException e) {
						err("That window can't be initialized by this means. (No Constructor Match)");
					} catch (SecurityException e) {
						err("A security manager is present and it prevented accessing/initializing the specified window. (Security Manager)");
					}
			}
		};

		helpBook.addCommand("echo",
				"Prints each given argument back to the console. This command is primarily meant for debugging.",
				"echo (argument)...", "print", "write");
		new StuffCmd("echo", "print", "write") {

			@Override
			public void act(ParsedObjectCommand<StandardConsoleUserInput> data) {
				if (data.getArgs().length == 0)
					err("Please include at least one argument when invoking this command.");
				else {
					for (String s : data.getArgs())
						print(s);
					println();
				}
			}
		};

		helpBook.addCommand("echo-raw", "Prints the command invocation of this command back to the console.",
				"echo-raw[*]", "print-raw", "write-raw");
		new StuffCmd("echo-raw", "print-raw", "write-raw") {

			@Override
			public void act(ParsedObjectCommand<StandardConsoleUserInput> data) {
				println(data.input());
			}
		};
	}

	private boolean printCaret;

	private final StandardConsole console;

	private final StringCommandParser parser = new StringCommandParser("");

	public StuffBasicConsoleLogic(StandardConsole console) {
		this.console = console;
	}

	private static void err(String error) {
		Logging.err(error);
	}

	@Override
	public void handle(StandardConsoleUserInput input) {
		printCaret = true;

		if (input.text == null || input.text.isEmpty())
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
	public void print(String text) {
		StyledPrintable.super.print(text, DEFAULT_RESPONSE_COLOR);
	}

	@Override
	public void print(String text, Color color, boolean bold, boolean italicized) {
		if (printCaret) {
			console.print("> ", DEFAULT_RESPONSE_COLOR);
			printCaret = false;
		}
		console.print(text, color, bold, italicized);
	}

	private void printCmdErrMessage() {
		print("Please type a command, or type ", DEFAULT_SYSTEM_MESSAGE_COLOR);
		print("help", DEFAULT_VALUE_COLOR);
		println(" for a list of commands.", DEFAULT_SYSTEM_MESSAGE_COLOR);
	}

	private void printCmdErrMessage(String cmd) {
		print("Command, ", DEFAULT_SYSTEM_MESSAGE_COLOR);
		print(cmd, DEFAULT_VALUE_COLOR);
		print(", not found. Type ", DEFAULT_SYSTEM_MESSAGE_COLOR);
		print("help", DEFAULT_VALUE_COLOR);
		println(" for a list of commands.", DEFAULT_SYSTEM_MESSAGE_COLOR);
	}

	@Override
	public void println() {
		StyledPrintable.super.println();
		printCaret = true;
	}

}
