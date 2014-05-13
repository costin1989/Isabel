/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.ase.dis.beans;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.commons.codec.binary.Base64;
import org.primefaces.event.FlowEvent;

/**
 *
 * @author costin1989
 */
@Named(value = "hashBean")
@ViewScoped
@ManagedBean
public class HashBean implements Serializable {

    /**
     * Creates a new instance of TaskBean
     */
    private String hashAlgorithm;
    private String hashText;
    private String plainText;


    public HashBean() {
    }

    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    public void setHashAlgorithm(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    public String getHashText() {
        return hashText;
    }

    public void setHashText(String hashText) {
        this.hashText = hashText;
    }

    public String getPlainText() {
        return plainText;
    }

    public void setPlainText(String plainText) {
        this.plainText = plainText;
    }

   public void doHash(){
       
       try{
           MessageDigest md = MessageDigest.getInstance(hashAlgorithm);
           md.update(plainText.getBytes());
           byte[] digest = md.digest();
           hashText = Base64.encodeBase64String(digest);
       } catch(NoSuchAlgorithmException ex){
           System.out.println(ex.getMessage());
       }
   }
       
   
}
