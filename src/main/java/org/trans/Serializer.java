package org.trans;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: liubingbing
 * Date: 12-7-16
 * Time: P.M 11:39
 */
class Serializer {

    public static <T> byte[] serialize(T t) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
        outputStream.writeObject(t);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        outputStream.close();
        return bytes;
    }

    public static <T> T unSerialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        T t = (T) objectInputStream.readObject();
        objectInputStream.close();
        return t;
    }
}
