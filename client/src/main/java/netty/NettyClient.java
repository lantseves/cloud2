package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import main.java.AbstractMessage;
import main.java.FilePartMessage;
import netty.coder.MessageEncode;

import java.nio.file.Paths;

public class NettyClient {
    private SocketChannel channel ;

    public NettyClient() {
        String host = "localhost";
        int port = 8189;
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Thread nettyThread = new Thread(() -> {
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(workerGroup);
                bootstrap.channel(NioSocketChannel.class);
                bootstrap.option(ChannelOption.SO_KEEPALIVE, true); //Прочитать доку по опциям, настроить реконект
                bootstrap.handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    public void initChannel(SocketChannel ch)
                            throws Exception {
                        channel = ch ;
                        ch.pipeline().addLast(
                                new StringDecoder(),
                                new MessageEncode(),
                                new ClientHandler(msg -> {
                                    //TODO Получение сообщения
                                    System.out.println(msg);
                                }));
                    }
                });

                ChannelFuture future = bootstrap.connect(host, port).sync();
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            }
        }) ;
        nettyThread.setDaemon(true);
        nettyThread.start();
    }

    //Отправляем сообщение на сервер
    public void writeMessage(AbstractMessage msg) {
        msg = new FilePartMessage(
                1 ,
                2,
                Paths.get("./client/src/main/resources"),
                new byte[]{(byte) 0x2, (byte) 0x3, (byte) 0x94}) ;
        System.out.println(msg);
        channel.writeAndFlush(msg);
    }
}
