package main.java.message;

import java.io.Serializable;

public class FileListMessage implements Serializable {
    private String path ;

    public FileListMessage(String path) {
        this.path = path;
    }

    public FileListMessage() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "FileListMessage{" +
                "path='" + path + '\'' +
                '}';
    }
}
