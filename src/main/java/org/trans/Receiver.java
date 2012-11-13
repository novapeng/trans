package org.trans;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * receive the dataPacket from sender, and return the result to sender
 * <br/>
 * <p/>
 * the Example:
 * <p/>
 * <pre>
 *          new Receiver(new ReceiverCallBack() {
 *
 *               &#064;Override
 *               public Result received(DataPacket dataPacket) {
 *                   //handle the dataPacket
 *                   //...
 *                   Result result =  new Result();
 *                   result.setSuccess(true/false);
 *                   //....
 *                   return result;
 *               }
 *           });
 *
 * </pre>
 * <p/>
 * Created with IntelliJ IDEA.
 * User: liubingbing
 * Date: 12-7-16
 * Time: P.M 8:49
 */
public class Receiver {

    private static final Log log = LogFactory.getLog(Receiver.class.getName());

    /* the default port */
    private final static int DEFAULT_PORT = 9000;

    /* default result queue length */
    private final static int DEFAULT_BUFFER_SIZE = 10000;

    /* result queue */
    private BlockingQueue<Result> blockQueue;

    /* socket outputStream */
    private static DataOutputStream out;

    /* socket inputStream */
    private static DataInputStream in;

    /* dataPacket handler */
    private ReceiverCallBack receiverCallBack;

    /* server socket */
    private ServerSocket serverSocket;

    /**
     * start a server socket to receive the dataPacket
     *
     * @param receiverCallBack dataPacket handler
     */
    public Receiver(ReceiverCallBack receiverCallBack) {
        this(DEFAULT_PORT, DEFAULT_BUFFER_SIZE, receiverCallBack);
    }

    /**
     * start a server socket to receive the dataPacket
     *
     * @param port server socket port
     * @param receiverCallBack dataPacket handler
     */
    public Receiver(int port, ReceiverCallBack receiverCallBack) {
        this(port, DEFAULT_BUFFER_SIZE, receiverCallBack);
    }

    /**
     * start a server socket to receive the dataPacket
     *
     * @param port server socket port
     * @param bufferSize length of result queue
     * @param receiverCallBack dataPacket handler
     */
    public Receiver(int port, int bufferSize, ReceiverCallBack receiverCallBack) {
        try {
            log.info(String.format("start receiver"));
            serverSocket = new ServerSocket(port);
            Socket socket = serverSocket.accept();
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            log.error("start receiver error!", e);
            throw new IllegalStateException("start receiver error!", e);
        }
        this.blockQueue = new LinkedBlockingQueue<Result>(bufferSize);
        this.receiverCallBack = receiverCallBack;

        new Monitor().start();
        new WriteThread().start();
        new ReadThread().start();
    }

    private class Monitor extends Thread {
        public void run() {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    log.info(String.format("accept sender socket!"));
                    in = new DataInputStream(socket.getInputStream());
                    out = new DataOutputStream(socket.getOutputStream());
                } catch (IOException e) {
                    log.error(String.format("accept sender socket io exception!"), e);
                }
            }
        }
    }

    private class WriteThread extends Thread {

        public void run() {
            while (true) {
                try {
                    Result result = blockQueue.take();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
                    objectOutputStream.writeObject(result);
                    objectOutputStream.flush();
                } catch (Exception e) {
                    log.error(String.format("send result to sender error!"), e);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        //do nothing
                    }
                }
            }
        }
    }

    private class ReadThread extends Thread {

        public void run() {
            while (true) {
                try {
                    ObjectInputStream objectInputStream = new ObjectInputStream(in);
                    Object o = objectInputStream.readObject();
                    blockQueue.put(receiverCallBack.received((DataPacket) o));
                } catch (SocketException e) {
                    log.error("sender may be shutdown, receiver socket io will to be closed!", e);
                    try {
                        in.close();
                        out.close();
                    } catch (IOException e1) {
                        log.error("close socket io exception!", e1);
                    }

                } catch (Exception e) {
                    log.error("receive the dataPacket error!", e);
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
