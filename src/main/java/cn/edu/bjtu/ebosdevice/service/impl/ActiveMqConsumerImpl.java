package cn.edu.bjtu.ebosdevice.service.impl;

import cn.edu.bjtu.ebosdevice.service.MqConsumer;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.util.ByteSequence;

import javax.jms.*;

public class ActiveMqConsumerImpl implements MqConsumer {
    private MessageConsumer messageConsumer;
    private static ConnectionFactory connectionFactory = new ActiveMQConnectionFactory();

    public ActiveMqConsumerImpl(String topic){
        try {
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(topic);
            MessageConsumer consumer = session.createConsumer(destination);
            this.messageConsumer = consumer;
        }catch (Exception e){}
    }

    @Override
    public String subscribe(){
        try {
            ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage) messageConsumer.receive();
            return activeMQTextMessage.getText();
        }catch (Exception e){
            try {
                ActiveMQBytesMessage activeMQMessage = (ActiveMQBytesMessage) messageConsumer.receive();
                ByteSequence content = activeMQMessage.getContent();
                String msg = new String(content.getData());
                return msg;
            }catch (Exception e1){e1.printStackTrace();return "收到的消息类型不支持";}
        }
    }
}
