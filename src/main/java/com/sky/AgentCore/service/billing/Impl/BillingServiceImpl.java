package com.sky.AgentCore.service.billing.Impl;

import com.sky.AgentCore.config.Exceptions.BusinessException;
import com.sky.AgentCore.config.Factory.BillingStrategyFactory;
import com.sky.AgentCore.dto.billing.RuleContext;
import com.sky.AgentCore.dto.billing.UsageRecordEntity;
import com.sky.AgentCore.dto.product.ProductEntity;
import com.sky.AgentCore.dto.product.RuleEntity;
import com.sky.AgentCore.service.user.AccountAppService;
import com.sky.AgentCore.service.billing.BillingService;
import com.sky.AgentCore.service.rule.ProductService;
import com.sky.AgentCore.service.rule.RuleService;
import com.sky.AgentCore.service.rule.RuleStrategy;
import com.sky.AgentCore.service.user.Impl.UsageRecordBusinessInfoService;
import com.sky.AgentCore.service.user.UsageRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class BillingServiceImpl implements BillingService {
    @Autowired
    private ProductService productService;
    @Autowired
    private RuleService ruleService;
    @Autowired
    private BillingStrategyFactory billingStrategyFactory;
    @Autowired
    private AccountAppService accountAppService;
    @Autowired
    private UsageRecordService usageRecordService;
    @Autowired
    private UsageRecordBusinessInfoService businessInfoService;
    @Override
    public boolean checkBalance(RuleContext context) {
        try {
            // 查找商品
            ProductEntity product = productService.findProductByBusinessKey(context.getType(),
                    context.getServiceId());

            if (product == null || !product.isActive()) return true; // 无需计费

            // 获取规则和策略
            RuleEntity rule = ruleService.getRuleById(product.getRuleId());
            if (rule == null) return false;

            RuleStrategy strategy = billingStrategyFactory.getStrategy(rule.getHandlerKey());

            // 计算费用
            BigDecimal cost = strategy.process(context.getUsageData(), product.getPricingConfig());
            System.out.println("费用"+ cost);
            // 实现最低计费0.01元逻辑
            if (cost.compareTo(BigDecimal.ZERO) > 0 && cost.compareTo(new BigDecimal("0.01")) < 0) {
                cost = new BigDecimal("0.01");
            }

            // 允许为负数
            if (cost.compareTo(BigDecimal.ZERO) <= 0) {
                return true; // 无需扣费
            }

            // 检查余额
            return accountAppService.checkSufficientBalance(context.getUserId(), cost);

        } catch (Exception e) {
            return false;
        }
    }
    /** 执行计费
     *
     * @param context 计费上下文
     * @throws BusinessException 余额不足或其他业务异常 */
    @Override
    @Transactional
    public void charge(RuleContext context) {
        // 1. 验证上下文
        if (!context.isValid()) throw new BusinessException("无效的计费上下文");

        // 2. 查找商品
        ProductEntity product = productService.findProductByBusinessKey(context.getType(), context.getServiceId());
        // 没有配置计费规则，直接放行
        if (product == null) return;

        if (!product.isActive()) throw new BusinessException("商品已被禁用，无法计费");

        // 3. 检查幂等性
        if (context.getRequestId() != null && usageRecordService.existsByRequestId(context.getRequestId())) {
            // 请求已处理，直接返回
            return;
        }

        // 4. 获取规则和策略
        RuleEntity rule = ruleService.getRuleById(product.getRuleId());
        if (rule == null) throw new BusinessException("关联的计费规则不存在");

        RuleStrategy strategy = billingStrategyFactory.getStrategy(rule.getHandlerKey());

        // 5. 计算费用
        BigDecimal cost = strategy.process(context.getUsageData(), product.getPricingConfig());

        if (cost.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("计算出的费用不能为负数");
        }

        // 实现最低计费0.01元逻辑：如果费用大于0但小于0.01，则按0.01计算
        if (cost.compareTo(BigDecimal.ZERO) > 0 && cost.compareTo(new BigDecimal("0.01")) < 0) {
            cost = new BigDecimal("0.01");
        }

        // 如果费用为0，也需要记录用量，但不扣费
        if (cost.compareTo(BigDecimal.ZERO) == 0) {
            recordUsage(context, product, cost);
            return;
        }

        // 6. 检查余额并扣费
        accountAppService.deduct(context.getUserId(), cost);
        System.out.println("扣费成功");
        // 7. 记录用量
        recordUsage(context, product, cost);
    }
    /** 记录用量 */
    private void recordUsage(RuleContext context, ProductEntity product, BigDecimal cost) {
        // 获取业务信息
        Map<String, UsageRecordBusinessInfoService.BusinessInfo> businessInfoMap = businessInfoService
                .getBatchBusinessInfo(Set.of(product.getId()));
        UsageRecordBusinessInfoService.BusinessInfo businessInfo = businessInfoMap.get(product.getId());

        String serviceName = businessInfo != null ? businessInfo.getServiceName() : "未知服务";
        String serviceType = businessInfo != null ? businessInfo.getServiceType() : "未知类型";
        String serviceDescription = businessInfo != null ? businessInfo.getServiceDescription() : "";
        String pricingRule = businessInfo != null ? businessInfo.getPricingRule() : "";
        String relatedEntityName = businessInfo != null ? businessInfo.getRelatedEntityName() : "";

        // 使用新的创建方法，包含业务信息
        UsageRecordEntity usageRecord = UsageRecordEntity.createNewWithBusinessInfo(context.getUserId(),
                product.getId(), context.getUsageData(), cost, context.getRequestId(), serviceName, serviceType,
                serviceDescription, pricingRule, relatedEntityName);
        usageRecord.setId(UUID.randomUUID().toString());

        usageRecordService.createUsageRecord(usageRecord);
    }
}
