package org.trans;

import org.trans.DataPacket;
import org.trans.Result;
import org.trans.ResultCallBack;
import org.trans.Sender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liubingbing
 * Date: 12-7-17
 * Time: A.M 10:39
 */
public class SenderDemo {

    public static void main(String args[]) throws IOException, InterruptedException {

        //make the Sender is a single instance when connect a receiver
        Sender sender = Sender.connect("127.0.0.1", 9000, new ResultCallBack() {
            @Override
            public void handleResult(Result result) {
                System.out.println(result.isSuccess());
                System.out.println(result.getObjectFlag());
                System.out.println(result.getIdList());
            }
        });

        int k = 0;
        while (true) {
            // send dataPackets
            DataPacket dataPacket = new DataPacket();
            dataPacket.setTransType("up");
            dataPacket.setObjectFlag("teacher");
            List<Map> list = new ArrayList<Map>();
            for (int i = 0; i < 5000; i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", k + "-" + i);
                map.put("name", "老师" + k + "-" + i);
                list.add(map);
            }
            dataPacket.setData(list);
            sender.send(dataPacket);
            k++;
            Thread.sleep(2000);
        }
    }
}
