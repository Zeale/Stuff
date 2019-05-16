package zeale.apps.stuff.api.logging;

import java.io.PrintWriter;

import org.alixia.chatroom.api.logging.Logger;

import javafx.scene.paint.Color;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import zeale.apps.stuff.Stuff;

public final class Logging {
	private Logging() {
	}

	private static final Logger standard = new Logger("STD"), error = new Logger("ERR"), warn = new Logger("WRN"),
			debug = new Logger("DBG");

	static {
		standard.bracketColor = standard.messageColor = standard.childColor = Color.GREEN;
		error.bracketColor = error.messageColor = error.childColor = Color.RED;
		warn.bracketColor = warn.messageColor = warn.childColor = Color.GOLD;
		debug.bracketColor = debug.messageColor = debug.childColor = Color.BLUE;

		standard.setPrinter(Stuff.PROGRAM_CONSOLE);
		error.setPrinter(Stuff.PROGRAM_CONSOLE);
		warn.setPrinter(Stuff.PROGRAM_CONSOLE);
		debug.setPrinter(Stuff.PROGRAM_CONSOLE);
	}

	public static void std(String text) {
		standard.log(text);
	}

	public static void err(String text) {
		error.log(text);
	}

	public static void err(Throwable error) {
		Logging.err("An error has occurred. The stack trace is as follows...");
		try (PrintWriter writer = Stuff.PROGRAM_CONSOLE.getWriter(Logging.error.messageColor, FontWeight.NORMAL,
				FontPosture.REGULAR)) {
			error.printStackTrace(writer);
		}
	}

	public static void wrn(String text) {
		warn.log(text);
	}

	public static void wrn(Throwable error) {
		wrn("An error has occurred. The stack trace is as follows...");
		try (PrintWriter writer = Stuff.PROGRAM_CONSOLE.getWriter(Logging.warn.messageColor, FontWeight.NORMAL,
				FontPosture.REGULAR)) {
			error.printStackTrace(writer);
		}
	}

	public static void dbg(String text) {
		debug.log(text);
	}

}
