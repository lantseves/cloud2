package main.java.entity;

import javafx.scene.image.ImageView;

import java.io.Serializable;
import java.nio.file.Path;

public class FileResponse implements Serializable {
    private String path ;
    private String fileName ;
    private String dateMod ;
    private String fileSize ;
    private boolean isDirectory ;

    public FileResponse() {
    }

    public String  getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDateMod() {
        return dateMod;
    }

    public void setDateMod(String dateMod) {
        this.dateMod = dateMod;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    @Override
    public String toString() {
        return "FileResponse{" +
                "path=" + path +
                ", fileName='" + fileName + '\'' +
                ", dateMod='" + dateMod + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", isDirectory=" + isDirectory +
                '}';
    }
}
