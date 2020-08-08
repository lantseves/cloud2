package main.java;

import java.nio.file.Path;
import java.util.Arrays;

public class FilePartMessage extends AbstractMessage {
    private int numberPart ;
    private int countParts;
    private Path path;
    private int pathLength ;
    private byte[] fileContent;
    private int fileContentLength;

    public FilePartMessage() {
    }

    public FilePartMessage(int numberPart, int countParts, Path path, byte[] fileContent) {
        this.numberPart = numberPart;
        this.countParts = countParts;
        this.path = path;
        this.pathLength = path.toString().length();
        this.fileContent = fileContent;
        this.fileContentLength = fileContent.length;
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

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
        this.pathLength = path.toString().length();
    }

    public int getPathLength() {
        return pathLength;
    }


    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
        this.fileContentLength = fileContent.length;
    }

    public int getFileContentLength() {
        return fileContentLength;
    }

    @Override
    public String toString() {
        return "FilePartMessage{" +
                "numberPart=" + numberPart +
                ", countParts=" + countParts +
                ", path=" + path +
                ", pathLength=" + pathLength +
                ", fileContent=" + Arrays.toString(fileContent) +
                ", fileContentLength=" + fileContentLength +
                '}';
    }
}
