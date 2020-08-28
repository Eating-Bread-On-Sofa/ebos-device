package cn.edu.bjtu.ebosdevice.service;

import cn.edu.bjtu.ebosdevice.controller.DeviceController;
import cn.edu.bjtu.ebosdevice.util.ApplicationContextProvider;

public class RawSubscribe implements Runnable{
    private String subTopic;

    private MqFactory mqFactory = ApplicationContextProvider.getBean(MqFactory.class);

    public RawSubscribe(String subTopic){
        this.subTopic = subTopic;
    }

    @Override
    public void run() {
        try{
            MqConsumer mqConsumer = mqFactory.createConsumer(subTopic);

            while (true){
                try {
                    String msg = mqConsumer.subscribe();
                    if(!DeviceController.check(subTopic)){
                        break;
                    }
                    System.out.println("收到"+subTopic+msg);
                }catch (Exception e){e.printStackTrace();break;}
            }
        }catch (Exception e){e.printStackTrace();}
    }

    public String getSubTopic() {
        return subTopic;
    }

    public void setSubTopic(String subTopic) {
        this.subTopic = subTopic;
    }

}
