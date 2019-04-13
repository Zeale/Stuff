package zeale.apps.stuff;

import java.io.File;

public class Stuff {

	/**
	 * The program's installation directory. This is laxly detected (as of now) by
	 * simply getting the program's working directory. Some sort of storage API will
	 * need to be made later.
	 */
	private static final File INSTALLATION_DIRECTORY = new File("");

	public static void main(String[] args) {
		// The application can be launched from here, but this will just revert to the
		// Launch class's launch method.
		Launch.main(args);
	}
}
