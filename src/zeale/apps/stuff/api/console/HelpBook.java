package zeale.apps.stuff.api.console;

import java.util.ArrayList;
import java.util.List;

import org.alixia.chatroom.api.printables.StyledPrintable;

import javafx.scene.paint.Color;

public class HelpBook {

	public final class CommandHelp {
		private final String name, description, usage, aliases[];

		public CommandHelp(String name, String description, String usage, String... aliases) {
			this.name = name;
			this.description = description;
			this.usage = usage;
			this.aliases = aliases;
		}
	}

	private final List<CommandHelp> helps = new ArrayList<>();

	private Color systemColor, messageColor;

	public HelpBook() {
		this(Color.RED, Color.GOLD);
	}

	public HelpBook(Color systemColor, Color messageColor) {
		this.systemColor = systemColor;
		this.messageColor = messageColor;
	}

	public void addCommand(CommandHelp help) {
		helps.add(help);
	}

	public CommandHelp addCommand(String name, String description, String usage, String... aliases) {
		CommandHelp help = new CommandHelp(name, description, usage, aliases);
		addCommand(help);
		return help;
	}

	public void print(StyledPrintable printable, CommandHelp help) {
		printable.print("Showing help for command: ", systemColor, true, false);
		printable.print(help.name, messageColor, true, false);
		printable.print(".", systemColor, true, false);
		printable.println();

		printCommandHelp(printable, help);
	}

	public void print(StyledPrintable printable, int page) throws HelpPageException {
		int item = (page - 1) * 3;
		int maxPage = (helps.size() + 2) / 3;
		if (page < 1 || page > maxPage)
			throw new HelpPageException(page, maxPage);

		printable.print("Showing page ", systemColor, true, false);
		printable.print(page + "", messageColor, true, false);
		printable.print(" of ", systemColor, true, false);
		printable.print(maxPage + "", messageColor, true, false);
		printable.print(" of help.", systemColor, true, false);
		printable.println();
		for (int i = item; i < item + 3;) {
			printCommandHelp(printable, helps.get(i));
			if (++i >= helps.size()) {
				printable.print("End of help reached.", systemColor, true, false);
				printable.println();
				return;
			}
		}
		if (page < maxPage) {
			printable.print("Type ", systemColor, true, false);
			printable.print("help " + (page + 1), messageColor, true, false);
			printable.print(" to view the next page.", systemColor, true, false);
			printable.println();
		}

	}

	public boolean print(StyledPrintable printable, String command, boolean allowAliases, boolean ignoreCase) {
		if (allowAliases) {
			if (ignoreCase) {
				for (CommandHelp ch : helps) {
					if (ch.name.equalsIgnoreCase(command)) {
						print(printable, ch);
						return true;
					}
					for (String s : ch.aliases)
						if (s.equalsIgnoreCase(command)) {
							print(printable, ch);
							return true;
						}
				}
			} else
				for (CommandHelp ch : helps) {
					if (ch.name.equals(command)) {
						print(printable, ch);
						return true;
					}
					for (String s : ch.aliases)
						if (s.equals(command)) {
							print(printable, ch);
							return true;
						}
				}
		} else if (ignoreCase) {
			for (CommandHelp ch : helps)
				if (ch.name.equalsIgnoreCase(command)) {
					print(printable, ch);
					return true;
				}
		} else
			for (CommandHelp ch : helps)
				if (ch.name.equals(command)) {
					print(printable, ch);
					return true;
				}
		return false;
	}

	private void printCommandHelp(StyledPrintable printable, CommandHelp help) {
		printable.print("Command: ", systemColor);
		printable.print(help.name, messageColor, true, false);
		printable.println();
		printable.print("   - Description: ", systemColor);
		printable.print(help.description, messageColor, false, true);
		printable.println();
		printable.print("   - Usage: ", systemColor);
		printable.print(help.usage, messageColor);
		printable.println();
		if (help.aliases.length > 0) {
			printable.print("   - Aliases: ", systemColor);
			printable.print(help.aliases[0], messageColor);
			for (int j = 1; j < help.aliases.length; j++) {
				printable.print(", ", systemColor);
				printable.print(help.aliases[j], messageColor);
			}
			printable.println();
		}
	}
}
