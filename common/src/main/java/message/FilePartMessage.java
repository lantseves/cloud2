package main.java.message;

import java.io.Serializable;
import java.util.Arrays;

public class FilePartMessage implements Serializable {
    private int numberPart ;
    private int countParts;
    private String path;
    private byte[] fileContent;

    public FilePartMessage() {
    }

    public FilePartMessage(int numberPart, int countParts, String path, byte[] fileContent) {
        this.numberPart = numberPart;
        this.countParts = countParts;
        this.path = path;
        this.fileContent = fileContent;
    }

    public int getNumberPart() {
        return numberPart;
    }

    public void setNumberPart(int numberPart) {
        this.numberPart = numberPart;
    }

    public int getCountParts() {
        return countParts;
    }

    public void setCountParts(int countParts) {
        this.countParts = countParts;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }

    @Override
    public String toString() {
        return "FilePartMessage{" +
                "numberPart=" + numberPart +
                ", countParts=" + countParts +
                ", path='" + path + '\'' +
                ", fileContent=" + Arrays.toString(fileContent) +
                '}';
    }
}
