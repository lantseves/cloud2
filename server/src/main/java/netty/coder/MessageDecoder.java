package netty.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import main.java.AbstractMessage;
import main.java.FilePartMessage;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class MessageDecoder extends ReplayingDecoder<AbstractMessage> {
    private final Charset charset = StandardCharsets.UTF_8;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        FilePartMessage msg = new FilePartMessage();
        msg.setNumberPart(in.readInt());
        msg.setCountParts(in.readInt());

        msg.setPath(Paths.get(in.readCharSequence(in.readInt(), charset).toString()));

        byte[] fileBuffer = new byte[in.readInt()];
        in.readBytes(fileBuffer);
        msg.setFileContent(fileBuffer);

        out.add(msg);
    }
}
