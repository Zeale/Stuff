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

		printable.print("Showing page ", Color.RED, true, false);
		printable.print(page + "", Color.GOLD, true, false);
		printable.print(" of help.", Color.RED, true, false);
		printable.println();
		for (int i = item; i < item + 3;) {
			CommandHelp commandHelp = helps.get(i);

			printable.print("Command: ", Color.RED);
			printable.print(commandHelp.name, Color.GOLD, true, false);
			printable.println();
			printable.print("   - Description: ", Color.RED);
			printable.print(commandHelp.description, Color.GOLD, false, true);
			printable.println();
			printable.print("   - Usage: ", Color.RED);
			printable.print(commandHelp.usage, Color.GOLD);
			printable.println();
			if (commandHelp.aliases.length > 0) {
				printable.print("   - Aliases: ", Color.RED);
				printable.print(commandHelp.aliases[0], Color.GOLD);
				for (int j = 1; j < commandHelp.aliases.length; j++) {
					printable.print(", ", Color.RED);
					printable.print(commandHelp.aliases[j], Color.GOLD);
				}
				printable.println();
			}
			if (++i >= helps.size()) {
				printable.print("End of help reached.", Color.RED, true, false);
				return;
			}
		}
		int maxPage = (helps.size() + 2) / 3;
		if (page < maxPage) {
			printable.print("Type ", Color.RED, true, false);
			printable.print("help " + (page + 1), Color.GOLD, true, false);
			printable.print(" to view the next page.", Color.RED, true, false);
			printable.println();
		}

	}

	public void print(StyledPrintable printable, String command, boolean allowAliases) {
		// TODO
	}
}
