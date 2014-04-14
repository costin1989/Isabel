package ro.ase.dis.server;

import javax.annotation.Resource;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.jms.*;

@Stateful
public class MessageSubjectSender {

    @Resource(mappedName = "jms/connectionFactory")
    private TopicConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/subjectQueue")
    Topic queue;

    public void sendMessage(String message) {
        TextMessage textMessage;
        try {
            try (TopicConnection connection = connectionFactory.createTopicConnection();
                    TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
                    TopicPublisher publisher = session.createPublisher(queue)) {
                textMessage = session.createTextMessage();
                textMessage.setText(message);
                publisher.publish(textMessage, DeliveryMode.PERSISTENT, 4, 30 *1000);
            }

        } catch (JMSException e) {
            System.err.println(e.getMessage());
        }
    }
}