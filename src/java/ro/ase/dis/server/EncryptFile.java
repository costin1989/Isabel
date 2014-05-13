/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.ase.dis.server;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author costin1989
 */
public class EncryptFile {

    public EncryptFile() {
    }

    /**
     * 
     * @return Base64 encoded String of the encrypted byte array
     */
    public static String getEncryptFile() {
        try {
            String message = "This is a secret message!";
            System.out.println("Plaintext: " + message + "\n");

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec("C0st1n123".toCharArray(),"salt".getBytes(), 1024, 128);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey skeySpec = new SecretKeySpec(tmp.getEncoded(), "AES");

            // build the initialization vector.  This example is all zeros, but it 
            // could be any value or generated using a random number generator.
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            // initialize the cipher for encrypt mode
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivspec);

            // encrypt the message
            byte[] encrypted = cipher.doFinal(message.getBytes());
            System.out.println("Ciphertext: " + hexEncode(encrypted) + "\n");

            String e = Base64.encodeBase64String(encrypted);
            return e;


        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }

    /**
     * Hex encodes a byte array. <BR>
     * Returns an empty string if the input array is null or empty.
     *
     * @param input bytes to encode
     * @return string containing hex representation of input byte array
     */
    private static String hexEncode(byte[] input) {
        if (input == null || input.length == 0) {
            return "";
        }

        int inputLength = input.length;
        StringBuilder output = new StringBuilder(inputLength * 2);

        for (int i = 0; i < inputLength; i++) {
            int next = input[i] & 0xff;
            if (next < 0x10) {
                output.append("0");
            }

            output.append(Integer.toHexString(next));
        }

        return output.toString();
    }
}
