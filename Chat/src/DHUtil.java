
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

// TODO: Auto-generated Javadoc
/**
 * The Class DHUtil.
 * * Klasa odpowiedzialna jest za operatory w protokole Diffiego-Hellmana:
 * ♪ Generowanie pary kluczy; 
 * ♪ Generowanie wspólnego sekretu;
 * ♪ Dekodowanie klucza poublicznego z formatu byte[] do formatu PublicKey;
 * ♪ Utworzenie klucza sekretnego na podstawie wspólnego sekretu;
 */
public class DHUtil {
	
	/**
	 * Generate DH key pair.
	 * Funkcja:
	 * ♪ Generuje parę kluczy java.security.KeyPairGenerator dla protokołu DH;
	 * ♪ Zwraca parę kluczy jako typ java.security.Keypair;
	 *
	 * @return KeyPair the key pair
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 */
	
    public static KeyPair generateDHKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
        keyPairGenerator.initialize(4096);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * Generate shared secret.
     * Funkcja:
     * ♪ Przyjmuje prywatny klucz instancj klasy, z której funkcja jest wywoływana;
     * ♪ Przyjmuje publiczny klucz instancji drugiej klasy.
     * ♪ Na ich podstawie generuje wspólny sekret dla obu instancji obu klas.
     *
     * @param privateKey the private key
     * @param publicKey the public key
     * @return the byte[]
     * @throws Exception the exception
     */
    
    public static byte[] generateSharedSecret(PrivateKey privateKey, PublicKey publicKey) throws Exception {
        KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
        keyAgreement.init(privateKey);
        keyAgreement.doPhase(publicKey, true);
        return keyAgreement.generateSecret();
    }
    
    /**
     * Decode public key.
     *  Funkcja:
     * ♪ Konwertuje klucz publiczny, przesłany sieciowo, z typu danych byte[] na typ danych PublicKey;
     * ♪ Używana jest specyfikacja X.509, ponieważ jest standardem definiującym formaty certyfikatów kluczy publicznych.
     *   Składa się na zakodowany odpowiednio klucz i metadane dotyczące tego klucza.
     *
     * @param encodedPublicKey the encoded public key
     * @return the public key
     * @throws Exception the exception
     */
    public static PublicKey decodePublicKey(byte[] encodedPublicKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(encodedPublicKey);
        return keyFactory.generatePublic(x509KeySpec);
    }
    
    
    /**
     * Creates the AES key from shared secret.
     * * Funkcja:
     * ♪ Tworzy sekretny klucz AES na podstawie wspólnego sekretu, wykorzystując pierwsze 16 bajtów skrótu wspólnego sekretu
     *   jako klucz AES.
     *
     * @param sharedSecret the shared secret
     * @return the secret key
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    public static SecretKey createAESKeyFromSharedSecret(byte[] sharedSecret) throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] key = sha256.digest(sharedSecret);
        return new SecretKeySpec(key, 0, 16, "AES");
    }
}
