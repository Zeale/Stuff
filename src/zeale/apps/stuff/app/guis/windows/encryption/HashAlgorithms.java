package zeale.apps.stuff.app.guis.windows.encryption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

enum HashAlgorithms implements HashAlgorithm {
	MD5("MD5"), SHA_1("SHA-1"), SHA_256("SHA-256");

	private final String name;

	private HashAlgorithms(String name) {
		this.name = name;
	}

	public String algorithmName() {
		return name;
	}

	/**
	 * Attempts to create a {@link MessageDigest} based off of this algorithm and
	 * returns <code>false</code> if an underlying exception is thrown due to the
	 * algorithm not being found.
	 * 
	 * @return <code>true</code> if this algorithm is available, <code>false</code>
	 *         otherwise.
	 */
	public boolean available() {
		return getDigest() != null;
	}

	/**
	 * Returns a {@link MessageDigest}, which can be used for hashing, that hashes
	 * with the algorithm represented by this
	 * 
	 * @return A {@link MessageDigest} object used for hashing.
	 * @throws UnsupportedOperationException In case this algorithm is unavailable
	 *                                       on the current machine.
	 */
	public MessageDigest getDigest() throws UnsupportedOperationException {
		try {
			return MessageDigest.getInstance(name);
		} catch (NoSuchAlgorithmException e) {
			throw new UnsupportedOperationException("The " + name + " hashing algorithm is not available.");
		}
	}

	public static void main(String[] args) {
		String text = "";
		System.out.println(SHA_1.hexHash(text));
	}

	@Override
	public byte[] hash(byte... input) {
		return getDigest().digest(input);
	}

}