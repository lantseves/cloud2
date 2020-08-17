package main.java.message;

import java.io.Serializable;

public class CreateDirectoryMessage implements Serializable {
    private String currentPath;
    private String dirName ;

    public CreateDirectoryMessage() {
    }

    public CreateDirectoryMessage(String currentPath, String dirName) {
        this.currentPath = currentPath;
        this.dirName = dirName;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    @Override
    public String toString() {
        return "CreateDirectoryMessage{" +
                "currentPath='" + currentPath + '\'' +
                ", dirName='" + dirName + '\'' +
                '}';
    }
}
