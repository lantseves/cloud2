package repository.filesource;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSource {
    public final static String FILE_PATH = "./server/src/main/resources/users";

    public static void main(String[] args) {
        Path path = Paths.get("./server/src/main/resources/users");
        Path parent = path.getFileName();
        System.out.println(parent);
    }

}
