/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ro.ase.dis.objects;

/**
 *
 * @author costin1989
 */
public class EncryptionMessageObject implements MessageObject{
    private String encryptedText;
    private String algorithm;
    private String secretKeyFactory;
    private String secretKeySpecification;
    private String salt;
    private int keyLength;

    public EncryptionMessageObject() {
    }

    public EncryptionMessageObject(String encryptedText, String algorithm, String salt, String secretKeyFactory, String secretKeySpecification, int keyLength) {
        this.encryptedText = encryptedText;
        this.algorithm = algorithm;
        this.secretKeyFactory = secretKeyFactory;
        this.secretKeySpecification = secretKeySpecification;
        this.keyLength = keyLength;
        this.salt = salt;
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

    public String getSecretKeySpecification() {
        return secretKeySpecification;
    }

    public void setSecretKeySpecification(String secretKeySpecification) {
        this.secretKeySpecification = secretKeySpecification;
    }

    public int getKeyLength() {
        return keyLength;
    }

    public void setKeyLength(int keyLength) {
        this.keyLength = keyLength;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public String toString() {
        return "EncryptionMessageObject{" + "encryptedText=" + encryptedText + ", algorithm=" + algorithm + ", secretKeyFactory=" + secretKeyFactory + ", secretKeySpecification=" + secretKeySpecification + ", keyLength=" + keyLength + '}';
    }
    
    
}
