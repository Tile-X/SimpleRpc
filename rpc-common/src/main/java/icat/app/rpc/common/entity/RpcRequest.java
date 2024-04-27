package icat.app.rpc.common.entity;

import lombok.Data;

/**
 * RPC请求实体类
 */
@Data
public class RpcRequest {

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 请求接口名
     */
    private String interfaceName;

    /**
     * 请求服务版本信息
     */
    private String serviceVersion;

    /**
     * 请求方法名
     */
    private String methodName;

    /**
     * 请求参数类型
     */
    private Class<?>[] parameterTypes;

    /**
     * 请求参数实体
     */
    private Object[] parameters;
}
