package org.trans;

/**
 * Created with IntelliJ IDEA.
 * User: liubingbing
 * Date: 12-7-16
 * Time: P.M 11:19
 */
public interface ResultCallBack {

    /**
     * handle the result for sender
     *
     * @param result the result from receiver
     */
    void handleResult(Result result);

}
