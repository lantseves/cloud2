import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileView {
    private Path path ;
    private String fileName ;
    private String dateMod ;
    private String fileSize ;
    private ImageView fileImage ;
    private boolean isDirectory ;

    public FileView() {
    }

    public FileView(Path path) {
        initFileView(path);
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
       this.path = path ;
    }


    public String getFileName() {
        return fileName;
    }


    public String getDateMod() {
        return dateMod;
    }


    public String getFileSize() {
        return fileSize;
    }


    public ImageView getFileImage() {
        return fileImage;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    @Override
    public String toString() {
        return "FileView{" +
                "path=" + path +
                ", fileName='" + fileName + '\'' +
                ", dateMod='" + dateMod + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", fileImage=" + fileImage +
                ", isDirectory=" + isDirectory +
                '}';
    }

    private void initFileView(Path path) {
        try {
            this.path = path ;
            this.fileName = path.getFileName().toString();
            this.isDirectory = path.toFile().isDirectory();
            this.dateMod = Files.getLastModifiedTime(path).toString() ;
            if (this.isDirectory) {
                fileImage = new ImageView(new Image(
                        Paths.get("./client/src/main/resources/directory.png")
                                .toFile()
                                .toURI()
                                .toString()));
            } else {
                fileImage = new ImageView(new Image(
                        Paths.get("./client/src/main/resources/file.png")
                                .toFile()
                                .toURI()
                                .toString()));
                this.fileSize = String.valueOf(Files.size(path) / 1024);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Сетеры для заполнения параметрами с сервера

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setDateMod(String dateMod) {
        this.dateMod = dateMod;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public void setFileImage(ImageView fileImage) {
        this.fileImage = fileImage;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }
}
