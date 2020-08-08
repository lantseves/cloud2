package repository;

import repository.datasource.DataSource;
import repository.datasource.mysql.MySQLDataSource;
import repository.entity.File;
import repository.filesource.FileSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ServerRepository {
    private DataSource dataSource ;
    private FileSource fileSource ;

    public ServerRepository() {
        this.dataSource = new MySQLDataSource();
        this.fileSource = new FileSource();
    }

    public static void main(String[] args) {
        ServerRepository repository = new ServerRepository();

        Path path = Paths.get("./server/src/main/resources/users/demo") ;
        List<File> list = repository.dataSource.getFileListByParent(path.toString()) ;
        list.stream().forEach(System.out::println);

    }
}
