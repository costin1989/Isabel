/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.ase.dis.beans;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import org.apache.commons.codec.binary.Base64;
import ro.ase.dis.server.FinalReceiverSync;

/**
 *
 * @author costin1989
 */
@Named(value = "responsesBean")
@ViewScoped
@ManagedBean
public class ResponsesBean implements Serializable {

    private String responses;

    @EJB
    FinalReceiverSync finalReceiverSync;

    public ResponsesBean() {
    }

    public String getResponses() {
        return responses;
    }

    public void setResponses(String responses) {
        this.responses = responses;
    }

    public void updateResponses() {
        while (true) {
            TextMessage message = (TextMessage) finalReceiverSync.receiveMessage();
            if (message == null) {
                break;
            } else {
                try {
                    responses += message.getText();
                } catch (JMSException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }
}
