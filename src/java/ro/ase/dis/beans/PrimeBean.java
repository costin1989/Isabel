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
import java.math.BigInteger;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import ro.ase.dis.objects.PrimeMessageObject;
import ro.ase.dis.server.MessageSubjectSender;
import ro.ase.dis.server.MessageTaskSender;
import ro.ase.dis.server.PasswordReader;

/**
 *
 * @author costin1989
 */
@Named(value = "primeBean")
@ViewScoped
@ManagedBean
public class PrimeBean implements Serializable {

    /**
     * Creates a new instance of TaskBean
     */
    private String number;
    private static long taskCounter;
    private final int validationThreshold = 2;
    private final int bunchSize = 10;
    @EJB
    MessageTaskSender taskSender;
    @EJB
    MessageSubjectSender subjectSender;

    public PrimeBean() {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void reset() {
        this.number = "";
    }

    public void sendTask() {
        Timer t = new Timer();
        try {
            System.out.println("task started");

            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    PrimeMessageObject m = new PrimeMessageObject(number);
                    subjectSender.sendMessage(m);
                    System.out.println("Subject sent:" + m + " Time: " + new Date().getTime());
                }
            }, 1000 * 30, 1000 * 30);

            PrimeMessageObject m = new PrimeMessageObject(number);
            subjectSender.sendMessage(m);
            System.out.println("Subject sent:" + m + " Time: " + new Date().getTime());

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
            InputStream is = PasswordReader.class.getResourceAsStream("primes");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            BigInteger nr = new BigInteger(number);
            BigInteger sqrt = sqrt(nr);
            BigInteger primeBig;
            do {
                String prime = "";
                String task = "";
                for (int i = 0; i < bunchSize; i++) {
                    prime = br.readLine();
                     if (prime.equals("")) {
                         prime = "1";
                         continue;
                     }
                    task += prime+",";
                    taskCounter += 1;
                }
                primeBig = new BigInteger(prime);
                for (int k = 0; k < validationThreshold; k++) {
                        taskSender.sendMessage(prime);
                    }
            } while (primeBig.compareTo(sqrt) < 0);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private BigInteger sqrt(BigInteger n) {
        BigInteger a = BigInteger.ONE;
        BigInteger b = new BigInteger(n.shiftRight(5).add(new BigInteger("8")).toString());
        while (b.compareTo(a) >= 0) {
            BigInteger mid = new BigInteger(a.add(b).shiftRight(1).toString());
            if (mid.multiply(mid).compareTo(n) > 0) {
                b = mid.subtract(BigInteger.ONE);
            } else {
                a = mid.add(BigInteger.ONE);
            }
        }
        return a.subtract(BigInteger.ONE);
    }

}
