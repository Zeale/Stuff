package zeale.apps.stuff.app.guis.windows.encryption;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

enum HashAlgorithms {
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
	 * @return
	 * @throws UnsupportedOperationException
	 */
	public MessageDigest getDigest() throws UnsupportedOperationException {
		try {
			return MessageDigest.getInstance(name);
		} catch (NoSuchAlgorithmException e) {
			throw new UnsupportedOperationException("The " + name + " hashing algorithm is not available.");
		}
	}

	public static void main(String[] args) {
		String text = "abc";
		System.out.println(SHA_1.hexHash(text));
	}

	/**
	 * Hashes the given text with the algorithm represented by this
	 * {@link HashAlgorithms} object and returns the output as a hexadecimal
	 * {@link String}.
	 * 
	 * @param text The input text to hash.
	 * @return Returns a {@link String} of hex digits representing the hashed input.
	 */
	public String hexHash(String text) {
		return bytesToHex(hash(text));
	}

	public byte[] hash(String text) {
		MessageDigest digest = getDigest();
		return digest.digest(text.getBytes(StandardCharsets.UTF_8));
	}

	private String bytesToHex(byte[] bytes) {
		return new BigInteger(1, bytes).toString(16);
	}

}