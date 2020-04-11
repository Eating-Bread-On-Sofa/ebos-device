package cn.edu.bjtu.ebosdevice.service.impl;

import cn.edu.bjtu.ebosdevice.service.MqConsumer;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.util.ByteSequence;

import javax.jms.*;

public class ActiveMqConsumerImpl implements MqConsumer {
    private MessageConsumer messageConsumer;
    private static ConnectionFactory connectionFactory = new ActiveMQConnectionFactory();

    public ActiveMqConsumerImpl(String topic) {
        try {
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(topic);
            MessageConsumer consumer = session.createConsumer(destination);
            this.messageConsumer = consumer;
        } catch (Exception e) {
        }
    }

    @Override
    public String subscribe() {
        try {
            Message message = messageConsumer.receive();
            if (message instanceof ActiveMQBytesMessage) {
                ActiveMQBytesMessage activeMQMessage = (ActiveMQBytesMessage) message;
                ByteSequence content = activeMQMessage.getContent();
                String msg = new String(content.getData());
                System.out.println("收到ActiveMQBytesMessage");
                return msg;
            } else if (message instanceof ActiveMQTextMessage) {
                ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage) message;
                System.out.println("收到ActiveMQTextMessage");
                return activeMQTextMessage.getText();
            } else if (message instanceof ActiveMQMapMessage) {
                ActiveMQMapMessage activeMQMapMessage = (ActiveMQMapMessage) message;
                String content = activeMQMapMessage.getContentMap().toString();
                System.out.println("收到ActiveMQMapMessage");
                return content;
            } else {
                System.out.println("收到" + message.getClass().toString());
                return "";
            }
        } catch (Exception e) {
            return "ActiveMQ接收出现异常";
        }
    }
}
