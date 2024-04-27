package icat.app.rpc.common.codec;

import icat.app.rpc.common.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * RPC服务编码器
 */
public class RpcEncoder extends MessageToByteEncoder<Object> {

    /**
     * 待编码对象类型
     */
    private final Class<?> clazz;

    private final Serializer serializer;

    public RpcEncoder(Class<?> clazz, Serializer serializer) {
        this.clazz = clazz;
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if (clazz.isInstance(o)) {
            byte[] data = serializer.serialize(o);
            byteBuf.writeInt(data.length);
            byteBuf.writeBytes(data);
        }
    }

}
