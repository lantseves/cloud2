package main.java.response;

import main.java.entity.FileResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FileListResponse implements Serializable {
    private List<FileResponse> fileList = new ArrayList<>() ;

    public FileListResponse() {
    }

    public List<FileResponse> getFileList() {
        return fileList;
    }

    @Override
    public String toString() {
        return "FileListResponse{" +
                "fileList=" + fileList +
                '}';
    }
}
