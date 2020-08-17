package netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import main.java.entity.FileResponse;
import main.java.message.AuthorizationMessage;
import main.java.message.CreateDirectoryMessage;
import main.java.message.FileListMessage;
import main.java.message.FilePartMessage;
import main.java.response.AuthorizationResponse;
import main.java.response.ErrorResponse;
import main.java.response.FileListResponse;
import main.java.response.FilePartResponse;
import repository.ServerRepository;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class BroadCastHandler extends ChannelInboundHandlerAdapter {
    private static ConcurrentMap<SocketChannel , String> channels = new ConcurrentHashMap<>();
    private static ServerRepository serverRepository = new ServerRepository() ;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        SocketChannel channel = (SocketChannel) ctx.channel();
        if (channels.containsKey(channel)) {

            if(msg instanceof FilePartMessage) {
                FilePartMessage fileMsg = (FilePartMessage) msg;
                FilePartResponse response = new FilePartResponse();
                response.setNumberPart(fileMsg.getNumberPart());
                response.setCountParts(fileMsg.getCountParts());
                response.setPath(fileMsg.getPath());
                response.setResult(serverRepository.writeFilePart(fileMsg , channels.get(channel)));

                channel.writeAndFlush(response) ;

            } else if (msg instanceof FileListMessage) {
                FileListMessage listMessage = (FileListMessage) msg;
                FileListResponse response = new FileListResponse() ;
                List<FileResponse> fileList = serverRepository.getFileListByParent(
                        listMessage.getPath(),
                        channels.get(channel));
                response.getFileList().addAll(fileList) ;
                channel.writeAndFlush(response);

            } else if (msg instanceof CreateDirectoryMessage) {
                CreateDirectoryMessage dirMsg = (CreateDirectoryMessage) msg ;
                if(serverRepository.createDirectory(
                        dirMsg.getCurrentPath() + dirMsg.getDirName() ,
                        channels.get(channel))) {
                    channel.writeAndFlush(serverRepository.getFileListByParent(
                            dirMsg.getCurrentPath() ,
                            channels.get(channel)));
                }
            }

        } else if (msg instanceof AuthorizationMessage){
            AuthorizationMessage authMsg = (AuthorizationMessage) msg;
            boolean resultAuth = serverRepository.AuthorizationClient(authMsg) ;
            AuthorizationResponse response = new AuthorizationResponse(authMsg.getLogin() , resultAuth) ;

            if (resultAuth) {
                channels.put(channel , authMsg.getLogin()) ;
            }

            channel.writeAndFlush(response) ;
        } else {
            channel.writeAndFlush(new ErrorResponse("Для начала работы необходимо авторизоваться!")) ;
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx)  {
        channels.remove((SocketChannel)ctx.channel()) ;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }
}
