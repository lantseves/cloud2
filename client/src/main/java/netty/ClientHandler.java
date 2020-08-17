package netty;

import io.netty.channel.*;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private NettyMessageCallBack listener ;

    public ClientHandler(NettyMessageCallBack listener) {
        this.listener = listener;
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
