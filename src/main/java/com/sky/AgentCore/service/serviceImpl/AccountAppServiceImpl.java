package com.sky.AgentCore.service.serviceImpl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.Exceptions.BusinessException;
import com.sky.AgentCore.dto.account.AccountDTO;
import com.sky.AgentCore.dto.account.AccountEntity;
import com.sky.AgentCore.mapper.AccountMapper;
import com.sky.AgentCore.service.AccountAppService;
import org.springframework.stereotype.Service;

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
    private AccountEntity getOrCreateAccount(String userId){
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
