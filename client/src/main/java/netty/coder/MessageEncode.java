package netty.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import main.java.AbstractMessage;
import main.java.FilePartMessage;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class MessageEncode extends MessageToByteEncoder<AbstractMessage> {
    private final Charset charset = StandardCharsets.UTF_8;

    @Override
    protected void encode(ChannelHandlerContext ctx, AbstractMessage msg, ByteBuf out) throws Exception {
        FilePartMessage fileMsg = (FilePartMessage) msg;
        out.writeInt(fileMsg.getNumberPart());
        out.writeInt(fileMsg.getCountParts());

        out.writeInt(fileMsg.getPathLength());
        out.writeCharSequence(fileMsg.getPath().toString(), charset);
        out.writeInt(fileMsg.getFileContentLength());
        out.writeBytes(fileMsg.getFileContent());
    }
}
