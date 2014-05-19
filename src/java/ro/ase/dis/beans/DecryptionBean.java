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
import ro.ase.dis.objects.EncryptionMessageObject;
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
    private String secretKeySpecification;
    private int keyLength;
    private final String salt = "salt";
    private long passwords;
    private boolean defaultPasswordList;
    private boolean customPasswords;
    private final int defaultPasswords = 2151221;
    private Integer progress;
    private boolean bruteForce = true;
    private String bruteForceLength;
    private final int validationThreshold = 2;
    private final int bunchSize = 10;

    private enum TaskTypes {

        BRUTE_FORCE, DICTIONARY, CUSTOM_LIST
    };
    private TaskTypes taskType = TaskTypes.BRUTE_FORCE;
    static char[] alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJDKMNOPQRSTUVWXYZ0123456789".toCharArray();

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
        setCustomPasswordsCount();
    }

    private void setCustomPasswordsCount() {
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
            this.bruteForce = false;
            this.customPasswords = false;
        }
        if (defaultPasswordList) {
            this.passwords = this.defaultPasswords;
        }
        this.taskType = TaskTypes.DICTIONARY;
    }

    public int getDefaultPasswords() {
        return defaultPasswords;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
        this.keyLength = Integer.parseInt(algorithm.substring(algorithm.indexOf("(")+1, algorithm.indexOf(")")));
        this.secretKeySpecification = algorithm.substring(0, 3);
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
        if (bruteForce) {
            this.defaultPasswordList = false;
            this.customPasswords = false;
            setBruteForcePasswordsCount();
        }
        this.taskType = TaskTypes.BRUTE_FORCE;
    }

    private void setBruteForcePasswordsCount() throws NumberFormatException {
        this.passwords = this.bruteForceLength == null ? 0 : (long) Math.pow(alphabet.length, Integer.parseInt(this.bruteForceLength));
    }

    public String getBruteForceLength() {
        return bruteForceLength;
    }

    public void setBruteForceLength(String bruteForceLength) {
        this.bruteForceLength = bruteForceLength;
        setBruteForcePasswordsCount();
    }

    public boolean isCustomPasswords() {
        return customPasswords;
    }

    public void setCustomPasswords(boolean customPasswords) {
        this.customPasswords = customPasswords;
        if (customPasswords) {
            this.defaultPasswordList = false;
            this.bruteForce = false;
            setCustomPasswordsCount();
        }
        this.taskType = TaskTypes.CUSTOM_LIST;
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
            setCustomPasswordsCount();
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
                    EncryptionMessageObject m = new EncryptionMessageObject(encryptedText, algorithm, salt, secretKeyFactory, secretKeySpecification, keyLength);
                    subjectSender.sendMessage(m);
                    System.out.println("Subject sent:" + m + " Time: " + new Date().getTime());
                }
            }, 1000 * 30, 1000 * 30);

            EncryptionMessageObject m = new EncryptionMessageObject(encryptedText, algorithm, salt, secretKeyFactory, secretKeySpecification, keyLength);
            subjectSender.sendMessage(m);
            System.out.println("Subject sent:" + m + " Time: " + new Date().getTime());

            taskSender.connect();
            switch (taskType) {
                case DICTIONARY:
                    dictionaryTaskSender();
                    break;
                case BRUTE_FORCE:
                    bruteForceTaskSender(Integer.parseInt(bruteForceLength));
                    break;
                case CUSTOM_LIST:
                    customListTaskSender();
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
                passwordCounter += 1;
            }
            message += PasswordReader.getInstance().readPassword();
            passwordCounter += 1;
            for (int k = 0; k < validationThreshold; k++) {
                taskSender.sendMessage(message);
            }
        }
    }

    private void customListTaskSender() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void bruteForceTaskSender(int maxPasswordsLength) {
        if (maxPasswordsLength < 1) {
            throw new IllegalArgumentException();
        }
        createWords("", maxPasswordsLength);
    }

    private void createWords(String base, int maxPasswordsLength) {
        for (char c : alphabet) {
            if (maxPasswordsLength == 1) {
                taskSender.sendMessage(base + c);
                passwordCounter += 1;
                progress = Long.valueOf(passwordCounter).intValue()/Long.valueOf(passwords).intValue()*100;
            } else {
                createWords(base + c, maxPasswordsLength - 1);
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
