package com.sky.AgentCore.dto.gateway;

import lombok.Data;
/** 网关统一响应结果
 *
 * @author fanofacane
 * @since 1.0.0 */
@Data
public class GatewayResult<T> {

    /** 响应码 */
    private Integer code;

    /** 响应消息 */
    private String message;

    /** 响应数据 */
    private T data;

    /** 是否成功 */
    private Boolean success;

    public GatewayResult() {
    }

    public GatewayResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = code != null && code == 200;
    }

    public void setCode(Integer code) {
        this.code = code;
        this.success = code != null && code == 200;
    }
    public boolean isSuccess() {
        return Boolean.TRUE.equals(success);
    }
}
