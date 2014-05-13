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
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.FileUploadEvent;
import ro.ase.dis.server.EncryptFile;
import ro.ase.dis.server.MessageSubjectSender;
import ro.ase.dis.server.MessageTaskSender;
import ro.ase.dis.server.PasswordReader;

/**
 *
 * @author costin1989
 */
@Named(value = "decryptionBean")
@ViewScoped
@ManagedBean
public class DecryptionBean implements Serializable {

    /**
     * Creates a new instance of DecryptionBean
     */
    private String encryptedText;
    private String passwordList;
    private String algorithm;
    private String secretKeyFactory;
    private long passwords;
    private boolean defaultPasswordList;
    private final int defaultPasswords = 2151221;
    private Integer progress;
    private boolean bruteForce = true;
    private String bruteForceLength;
    private final int validationThreshold = 2;
    private final int bunchSize = 10;
    private final int maxPasswordsLength = 2;

    private enum taskTypes {

        BRUTE_FORCE, DICTIONARY
    };
    private final taskTypes taskType = taskTypes.BRUTE_FORCE;

    @EJB
    MessageTaskSender taskSender;
    @EJB
    MessageSubjectSender subjectSender;
    private static long passwordCounter;

    public DecryptionBean() {
    }

    public String getEncryptedText() {
        return encryptedText;
    }

    public void setEncryptedText(String encryptedText) {
        this.encryptedText = encryptedText;
    }

    public String getPasswordList() {
        return passwordList;
    }

    public void setPasswordList(String passwordList) {
        this.passwordList = passwordList;
        setPasswordsCount();
    }

    private void setPasswordsCount() {
        this.passwords = (this.passwordList == null || "".equals(this.passwordList)) ? 0 : this.passwordList.split("\r\n").length;
    }

    public long getPasswords() {
        return passwords;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public boolean isDefaultPasswordList() {
        return defaultPasswordList;
    }

    public void setDefaultPasswordList(boolean defaultPasswordList) {
        this.defaultPasswordList = defaultPasswordList;
        if (defaultPasswordList) {
            this.passwords = this.defaultPasswords;
        } else {
            setPasswordsCount();
        }
    }

    public int getDefaultPasswords() {
        return defaultPasswords;
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

    public boolean isBruteForce() {
        return bruteForce;
    }

    public void setBruteForce(boolean bruteForce) {
        this.bruteForce = bruteForce;
    }

    public String getBruteForceLength() {
        return bruteForceLength;
    }

    public void setBruteForceLength(String bruteForceLength) {
        this.bruteForceLength = bruteForceLength;
        this.passwords = (long) Math.pow(26, Integer.parseInt(this.bruteForceLength));
    }

    public void encryptedTextFileUploadListener(FileUploadEvent event) {
        try {
            StringBuilder sb = readFileUploadEvent(event);
            this.encryptedText = sb.toString();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void passwordListFileUploadListener(FileUploadEvent event) {
        try {
            StringBuilder sb = readFileUploadEvent(event);
            this.passwordList = sb.toString();
            setPasswordsCount();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private StringBuilder readFileUploadEvent(FileUploadEvent event) throws IOException {
        InputStream fis = event.getFile().getInputstream();
        BufferedReader in = new BufferedReader(new InputStreamReader(fis));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = in.readLine()) != null) {
            sb.append(line);
            sb.append("\r\n");
        }
        return sb;
    }

    public void resetConfigPanel() {
        this.algorithm = null;
        this.encryptedText = null;
        this.passwordList = null;
        this.passwords = 0;
        this.defaultPasswordList = true;
        this.secretKeyFactory = null;
    }

    public void sendTask() {
        Timer t = new Timer();
        try {
            System.out.println("task started");

            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    String encryptMessage = EncryptFile.getEncryptFile();
                    subjectSender.sendMessage(encryptMessage);
                    System.out.println("Subject sent:" + encryptMessage + " Time: " + new Date().getTime());
                }
            }, 1000 * 30, 1000 * 30);

            String encryptMessage = EncryptFile.getEncryptFile();
            subjectSender.sendMessage(encryptMessage);
            System.out.println("Subject sent:" + encryptMessage + " Time: " + new Date().getTime());

            taskSender.connect();
            switch (taskType) {
                case DICTIONARY:
                    dictionaryTaskSender();
                    break;
                case BRUTE_FORCE:
                    bruteForceTaskSender(maxPasswordsLength);
                    break;
            }

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
        for (int i = 0; i < 500; i++) {
            String message = "";
            for (int j = 0; j < bunchSize; j++) {
                message += PasswordReader.getInstance().readPassword() + ",";
            }
            message += PasswordReader.getInstance().readPassword();
            for (int k = 0; k < validationThreshold; k++) {
                taskSender.sendMessage(message);
            }

            passwordCounter += validationThreshold * bunchSize;
        }
    }

    private void bruteForceTaskSender(int maxPasswordsLength)  {
        if(maxPasswordsLength < 1){
            throw new IllegalArgumentException();
        }
        createWords("", maxPasswordsLength);
    }

    private void createWords(String base, int maxPasswordsLength) {
        for(char c='a';c<='z';c++){
            if(maxPasswordsLength == 1){
                taskSender.sendMessage(base+c);
                passwordCounter += 1;
            } else {
                createWords(base+c, maxPasswordsLength-1);
            }
        }
    }

    public Integer getProgress() {
        if (progress == null) {
            progress = 0;
        }
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public void onComplete() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Progress Completed", "Progress Completed"));
    }

}
