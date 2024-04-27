package icat.app.rpc.common.entity;

import lombok.Data;

/**
 * RPC响应实体
 */
@Data
public class RpcResponse {

    /**
     * 响应ID（与请求ID对应）
     */
    private String requestId;

    /**
     * 调用响应异常
     */
    private Exception exception;

    /**
     * 调用响应结果
     */
    private Object result;

    /**
     * 该响应是否存在异常
     * @return 是否存在异常
     */
    public boolean hasException() {
        return exception != null;
    }

}
