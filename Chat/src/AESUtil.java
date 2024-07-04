/*
 * Klasa odpowiedzialna jest za:
 * ♪ szyfrowanie i deszyfrowanie wiadomości za pomocą algorytmu AES;
 * ♪ Tryb AES wykorzystywany w aplikacji to:
 *   ♫ CBC - Cipher Block Chaining - tryb wiązania bloków zaszyfrowanych z losowym wektorem początkowym,
 *   								 który w sprzężeniu zwrotnym (n-ty blok zależy od n-1-tego bloku 
 *   								 lub od wektora początowego (n=0))
 *   ♫ PKCS5Padding - Zapewnia równość długości bloków
 * ♪ Jeżeli wynikiem działania funkcji ma być zaszyfrowana wiadomość, zwracana tablica bajtów;
 * ♪ Jeżeli wynikiem działania funkcji ma być odszyfrowana wiadomość, zwracany jest String.
 */

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;

// TODO: Auto-generated Javadoc
/**
 * The Class AESUtil.
 */
public class AESUtil {
    
    /** The Constant ALGORITHM. */
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    
    /**
     * Encrypt.
     *
     * @param key the key
     * @param message the message
     * @return the byte[]
     * @throws Exception the exception
     */
    public static byte[] encrypt(SecretKey key, String message) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        byte[] iv = new byte[cipher.getBlockSize()];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
        byte[] encryptedMessage = cipher.doFinal(message.getBytes());
        return concatenate(iv, encryptedMessage);
    }

    /**
     * Decrypt.
     *
     * @param key the key
     * @param encryptedMessage the encrypted message
     * @return the string
     * @throws Exception the exception
     */
    public static String decrypt(SecretKey key, byte[] encryptedMessage) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        byte[] iv = new byte[cipher.getBlockSize()];
        System.arraycopy(encryptedMessage, 0, iv, 0, iv.length);
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivParams);
        byte[] originalMessage = cipher.doFinal(encryptedMessage, iv.length, encryptedMessage.length - iv.length);
        return new String(originalMessage);
    }

    /**
     * Concatenate.
     *
     * @param iv the iv
     * @param encryptedMessage the encrypted message
     * @return the byte[]
     */
    private static byte[] concatenate(byte[] iv, byte[] encryptedMessage) {
        byte[] result = new byte[iv.length + encryptedMessage.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(encryptedMessage, 0, result, iv.length, encryptedMessage.length);
        return result;
    }
}
