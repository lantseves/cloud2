package main.java.response;

import java.io.Serializable;

public class ErrorResponse implements Serializable {
    private String msg ;

    public ErrorResponse(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "msg='" + msg + '\'' +
                '}';
    }
}
