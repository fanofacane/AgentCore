package com.sky.AgentCore.controller.payment;

import com.sky.AgentCore.dto.account.OrderStatusResponseDTO;
import com.sky.AgentCore.dto.account.PaymentMethodDTO;
import com.sky.AgentCore.dto.account.PaymentResponseDTO;
import com.sky.AgentCore.dto.account.RechargeRequest;
import com.sky.AgentCore.dto.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 支付控制器 */
@RestController
@RequestMapping("/payments")
@Validated
public class PaymentController {


    /** 创建充值支付
     *
     * @param request 充值请求
     * @return 支付响应 */
    @PostMapping("/recharge")
    public Result<PaymentResponseDTO> createRechargePayment(@RequestBody @Validated RechargeRequest request) {
        return Result.success();
    }

    /** 查询订单状态
     *
     * @param orderNo 订单号
     * @return 订单状态响应 */
    @GetMapping("/orders/{orderNo}/status")
    public Result<OrderStatusResponseDTO> queryOrderStatus(@PathVariable String orderNo) {
        return Result.success();
    }

    /** 获取可用的支付方法列表
     *
     * @return 支付方法列表 */
    @GetMapping("/methods")
    public Result<List<PaymentMethodDTO>> getAvailablePaymentMethods() {
        return Result.success();
    }

    /** 处理支付平台回调
     *
     * @param platform 支付平台代码
     * @param request HTTP请求对象
     * @return 回调响应 */
    @PostMapping("/callback/{platform}")
    public ResponseEntity<String> handlePaymentCallback(@PathVariable String platform, HttpServletRequest request) {
        return null;
    }
}

