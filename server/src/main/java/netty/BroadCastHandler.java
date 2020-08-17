package netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import main.java.message.*;
import main.java.response.AuthorizationResponse;
import main.java.response.ErrorResponse;
import main.java.response.FileListResponse;
import main.java.response.FilePartResponse;
import repository.ServerRepository;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class BroadCastHandler extends ChannelInboundHandlerAdapter {
    private static ConcurrentMap<SocketChannel , String> channels = new ConcurrentHashMap<>();
    private static ServerRepository serverRepository = new ServerRepository() ;

    @Override
    public void channelInactive(ChannelHandlerContext ctx)  {
        channels.remove((SocketChannel)ctx.channel()) ;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        SocketChannel channel = (SocketChannel) ctx.channel();
        if (channels.containsKey(channel)) {
            if(msg instanceof FilePartMessage) {
                FilePartMessage fileMsg = (FilePartMessage) msg;
                readFilePartMessage(fileMsg, channel);

            } else if (msg instanceof FileListMessage) {
                FileListMessage listMessage = (FileListMessage) msg;
                readFileListMessage(listMessage , channel);

            } else if (msg instanceof CreateDirectoryMessage) {
                CreateDirectoryMessage dirMsg = (CreateDirectoryMessage) msg ;
                readCreateDirectoryMessage(dirMsg , channel);

            } else if (msg instanceof DeleteFileMessage) {
                DeleteFileMessage delMsg = (DeleteFileMessage)msg ;
                readDeleteFileMessage(delMsg, channel);

            } else if (msg instanceof DownloadFileMessage) {
                DownloadFileMessage dMsg = (DownloadFileMessage) msg ;
                readDownloadFileMessage(dMsg, channel);
            }

        } else if (msg instanceof AuthorizationMessage){
            AuthorizationMessage authMsg = (AuthorizationMessage) msg;
            readAuthorizationMessage(authMsg, channel);

        } else {
            channel.writeAndFlush(new ErrorResponse("Для начала работы необходимо авторизоваться!")) ;
        }

    }

    //Обрабатывает получение файла от клиента
    private void readFilePartMessage(FilePartMessage fileMsg, SocketChannel channel) {
        FilePartResponse response = new FilePartResponse();
        response.setNumberPart(fileMsg.getNumberPart());
        response.setCountParts(fileMsg.getCountParts());
        response.setPath(fileMsg.getPath());
        response.setResult(serverRepository.writeFilePart(fileMsg , channels.get(channel)));

        //если пришел последний пакет, то отправить файл лист
        if (fileMsg.getCountParts() == fileMsg.getNumberPart()) {
            FileListResponse responseList = new FileListResponse() ;
            responseList.getFileList().addAll(serverRepository.getFileListByParent(
                    fileMsg.getParent(),
                    channels.get(channel))) ;

            channel.writeAndFlush(responseList);
        } else {
            channel.writeAndFlush(response) ;
        }
    }

    //Обрабатывает отправку списка файлов в каталоге сервера
    private void readFileListMessage(FileListMessage listMessage, SocketChannel channel) {
        FileListResponse response = new FileListResponse() ;
        response.getFileList().addAll(serverRepository.getFileListByParent(
                listMessage.getPath(),
                channels.get(channel))) ;

        channel.writeAndFlush(response);
    }

    //обрабатывает создание каталога на сервере
    private void readCreateDirectoryMessage(CreateDirectoryMessage dirMsg, SocketChannel channel) {
        String dirPathStr = "".equals(dirMsg.getCurrentPath()) ?
                dirMsg.getDirName() : dirMsg.getCurrentPath() + "/" + dirMsg.getDirName();

        if(serverRepository.createDirectory( dirPathStr, channels.get(channel))) {
            FileListResponse response = new FileListResponse() ;
            response.getFileList().addAll(serverRepository.getFileListByParent(
                    dirMsg.getCurrentPath() ,
                    channels.get(channel))) ;

            channel.writeAndFlush(response);
        }
    }

    //Обрабатывает удаление файлов и каталогов
    private void readDeleteFileMessage(DeleteFileMessage delMsg , SocketChannel channel) {
        Path parent = Paths.get(delMsg.getPath()).getParent();
        parent = parent == null ? Paths.get("") : parent ;
        if (serverRepository.deleteFile(delMsg.getPath() , channels.get(channel))) {
            FileListResponse response = new FileListResponse();
            response.getFileList().addAll(serverRepository.getFileListByParent(
                    parent.toString(),
                    channels.get(channel)));

            channel.writeAndFlush(response);
        }
    }

    //Обрабатывает загрузку файлов с сервера
    private void readDownloadFileMessage(DownloadFileMessage dMsg, SocketChannel channel) {
        serverRepository.downloadFile(dMsg.getFilePath() , channel , channels.get(channel)) ;
    }

    //Обрабатывает авторизацию на сервере
    private void readAuthorizationMessage(AuthorizationMessage authMsg, SocketChannel channel) {
        boolean resultAuth = serverRepository.AuthorizationClient(authMsg) ;
        AuthorizationResponse response = new AuthorizationResponse(authMsg.getLogin() , resultAuth) ;

        if (resultAuth) {
            channels.put(channel , authMsg.getLogin()) ;
        }

        channel.writeAndFlush(response) ;
    }
}
