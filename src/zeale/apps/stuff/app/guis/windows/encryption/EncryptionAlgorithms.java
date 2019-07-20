package zeale.apps.stuff.app.guis.windows.encryption;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public enum EncryptionAlgorithms implements EncryptionAlgorithm {
	AES("AES"), RSA("RSA"), DES("DES"), RC2("RC2"), RC4("RC4"), RC5("RC5"), IDEA("IDEA"), BLOWFISH("Blowfish");
	private final String name;

	private EncryptionAlgorithms(String name) {
		this.name = name;
	}

	public String algorithmName() {
		return name;
	}

	@Override
	public boolean available() {
		try {
			getCipher();
			return true;
		} catch (UnsupportedOperationException e) {
			return false;
		}
	}

	@Override
	public byte[] decrypt(byte[] processedKeyBytes, byte... encryptedInput) throws GeneralSecurityException {
		return decrypt(new SecretKeySpec(processedKeyBytes, algorithmName()), encryptedInput);
	}

	@Override
	public byte[] decrypt(SecretKey key, byte... encryptedInput) throws GeneralSecurityException {
		Cipher cipher = getCipher();
		cipher.init(Cipher.DECRYPT_MODE, key);

		return cipher.doFinal(encryptedInput);
	}

	@Override
	public byte[] decryptRaw(byte[] unprocessedKeyBytes, byte... encryptedInput) throws GeneralSecurityException {
		int len = Cipher.getMaxAllowedKeyLength(algorithmName());
		byte[] processedKeyBytes = EncryptionAlgorithm.processKeyBytes(unprocessedKeyBytes);
		if (processedKeyBytes.length > len)
			processedKeyBytes = Arrays.copyOf(processedKeyBytes, len);
		return decrypt(processedKeyBytes, encryptedInput);
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
	@Override
	public byte[] encrypt(byte... input) throws GeneralSecurityException {
		KeyGenerator gen = getKeyGenerator();
		gen.init(128);
		return encrypt(gen.generateKey(), input);
	}

	@Override
	public byte[] encrypt(byte[] processedKeyBytes, byte... input) throws GeneralSecurityException {
		return encrypt(new SecretKeySpec(processedKeyBytes, algorithmName()), input);
	}

	@Override
	public byte[] encrypt(SecretKey key, byte... input) throws GeneralSecurityException {
		Cipher cipher = getCipher();
		cipher.init(Cipher.ENCRYPT_MODE, key);

		return cipher.doFinal(input);
	}

	@Override
	public byte[] encryptRaw(byte[] unprocessedKeyBytes, byte... input) throws GeneralSecurityException {
		int len = Cipher.getMaxAllowedKeyLength(algorithmName());
		byte[] processedKeyBytes = EncryptionAlgorithm.processKeyBytes(unprocessedKeyBytes);
		if (processedKeyBytes.length > len)
			processedKeyBytes = Arrays.copyOf(processedKeyBytes, len);
		return encrypt(processedKeyBytes, input);
	}

	@Override
	public Cipher getCipher() {
		try {
			return Cipher.getInstance(name);
		} catch (GeneralSecurityException e) {
			throw new UnsupportedOperationException("The cipher " + name + " is not available.");
		}
	}

	public KeyGenerator getKeyGenerator() throws NoSuchAlgorithmException {
		return KeyGenerator.getInstance(algorithmName());
	}

}
