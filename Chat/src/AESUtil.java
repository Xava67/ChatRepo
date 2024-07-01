import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;

public class AESUtil {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE = 256;

    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(KEY_SIZE);
        return keyGen.generateKey();
    }

    public static byte[] encrypt(SecretKey key, String message) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        byte[] iv = new byte[cipher.getBlockSize()];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
        byte[] encryptedMessage = cipher.doFinal(message.getBytes());
        return concatenate(iv, encryptedMessage);
    }

    public static String decrypt(SecretKey key, byte[] encryptedMessage) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        byte[] iv = new byte[cipher.getBlockSize()];
        System.arraycopy(encryptedMessage, 0, iv, 0, iv.length);
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivParams);
        byte[] originalMessage = cipher.doFinal(encryptedMessage, iv.length, encryptedMessage.length - iv.length);
        return new String(originalMessage);
    }

    private static byte[] concatenate(byte[] iv, byte[] encryptedMessage) {
        byte[] result = new byte[iv.length + encryptedMessage.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(encryptedMessage, 0, result, iv.length, encryptedMessage.length);
        return result;
    }
}
