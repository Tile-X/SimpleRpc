package icat.app.rpc.common.serializer;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import icat.app.rpc.common.context.SchemaContext;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

public class ProtostuffSerializer implements Serializer {

    private static final Objenesis OBJENESIS = new ObjenesisStd(true);

    @Override
    public <T> byte[] serialize(T obj) {
        Class<T> clazz = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = SchemaContext.getSchema(clazz);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            T message = OBJENESIS.newInstance(clazz);
            Schema<T> schema = SchemaContext.getSchema(clazz);
            ProtostuffIOUtil.mergeFrom(bytes, message, schema);
            return message;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
