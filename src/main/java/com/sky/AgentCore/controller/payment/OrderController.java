package com.sky.AgentCore.controller.payment;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.AgentCore.dto.account.OrderDTO;
import com.sky.AgentCore.dto.account.QueryUserOrderRequest;
import com.sky.AgentCore.dto.common.Result;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 订单控制器 */
@RestController
@RequestMapping("/orders")
@Validated
public class OrderController {

    /** 获取当前用户的已支付订单列表
     *
     * @param queryRequest 查询参数
     * @return 订单分页列表 */
    @GetMapping
    public Result<Page<OrderDTO>> getUserOrders(QueryUserOrderRequest queryRequest) {
        Page<OrderDTO> emptyPage = new Page<>(
                queryRequest.getPage() == null ? 1 : queryRequest.getPage(),
                queryRequest.getPageSize() == null ? 10 : queryRequest.getPageSize()
        );
        emptyPage.setTotal(0);

        return Result.success(emptyPage);
    }

    /** 获取订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情 */
    @GetMapping("/{orderId}")
    public Result<OrderDTO> getOrderDetail(@PathVariable String orderId) {
        return Result.success();
    }
}