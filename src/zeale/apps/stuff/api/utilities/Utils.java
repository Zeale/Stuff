package zeale.apps.stuff.api.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.UUID;

public final class Utils {

	/**
	 * <p>
	 * Attempts to find a feasible, non-existent {@link File} by creating a
	 * randomized {@link UUID} and using it and the specified
	 * <code>extension</code>, as the file's name. <b>Please note that this method
	 * does not prepend the <code>extension</code> with a '<code>.</code>'
	 * character.</b>.
	 * </p>
	 * <p>
	 * If a {@link File} with that name and the specified extension already exists
	 * in the specified <code>location</code> directory, then <code>-numb</code>,
	 * where <code>numb</code> is an int variable with the value <code>0</code>, is
	 * appended to the file's name.
	 * </p>
	 * <p>
	 * If a file with that name also exists, then <code>numb</code> is incremented
	 * and the file's feasibility is reevaluated. If such incrementation and
	 * reevaluation fails until <code>numb</code> hits 0, a
	 * {@link FileNotFoundException} is thrown.
	 * </p>
	 * 
	 * @param location  The directory to try and get an untaken filename in.
	 * @param extension The extension of the file.
	 * @return The {@link File}.
	 * @throws FileNotFoundException If no viable {@link File} could be found.
	 */
	public static final File findFeasibleFile(File location, String extension) throws FileNotFoundException {
		String uuid = UUID.randomUUID().toString();
		File file = new File(location, uuid);
		if (file.exists()) {
			int val = 0;
			while ((file = new File(location, uuid + "-" + val + extension)).exists())
				if (++val == 0)
					// If the user has 2^32 files in this directory each with the same UUID then
					// wtf.
					throw new FileNotFoundException();
		}
		return file;
	}

}
