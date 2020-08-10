package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

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
                    public void initChannel(SocketChannel ch) {
                        channel = ch ;
                        ch.pipeline().addLast(
                                new ObjectEncoder(),
                                new ObjectDecoder(1024 * 1024 * 100, ClassResolvers.cacheDisabled(null)),
                                new ClientHandler(System.out::println)); //TODO Получение сообщения
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
    public void writeMessage(Object msg) {
        channel.writeAndFlush(msg);
    }
}
