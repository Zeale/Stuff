package zeale.apps.stuff.app.console;

import org.alixia.javalibrary.commands.GenericCommand;
import org.alixia.javalibrary.strings.matching.Matching;

import zeale.apps.stuff.api.console.ParsedObjectCommand;

abstract class StringCommand<O> implements GenericCommand<ParsedObjectCommand<O>> {

	private final Matching matching;

	public StringCommand(boolean ignoreCase, String... matches) {
		matching = ignoreCase ? Matching.ignoreCase(matches) : Matching.build(matches);
	}

	/**
	 * @param matching The {@link Matching} that will match the
	 *                 {@link ParsedObjectCommand}'s
	 *                 {@link ParsedObjectCommand#cmd() cmd()}.
	 */
	public StringCommand(Matching matching) {
		this.matching = matching;
	}

	/**
	 * Creates a new {@link StringCommand} with the given strings as the matching
	 * against the {@link ParsedObjectCommand}'s {@link ParsedObjectCommand#cmd()
	 * cmd()}. Capitalization is ignored.
	 *
	 * @param matches
	 */
	public StringCommand(String... matches) {
		this(true, matches);
	}

	@Override
	public boolean match(ParsedObjectCommand<O> data) {
		return matching.fullyMatches(data.cmd());
	}

}
