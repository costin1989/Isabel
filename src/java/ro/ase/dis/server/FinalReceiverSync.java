package ro.ase.dis.server;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import javax.annotation.Resource;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;

/**
 *
 * @author costin1989
 */
@Stateless
public class FinalReceiverSync  {
    @Resource(mappedName = "jms/connectionFactory")
    private ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/finalQueue")
    private Queue finalQueue;
    
       
    public Message receiveMessage() {
        try {
            try (Connection connection = connectionFactory.createConnection();
                    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                    MessageConsumer consumer = session.createConsumer(finalQueue)) {
                connection.start();
                return consumer.receive(1000);
            }
        } catch (JMSException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

   
}
