package repository.filesource;

import io.netty.channel.socket.SocketChannel;

import java.io.IOException;
import java.nio.file.*;

// Управляет файлами на сервере
public class FileSource {

    public boolean writeFilePart(Path filePath, byte[] fileContent, boolean appended) {
        try {
            if (Files.notExists(filePath)) {
                Files.createDirectories(filePath.getParent()) ;
            }
            if (!appended) {
                Files.write(filePath, fileContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } else {
                Files.write(filePath, fileContent, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true ;
    }

    public void readFilePart(Path filePath, SocketChannel channel) {
        //TODO реализовать отправку файла частями
    }
}
