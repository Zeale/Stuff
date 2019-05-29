package zeale.apps.stuff.app.guis.windows.encryption;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public enum EncryptionAlgorithms {
	AES("AES");
	private final String name;

	private EncryptionAlgorithms(String name) {
		this.name = name;
	}

	public String algorithmName() {
		return name;
	}

	public boolean available() {
		try {
			getCipher();
			return true;
		} catch (UnsupportedOperationException e) {
			return false;
		}
	}

	public Cipher getCipher() {
		try {
			return Cipher.getInstance(name);
		} catch (GeneralSecurityException e) {
			throw new UnsupportedOperationException("The cipher " + name + " is not available.");
		}
	}

	public byte[] encrypt(byte[] processedKeyBytes, byte... input) throws GeneralSecurityException {
		return encrypt(new SecretKeySpec(processedKeyBytes, algorithmName()), input);
	}

	public byte[] encryptRaw(byte[] unprocessedKeyBytes, byte... input) throws GeneralSecurityException {
		int len = Cipher.getMaxAllowedKeyLength(name);
		byte[] processedKeyBytes = processKeyBytes(unprocessedKeyBytes);
		if (processedKeyBytes.length > len)
			processedKeyBytes = Arrays.copyOf(processedKeyBytes, len);
		return encrypt(processedKeyBytes, input);
	}

	public byte[] encrypt(String key, String input) throws GeneralSecurityException {
		return encryptRaw(key.getBytes(StandardCharsets.UTF_8), input.getBytes(StandardCharsets.UTF_8));
	}

	public String hexEncrypt(String key, String input) throws GeneralSecurityException {
		return HashAlgorithm.bytesToHex(encrypt(key, input));
	}

	public byte[] processKeyBytes(byte... keyBytes) {
		return HashAlgorithms.SHA_256.hash(keyBytes);
	}

	public byte[] encrypt(SecretKey key, byte... input) throws GeneralSecurityException {
		Cipher cipher = getCipher();
		cipher.init(Cipher.ENCRYPT_MODE, key);

		return cipher.doFinal(input);
	}

	/**
	 * Encrypts the specified input bytes using the algorithm specified by this
	 * {@link EncryptionAlgorithms} object and a randomly generated key, (created
	 * with
	 * <code>{@link KeyGenerator KeyGenerator}.{@link KeyGenerator#getInstance(String) getInstance(}{@link EncryptionAlgorithms this}.{@link #algorithmName()}{@link KeyGenerator#getInstance(String) )}</code>).
	 * 
	 * @param input The input bytes to encrypt.
	 * @throws GeneralSecurityException If a security exception is raised.
	 */
	public byte[] encrypt(byte... input) throws GeneralSecurityException {
		KeyGenerator gen = KeyGenerator.getInstance(algorithmName());
		gen.init(128);
		return encrypt(gen.generateKey(), input);
	}

}
