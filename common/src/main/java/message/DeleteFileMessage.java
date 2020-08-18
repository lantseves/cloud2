package main.java.message;

import java.io.Serializable;

public class DeleteFileMessage implements Serializable {
    String path ;

    public DeleteFileMessage(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "DeleteFileMessage{" +
                "path='" + path + '\'' +
                '}';
    }
}
