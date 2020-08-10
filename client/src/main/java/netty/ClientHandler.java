package netty;

import io.netty.channel.*;
import main.java.message.AuthorizationMessage;
import main.java.message.FileListMessage;
import main.java.message.FilePartMessage;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private NettyMessageCallBack listener ;

    public ClientHandler(NettyMessageCallBack listener) {
        this.listener = listener;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Path pathRoot = Paths.get("./client/src/main/user_files") ;
        ctx.writeAndFlush(new AuthorizationMessage("lantsev","123"));
        ctx.writeAndFlush(new FileListMessage("./")) ;

        /*
        Path path = Paths.get("./client/src/main/user_files/mydirectory/lesson5.sql") ;
        try(FileInputStream fis = new FileInputStream(path.toFile())) {
            int countParts = (int)Math.ceil(fis.available() / 1024f) ;
            for (int i = 1 ; fis.available() > 0 ; i++) {
                byte[] buffer = new byte[1024] ;
                FilePartMessage filePart = new FilePartMessage() ;
                filePart.setNumberPart(i);
                filePart.setCountParts(countParts);
                filePart.setPath(pathRoot.relativize(path).toString());
                fis.read(buffer);
                filePart.setFileContent(buffer);
                ctx.writeAndFlush(filePart);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        */
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        listener.readMessage(msg) ;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace() ;
    }
}
