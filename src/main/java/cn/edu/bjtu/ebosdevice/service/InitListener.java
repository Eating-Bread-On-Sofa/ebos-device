package cn.edu.bjtu.ebosdevice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@Order(1)
public class InitListener implements ApplicationRunner {
    @Autowired
    MqFactory mqFactory;
    @Value("${mq}")
    private String name;

    @Override
    public void run(ApplicationArguments arguments){
        new Thread(() -> {
            MqConsumer mqConsumer = mqFactory.createConsumer("test");
            while (true) {
                try {
                    String msg = mqConsumer.subscribe();
                    System.out.println("收到：" + msg);
                }catch (Exception e){}
            }
        }).start();
    }
}
