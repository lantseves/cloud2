package netty;

import io.netty.channel.*;
import main.java.AbstractMessage;
import main.java.FilePartMessage;

public class ClientHandler extends SimpleChannelInboundHandler<String> {

    private NettyMessageCallBack listener ;

    public ClientHandler(NettyMessageCallBack listener) {
        this.listener = listener;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        listener.readMessage(msg) ;
    }
}
