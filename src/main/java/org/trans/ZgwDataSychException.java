package org.trans;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 12-7-17
 * Time: P.M 2:11
 */
public class ZgwDataSychException extends RuntimeException{

    public ZgwDataSychException(String message, Exception e) {
        super(message, e);
    }
}
