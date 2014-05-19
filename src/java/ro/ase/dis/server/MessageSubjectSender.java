package ro.ase.dis.server;

import javax.annotation.Resource;
import javax.ejb.Stateful;
import javax.jms.*;
import ro.ase.dis.objects.MessageObject;

@Stateful
public class MessageSubjectSender {

    @Resource(mappedName = "jms/connectionFactory")
    private TopicConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/subjectQueue")
    Topic queue;

    public void sendMessage(MessageObject message) {
        ObjectMessage objectMessage;
        try {
            try (TopicConnection connection = connectionFactory.createTopicConnection();
                    TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
                    TopicPublisher publisher = session.createPublisher(queue)) {
                objectMessage = session.createObjectMessage(message);
                publisher.publish(objectMessage, DeliveryMode.PERSISTENT, 4, 30 *1000);
            }
        } catch (JMSException e) {
            System.err.println(e.getMessage());
        }
    }
}