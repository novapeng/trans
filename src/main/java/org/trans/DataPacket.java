package org.trans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liubingbing
 * Date: 12-7-16
 * Time: P.M 11:25
 */
public class DataPacket extends Model {


    private static final long serialVersionUID = -2173385680389450554L;

    /* the value only : up or down */
    private String transType = "unknown";

    /* the flag of object, forExample: teacher, student,.... */
    private String objectFlag = "unknown";

    /* the operation type, only: add, edit, delete */
    private String operationType = "unknown";

    /* the count of objects in a packet */
    private long count;

    /* data collection */
    private List<Map> data = new ArrayList<Map>();

    /**
     * the trans type, up or down
     *
     * @return up/down
     */
    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    /**
     * flag of object type
     *
     * @return the object type, forExample: teacher or student or ...
     */
    public String getObjectFlag() {
        return objectFlag;
    }

    public void setObjectFlag(String objectFlag) {
        this.objectFlag = objectFlag;
    }

    /**
     * the operation type, create or update or delete
     *
     * @return create/update/delete
     */
    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    /**
     * the data's count
     *
     * @return long
     */
    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    /**
     * the data
     * @return List&lt;Map&gt;
     */
    public List<Map> getData() {
        return data;
    }

    public void setData(List<Map> data) {
        this.data = data;
    }
}
