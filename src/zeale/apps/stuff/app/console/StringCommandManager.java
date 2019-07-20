package zeale.apps.stuff.app.console;

import org.alixia.javalibrary.commands.GenericCommandManager;
import org.alixia.javalibrary.commands.processing.StringCommandParser;

import zeale.apps.tools.console.std.StandardConsole.StandardConsoleUserInput;

class StringCommandManager extends GenericCommandManager<ParsedObjectCommand<StandardConsoleUserInput>> {

	private final StringCommandParser parser;

	public StringCommandManager(String cmdChar) {
		this(new StringCommandParser(cmdChar));
	}

	public StringCommandManager(StringCommandParser parser) {
		this.parser = parser;
	}

	public boolean run(StandardConsoleUserInput data) {
		return super.run(new ParsedObjectCommand<>(parser.parse(data.text), data));
	}
}
