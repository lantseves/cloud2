package main.java.message;

import java.io.Serializable;

public class DownloadFileMessage implements Serializable {
    private String filePath ;

    public DownloadFileMessage(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "DownloadFileMessage{" +
                "filePath='" + filePath + '\'' +
                '}';
    }
}
