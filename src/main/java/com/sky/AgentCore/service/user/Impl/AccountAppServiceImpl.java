package com.sky.AgentCore.service.user.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.config.Exceptions.BusinessException;
import com.sky.AgentCore.dto.account.AccountDTO;
import com.sky.AgentCore.dto.account.AccountEntity;
import com.sky.AgentCore.mapper.user.AccountMapper;
import com.sky.AgentCore.service.user.AccountAppService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class AccountAppServiceImpl extends ServiceImpl<AccountMapper,AccountEntity> implements AccountAppService {
    /** 用户级别的锁，确保同一用户的账户操作串行化 */
    private final ConcurrentHashMap<String, ReentrantLock> userLocks = new ConcurrentHashMap<>();
    /** 获取用户账户信息
     * @param userId 用户ID
     * @return 账户DTO */
    @Override
    public AccountDTO getUserAccount(String userId) {
        AccountDTO accountDTO = new AccountDTO();
        AccountEntity entity = getOrCreateAccount(userId);
        BeanUtil.copyProperties(entity,accountDTO);
        return accountDTO;
    }
    /** 检查账户余额是否充足
     * @param userId 用户ID
     * @param amount 需要检查的金额
     * @return 是否充足 */
    @Override
    public boolean checkSufficientBalance(String userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("检查金额必须大于等于0");
        }

        AccountEntity account = lambdaQuery().eq(AccountEntity::getUserId, userId).one();
        if (account == null) return false;

        return account.checkSufficientBalance(amount);
    }
    /** 账户扣费（deductBalance的别名）
     * @param userId 用户ID
     * @param amount 扣费金额 */
    @Override
    public void deduct(String userId, BigDecimal amount) {
        deductBalance(userId, amount);
    }
    /** 扣除账户余额（带锁保护）
     * @param userId 用户ID
     * @param amount 扣除金额
     * @throws BusinessException 余额不足或其他业务异常 */
    public void deductBalance(String userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("扣费金额必须大于0");
        }

        ReentrantLock lock = getUserLock(userId);
        lock.lock();
        try {
            // 获取最新的账户信息
            AccountEntity account = getOrCreateAccount(userId);

            // 扣除余额
            account.deduct(amount);

            // 更新数据库
            boolean b = updateById(account);
            if (!b) throw new BusinessException("更新数据失败");

        } finally {
            lock.unlock();
        }
    }
    @Override
    public AccountEntity getOrCreateAccount(String userId){
        if (userId==null || userId.trim().isEmpty()) throw new BusinessException("用户ID不可为空");
        AccountEntity account = lambdaQuery().eq(AccountEntity::getUserId, userId).one();
        if (account==null){
            ReentrantLock lock = getUserLock(userId);
            lock.lock();
            try {
                //双重检查
                account = lambdaQuery().eq(AccountEntity::getUserId, userId).one();
                if (account == null){
                    account =AccountEntity.createNew(userId);
                    save(account);
                }
            }finally {
                lock.unlock();
            }
        }
        return account;
    }
    /** 获取用户的锁对象
     * @param userId 用户ID
     * @return 锁对象 */
    private ReentrantLock getUserLock(String userId) {
        return userLocks.computeIfAbsent(userId, k -> new ReentrantLock());
    }
}
