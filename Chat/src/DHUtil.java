import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

public class DHUtil {
    public static KeyPair generateDHKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
        keyPairGenerator.initialize(4096);
        return keyPairGenerator.generateKeyPair();
    }

    public static byte[] generateSharedSecret(PrivateKey privateKey, PublicKey publicKey) throws Exception {
        KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
        keyAgreement.init(privateKey);
        keyAgreement.doPhase(publicKey, true);
        return keyAgreement.generateSecret();
    }

    public static PublicKey decodePublicKey(byte[] encodedPublicKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(encodedPublicKey);
        return keyFactory.generatePublic(x509KeySpec);
    }

    public static SecretKey createAESKeyFromSharedSecret(byte[] sharedSecret) throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] key = sha256.digest(sharedSecret);
        return new SecretKeySpec(key, 0, 16, "AES");
    }
}
