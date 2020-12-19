package cn.edu.bjtu.ebosdevice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EbosDeviceApplicationTests {

    @Test
    void contextLoads() {
        int[] i = new int[100];
        for (int k=0;k<100;k++){
            i[k]=k;
        }
        for(int j=0;j<i.length;j++){
            System.out.println(i[j]);
        }
    }
}
