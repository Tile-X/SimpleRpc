package icat.app.rpc.common.context;

import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SchemaContext {
    private static final Map<Class<?>, Schema<?>> CACHED_SCHEMA = new ConcurrentHashMap<>();

    public static <T> Schema<T> getSchema(Class<T> cls) {
        Schema<?> schema = CACHED_SCHEMA.get(cls);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(cls);
            CACHED_SCHEMA.put(cls, schema);
        }
        return (Schema<T>) schema;
    }
}
