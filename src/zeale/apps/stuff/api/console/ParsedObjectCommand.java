package zeale.apps.stuff.api.console;

import org.alixia.javalibrary.commands.StringCommand;

public class ParsedObjectCommand<O> {
	private final StringCommand cmd;
	private final O data;

	public ParsedObjectCommand(StringCommand cmd, O data) {
		this.cmd = cmd;
		this.data = data;
	}

	public String cmd() {
		return cmd.command;
	}

	public String[] getArgs() {
		return cmd.args;
	}

	public StringCommand getCmd() {
		return cmd;
	}

	public O getData() {
		return data;
	}

	public String input() {
		return cmd.inputText;
	}

}
