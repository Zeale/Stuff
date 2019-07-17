package zeale.apps.stuff.app.console;

import java.util.ArrayList;
import java.util.List;

import org.alixia.chatroom.api.printables.StyledPrintable;

import javafx.scene.paint.Color;

class HelpBook {

	private final List<CommandHelp> helps = new ArrayList<>();

	public final class CommandHelp {
		private final String name, description, usage, aliases[];

		public CommandHelp(String name, String description, String usage, String... aliases) {
			this.name = name;
			this.description = description;
			this.usage = usage;
			this.aliases = aliases;
		}

	}

	private Color systemColor, messageColor;

	public HelpBook(Color systemColor, Color messageColor) {
		this.systemColor = systemColor;
		this.messageColor = messageColor;
	}
	
	public HelpBook() {
		this(Color.RED, Color.GOLD);
	}

	public void addCommand(CommandHelp help) {
		helps.add(help);
	}

	public void addCommand(String name, String description, String usage, String... aliases) {
		helps.add(new CommandHelp(name, description, usage, aliases));
	}

	public void print(StyledPrintable printable, int page) throws HelpPageException {
		int item = (page - 1) * 3;
		if (item < 0 || item > helps.size())
			throw new HelpPageException(page, (helps.size() + 2) / 3);

		printable.print("Showing page ", systemColor, true, false);
		printable.print(page + "", messageColor, true, false);
		printable.print(" of help.", systemColor, true, false);
		printable.println();
		for (int i = item; i < item + 3;) {
			CommandHelp commandHelp = helps.get(i);

			printable.print("Command: ", systemColor);
			printable.print(commandHelp.name, messageColor, true, false);
			printable.println();
			printable.print("   - Description: ", systemColor);
			printable.print(commandHelp.description, messageColor, false, true);
			printable.println();
			printable.print("   - Usage: ", systemColor);
			printable.print(commandHelp.usage, messageColor);
			printable.println();
			if (commandHelp.aliases.length > 0) {
				printable.print("   - Aliases: ", systemColor);
				printable.print(commandHelp.aliases[0], messageColor);
				for (int j = 1; j < commandHelp.aliases.length; j++) {
					printable.print(", ", systemColor);
					printable.print(commandHelp.aliases[j], messageColor);
				}
				printable.println();
			}
			if (++i >= helps.size()) {
				printable.print("End of help reached.", systemColor, true, false);
				return;
			}
		}
		int maxPage = (helps.size() + 2) / 3;
		if (page < maxPage) {
			printable.print("Type ", systemColor, true, false);
			printable.print("help " + (page + 1), messageColor, true, false);
			printable.print(" to view the next page.", systemColor, true, false);
			printable.println();
		}

	}

	public void print(StyledPrintable printable, String command, boolean allowAliases) {
		// TODO
	}
}
