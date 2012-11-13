package org.trans;

import java.util.ArrayList;
import java.util.List;

/**
 * the result of receiver handled, return to sender
 *
 * Created with IntelliJ IDEA.
 * User: liubingbing
 * Date: 12-7-16
 * Time: P.M 11:33
 */
public class Result extends Model {

    private static final long serialVersionUID = 2069381066821434310L;

    private boolean isSuccess;

    /* the flag of object, forExample: teacher, student,.... */
    private String objectFlag = "unknown";

    /* the operation type, only: add, edit, delete */
    private String operationType = "unknown";

    /* id collection of objects */
    private List<Object> idList = new ArrayList<Object>();

    /**
     * if handle success return true, else return false
     *
     * @return boolean
     */
    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    /**
     * the flag of object type, forExample: teacher or student or ...
     *
     * @return teacher/student/...
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
     * the id collection
     *
     * @return List&lt;Object&gt;
     */
    public List<Object> getIdList() {
        return idList;
    }

    public void setIdList(List<Object> idList) {
        this.idList = idList;
    }
}
