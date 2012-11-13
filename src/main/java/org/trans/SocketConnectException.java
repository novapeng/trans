package org.trans;

/**
 * Created with IntelliJ IDEA.
 * User: liubingbing
 * Date: 12-7-17
 * Time: P.M 2:10
 */
class SocketConnectException extends Exception {

    public SocketConnectException(String message, Exception e) {
        super(message, e);
    }
}
