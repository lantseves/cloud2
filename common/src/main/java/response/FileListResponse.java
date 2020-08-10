package main.java.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FileListResponse implements Serializable {
    private List<String> fileList = new ArrayList<>() ;

    public FileListResponse() {
    }

    public List<String> getFileList() {
        return fileList;
    }

    @Override
    public String toString() {
        return "FileListResponse{" +
                "fileList=" + fileList +
                '}';
    }
}
