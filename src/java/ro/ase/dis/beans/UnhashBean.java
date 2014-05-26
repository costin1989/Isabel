/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.ase.dis.beans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import ro.ase.dis.objects.HashMessageObject;
import ro.ase.dis.server.MessageSubjectSender;
import ro.ase.dis.server.MessageTaskSender;
import ro.ase.dis.server.PasswordReader;

/**
 *
 * @author costin1989
 */
@Named(value = "unhashBean")
@ViewScoped
@ManagedBean
public class UnhashBean implements Serializable {

    /**
     * Creates a new instance of DecryptionBean
     */
    private String hashAlgorithm;
    private String hashText;

    private final int validationThreshold = 2;
    private final int bunchSize = 10;
    private static long taskCounter;

    @EJB
    MessageTaskSender taskSender;
    @EJB
    MessageSubjectSender subjectSender;

    public UnhashBean() {
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

    public void reset() {
        this.hashText = "";
        this.hashAlgorithm = "";
    }

    public void sendTask() {
        Timer t = new Timer();
        try {
            System.out.println("task started");

            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    HashMessageObject m = new HashMessageObject(hashText, hashAlgorithm);
                    subjectSender.sendMessage(m);
                }
            }, 1000 * 30, 1000 * 30);

            HashMessageObject m = new HashMessageObject(hashText, hashAlgorithm);
            subjectSender.sendMessage(m);

            taskSender.connect();

            dictionaryTaskSender();
            taskSender.disconnect();
            t.cancel();
            System.out.println("task ended");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            t.cancel();
        }
    }

    private void dictionaryTaskSender() {
        try {
            InputStream is = PasswordReader.class.getResourceAsStream("dict");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            for (int i = 0; i < 50000; i++) {
                String word = "";
                for (int j = 0; j < bunchSize; j++) {
                    word += br.readLine()+",";
                    taskCounter += 1;
                }
                for (int k = 0; k < validationThreshold; k++) {
                    taskSender.sendMessage(word);
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
