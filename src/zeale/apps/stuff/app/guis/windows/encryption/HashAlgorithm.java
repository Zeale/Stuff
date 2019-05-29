package zeale.apps.stuff.app.guis.windows.encryption;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public interface HashAlgorithm {

	/**
	 * Hashes the given bytes and returns a byte array that is the result.
	 * 
	 * @param input The input bytes to hash.
	 * @return The output.
	 */
	byte[] hash(byte... input);

	/**
	 * Runs the given {@link String} through this hashing algorithm. The hashing
	 * algorithm expects a sequences of bytes, so the {@link String} is converted to
	 * bytes using UTF-8 encoding first.
	 * 
	 * @param input The {@link String} to hash.
	 * @return An array of <code>byte</code>s that are output of this hashing
	 *         algorithm given the input {@link String}, <code>input</code>, as
	 *         input.
	 */
	default byte[] hash(String input) {
		return hash(input.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Some algorithms are not available due to a lack of presence of their
	 * implementation on the current JVM. In this case, this method should return
	 * <code>false</code>.
	 * 
	 * @return whether or not this algorithm is available.
	 */
	boolean available();

	/**
	 * Hashes the given text with the algorithm represented by this
	 * {@link HashAlgorithms} object and returns the output as a hexadecimal
	 * {@link String}.
	 * 
	 * @param text The input text to hash.
	 * @return Returns a {@link String} of hex digits representing the hashed input.
	 */
	default String hexHash(String input) {
		return bytesToHex(hash(input));
	}

	static String bytesToHex(byte[] bytes) {
		return new BigInteger(1, bytes).toString(16);
	}
}
