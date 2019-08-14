package zeale.apps.stuff.app.guis.windows.encryption;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public interface EncryptionAlgorithm {
	static byte[] hexToBytes(String hexString) {
		int len = hexString.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2)
			data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
					+ Character.digit(hexString.charAt(i + 1), 16));
		return data;
	}

	static byte[] processKeyBytes(byte... keyBytes) {
		return HashAlgorithms.SHA_256.hash(keyBytes);
	}

	boolean available();

	byte[] decrypt(byte[] processedKeyBytes, byte... encryptedInput) throws GeneralSecurityException;

	byte[] decrypt(SecretKey key, byte... encryptedInput) throws GeneralSecurityException;

	/**
	 * Takes a given {@link String} key (just raw key input) and an
	 * <code>encryptedInput</code> {@link String}, which is the output of a call to
	 * {@link #encrypt(byte...)}
	 *
	 * @param key            The key.
	 * @param encryptedInput The encrypted input (the result of a call to any of the
	 *                       {@link #encrypt(byte...) encrypt(...)} methods.
	 * @return The bytes of the result. They can be given to a {@link String} and
	 *         decoded with UTF-8 to retrieve the original input text.
	 * @throws GeneralSecurityException In case there is an exception raised from
	 *                                  one of the security classes used, either
	 *                                  directly or indirectly, by this method.
	 */
	default byte[] decrypt(String key, byte... encryptedInput) throws GeneralSecurityException {
		return decryptRaw(key.getBytes(StandardCharsets.UTF_8), encryptedInput);
	}

	byte[] decryptRaw(byte[] unprocessedKeyBytes, byte... encryptedInput) throws GeneralSecurityException;

	byte[] encrypt(byte... input) throws GeneralSecurityException;

	byte[] encrypt(byte[] processedKeyBytes, byte... input) throws GeneralSecurityException;

	byte[] encrypt(SecretKey key, byte... input) throws GeneralSecurityException;

	default byte[] encrypt(String key, String input) throws GeneralSecurityException {
		return encryptRaw(key.getBytes(StandardCharsets.UTF_8), input.getBytes(StandardCharsets.UTF_8));
	}

	byte[] encryptRaw(byte[] unprocessedKeyBytes, byte... input) throws GeneralSecurityException;

	Cipher getCipher();

	/**
	 * Decrypts the given {@link String} by converting the hexadecimal
	 * {@link String} input to a byte array then calling
	 * {@link #decrypt(String, byte...)} and passing the given key, and the
	 * generated byte array. The result of this call is (a <code>byte</code> array
	 * and) is used to construct a {@link String} object, by decoding the bytes with
	 * UTF-8.
	 *
	 * @param key      The key to encrypt the data with; it's needed for decryption.
	 * @param hexInput The hexadecimal output to decrypt.
	 * @return The decrypted {@link String} as regular character data.
	 * @throws GeneralSecurityException In case an exception is thrown by any of the
	 *                                  security API's operations.
	 */
	default String hexDecrypt(String key, String hexInput) throws GeneralSecurityException {
		return new String(decrypt(key, hexToBytes(hexInput)), StandardCharsets.UTF_8);
	}

	/**
	 * Encrypts the given {@link String} using {@link #encrypt(String, String)},
	 * then takes the <code>byte[]</code> that that method returns and converts it
	 * to a hexadecimal {@link String}.
	 *
	 * @param key   The key to use for encrypting.
	 * @param input The input text to encrypt.
	 * @return A hexadecimal string representing the bytes of the encrypted result.
	 * @throws GeneralSecurityException In case an exception is thrown by one of the
	 *                                  underlying security API calls.
	 */
	default String hexEncrypt(String key, String input) throws GeneralSecurityException {
		return HashAlgorithm.bytesToHex(encrypt(key, input));
	}

}
