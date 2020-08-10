package main.java.response;

import java.io.Serializable;

public class FilePartResponse  implements Serializable {
    private int numberPart ;
    private int countParts;
    private String path;
    private boolean result;

    public FilePartResponse() {
    }

    public FilePartResponse(int numberPart, int countParts, String path, boolean result) {
        this.numberPart = numberPart;
        this.countParts = countParts;
        this.path = path;
        this.result = result;
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

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "FilePartResponse{" +
                "numberPart=" + numberPart +
                ", countParts=" + countParts +
                ", path='" + path + '\'' +
                ", result=" + result +
                '}';
    }
}
