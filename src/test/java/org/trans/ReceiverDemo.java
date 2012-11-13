package org.trans;

import org.trans.DataPacket;
import org.trans.Receiver;
import org.trans.ReceiverCallBack;
import org.trans.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liubingbing
 * Date: 12-7-17
 * Time: A.M 10:36
 */
public class ReceiverDemo {

    public static void main(String args[]) throws IOException {

        new Receiver(9000, new ReceiverCallBack() {
            @Override
            public Result received(DataPacket dataPacket) {
                System.out.println(dataPacket.getTransType());
                System.out.println(dataPacket.getObjectFlag());
                System.out.println(dataPacket.getOperationType());
                System.out.println(dataPacket.getCount());
                List<Map> data = dataPacket.getData();
                for (Map<String, Object> map : data) {
                    System.out.println(map);
                }
                Result result = new Result();
                result.setSuccess(true);
                result.setObjectFlag(dataPacket.getObjectFlag());
                List<Object> list = new ArrayList<Object>();
                for (Map<String, Object> map : data) {
                    list.add(map.get("id"));
                }
                result.setIdList(list);
                return result;
            }
        });
    }

}
