package icat.app.rpc.common.serializer;

public interface Serializer {

    /**
     * 对象序列化
     * @param obj 需要序列化的对象
     * @return 序列化结果
     */
    <T> byte[] serialize(T obj);

    /**
     * 对象反序列化
     * @param bytes 对象序列
     * @param clazz 对象类型
     * @return 反序列化的到的对象
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);

}
