/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.ase.dis.beans;

import java.io.Serializable;
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
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author costin1989
 */
@Named(value = "encryptionBean")
@ViewScoped
@ManagedBean
public class EncryptionBean implements Serializable {

    /**
     * Creates a new instance of TaskBean
     */
    private String encryptedText;
    private String plainText;
    private String algorithm;
    private String secretKeyFactory;
    private String password;

    public EncryptionBean() {
    }

    public String getEncryptedText() {
        return encryptedText;
    }

    public void setEncryptedText(String encryptedText) {
        this.encryptedText = encryptedText;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getSecretKeyFactory() {
        return secretKeyFactory;
    }

    public void setSecretKeyFactory(String secretKeyFactory) {
        this.secretKeyFactory = secretKeyFactory;
    }

    public String getPlainText() {
        return plainText;
    }

    public void setPlainText(String plainText) {
        this.plainText = plainText;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void doEncrypt(){
         try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(secretKeyFactory);//"PBKDF2WithHmacSHA1"
            KeySpec spec = new PBEKeySpec(password.toCharArray(),"salt".getBytes(), 1024, 128);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey skeySpec = new SecretKeySpec(tmp.getEncoded(), algorithm.substring(0, 3));//"AES"

            // build the initialization vector.  This example is all zeros, but it 
            // could be any value or generated using a random number generator.
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            // initialize the cipher for encrypt mode
            Cipher cipher = Cipher.getInstance(algorithm.substring(0, algorithm.indexOf(" "))); //"AES/CBC/PKCS5Padding"
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivspec);

            // encrypt the message
            byte[] encrypted = cipher.doFinal(encryptedText.getBytes());

            encryptedText = Base64.encodeBase64String(encrypted);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException ex) {
            System.err.println(ex.getMessage());
        }
    }

}
