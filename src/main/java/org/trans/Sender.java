package org.trans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * send the dataPackets to receiver and get result
 * <br/>
 * the Example :
 * <p/>
 * <pre>
 *      Sender sender = new Sender("127.0.0.1", 9000, new ResultCallBack() {
 *           &#064;Override
 *           public void handleResult(Result result) {
 *               System.out.println(result.isSuccess());
 *               System.out.println(result.getObjectFlag());
 *               //handle the result...
 *           }
 *       });
 *
 *        // send dataPackets
 *        DataPacket dataPacket = new DataPacket();
 *        dataPacket.setTransType("up");
 *        //dataPacket.set...
 *        List<Map> list = new ArrayList<Map>();
 *        Map<String, Object> map = new HashMap<String, Object>();
 *        map.put("id", 1);
 *        map.put("name", "ivan");
 *        list.add(map);
 *        dataPacket.setData(list);
 *        sender.send(dataPacket);
 *
 * </pre>
 * <p/>
 * Created with IntelliJ IDEA.
 * User: liubingbing
 * Date: 12-7-16
 * Time: P.M 8:52
 */
public class Sender {

    private static final Log log = LogFactory.getLog(Sender.class.getName());

    /* length of dataPacket Queue */
    private static final int DEFAULT_BUFFER_SIZE = 10000;

    /* default reconnect time, value is 3 second */
    private static final int DEFAULT_TIME_OUT = 3000;

    /* socket outputStream */
    private static DataOutputStream out;

    /* socket inputStream */
    private static DataInputStream in;

    /* the address of receiver */
    private String ip;

    /* the port of receiver */
    private int port;

    /* dataPacket Queue */
    private BlockingQueue<DataPacket> blockingQueue;

    /* result handler */
    private ResultCallBack resultCallBack;

    private static Map<Integer, Sender> senderPool = new HashMap<Integer, Sender>();

    private Socket socket;

    /**
     * create a sender to connect receiver
     *
     * @param ip             the address of receiver
     * @param port           the port of receiver
     * @param bufferSize     length of dataPacket queue
     * @param resultCallBack result handler
     * @throws SocketConnectException if can not connect the receiver, will throw this exception
     */
    private Sender(String ip, int port, int bufferSize, ResultCallBack resultCallBack) throws SocketConnectException {
        try {
            log.info("to connect receiver...");
            socket = new Socket(ip, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            log.error("connect the receiver error!", e);
            throw new SocketConnectException("connect the receiver error!", e);
        }
        this.ip = ip;
        this.port = port;
        this.blockingQueue = new LinkedBlockingQueue<DataPacket>(bufferSize);
        this.resultCallBack = resultCallBack;
        new ReadThread().start();
        new WriteThread().start();
    }

    /**
     * connect the Receiver, if connection refused, will reconnect.
     *
     * @param ip             the receiver address
     * @param port           the receiver port
     * @param timeout       reconnect time
     * @param bufferSize     size of dataPacket Queue
     * @param resultCallBack result handler
     * @return instance of Sender
     */
    public static Sender connect(String ip, int port, int timeout, int bufferSize, ResultCallBack resultCallBack) {
        Sender sender = getSenderFromPool(ip, port);
        if (sender != null) return sender;
        try {
            sender = new Sender(ip, port, bufferSize, resultCallBack);
        } catch (SocketConnectException e) {
            log.info("reconnect receiver...");
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e1) {
                //do nothing
            }
            sender = connect(ip, port, bufferSize, resultCallBack);
        }
        putSenderPool(sender);
        return sender;
    }

    /**
     * connect the Receiver, if connection refused, will reconnect.
     *
     * @param ip             the receiver address
     * @param port           the receiver port
     * @param bufferSize     size of dataPacket Queue
     * @param resultCallBack result handler
     * @return instance of Sender
     */
    public static Sender connect(String ip, int port, int bufferSize, ResultCallBack resultCallBack) {
        return connect(ip, port, DEFAULT_TIME_OUT, bufferSize, resultCallBack);
    }

    private static void putSenderPool(Sender sender) {
        senderPool.put((sender.ip + "-" + sender.port).hashCode(), sender);
    }

    private static Sender getSenderFromPool(String ip, int port) {
        return senderPool.get((ip + "-" + port).hashCode());
    }

    /**
     * connect the Receiver, if connection refused, will reconnect..
     *
     * @param ip             the receiver address
     * @param port           the receiver port
     * @param resultCallBack result handler
     * @return instance of Sender
     */
    public static Sender connect(String ip, int port, ResultCallBack resultCallBack) {
        return connect(ip, port, DEFAULT_BUFFER_SIZE, resultCallBack);
    }

    /**
     * send the dataPacket to receiver
     *
     * @param dataPacket data packet
     */
    public void send(DataPacket dataPacket) {
        try {
            blockingQueue.put(dataPacket);
        } catch (InterruptedException e) {
            log.error("the queue is unavailable for send dataPacket!", e);
            throw new IllegalStateException("the queue is unavailable for send dataPacket!", e);
        }
    }

    private class ReadThread extends Thread {

        public void run() {
            while (true) {
                try {
                    ObjectInputStream objectInputStream = new ObjectInputStream(in);
                    resultCallBack.handleResult((Result) objectInputStream.readObject());
                } catch (SocketException e) {
                    log.error("receiver may be shutdown, sender socket io will to be closed!", e);
                    try {
                        in.close();
                        out.close();
                    }catch (IOException e1) {
                        log.error("close sender socket io exception!", e1);
                    }
                        log.info("reconnect receiver...");
                    try {
                        socket = new Socket(ip, port);
                        in = new DataInputStream(socket.getInputStream());
                        out = new DataOutputStream(socket.getOutputStream());
                    } catch (IOException e1) {
                        log.error("connect receiver failed!", e1);
                    }
                } catch (Exception e) {
                    log.error("receive the result error!", e);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    //do nothing
                }
            }
        }
    }

    private class WriteThread extends Thread {

        public void run() {
            while (true) {
                try {
                    DataPacket dataPacket = blockingQueue.take();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
                    objectOutputStream.writeObject(dataPacket);
                    objectOutputStream.flush();
                } catch (Exception e) {
                    log.error(String.format("send dataPacket to receiver error!"), e);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    //do nothing
                }
            }
        }
    }

}
