package repository.filesource;

import io.netty.channel.socket.SocketChannel;
import main.java.message.FilePartMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;

// Управляет файлами на сервере
public class FileSource {

    //Записывает файлы
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

    //Читает файлы с сервера
    public boolean readFilePart(Path filePath, SocketChannel channel) {
            try(FileInputStream fis = new FileInputStream(filePath.toFile())) {
                int countParts = (int)Math.ceil(fis.available() / 1024f) ;
                for (int i = 1 ; fis.available() > 0 ; i++) {
                    byte[] buffer = new byte[1024] ;
                    FilePartMessage filePart = new FilePartMessage() ;
                    filePart.setNumberPart(i);
                    filePart.setCountParts(countParts);
                    filePart.setPath(filePath.getFileName().toString());
                    fis.read(buffer);
                    filePart.setFileContent(buffer);
                    channel.writeAndFlush(filePart) ;
                }
            }catch (Exception e) {
                e.printStackTrace();
                return false ;
            }
            return true ;
    }

    //Создает каталог
    public boolean createDirectory(Path dir) {
        try {
            Files.createDirectories(dir) ;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true ;
    }

    //Удалить файл
    public boolean deleteFile(Path filePath) {
        try {
            if (Files.isDirectory(filePath)) {
                // Скопировал данный код, но не понял как он удаляет вложенные файлы
                Files.walk(filePath)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } else {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false ;
        }
        return true ;
    }
}
