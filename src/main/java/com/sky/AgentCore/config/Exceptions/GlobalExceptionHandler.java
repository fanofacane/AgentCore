package com.sky.AgentCore.config.Exceptions;


import com.sky.AgentCore.dto.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * RestControllerAdvice 会将返回值自动转为 JSON
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        // 返回自定义的错误信息给前端
        return Result.error(400, e.getMessage());
    }

    /**
     * 处理其他所有未捕获的异常（兜底）
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        // 生产环境建议记录日志，不要把具体异常信息返回给前端
        e.printStackTrace();
        return Result.error(500, "服务器内部错误");
    }
}
