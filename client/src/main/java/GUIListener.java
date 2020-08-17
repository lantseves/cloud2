import main.java.entity.FileResponse;

import java.nio.file.Path;
import java.util.List;

public interface GUIListener {

    //Изменилась текущая папка
    void onChangeCurrentPath(Path currentPath) ;
    //Изменился текущий каталог сервера
    void onChangeCurrentPathServer(Path currentPath) ;

    //авторизация прошла успешно
    void OnAuthresultOk();

    //Обновился список файлов в текущем каталоге
    void onChangeFileListCurrentPath(Path currentPath);

    //Получен список файлов от сервера
    void onChangeFileListCurrentPathServer(List<FileResponse> list);




}
