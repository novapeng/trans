package org.trans;

/**
 * Created with IntelliJ IDEA.
 * User: liubingbing
 * Date: 12-7-16
 * Time: P.M 11:05
 */
public interface ReceiverCallBack {

    /**
     * handle the data from sender, and return the result to the sender
     *
     * @param data the data from sender
     * @return the data return to sender
     */
    Result received(DataPacket data);

}
