package com.sky.AgentCore.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.account.AccountDTO;
import com.sky.AgentCore.dto.account.AccountEntity;

import java.math.BigDecimal;

public interface AccountAppService extends IService<AccountEntity> {
    AccountDTO getUserAccount(String userId);

    boolean checkSufficientBalance(String userId, BigDecimal cost);

    void deduct(String userId, BigDecimal cost);

    AccountEntity getOrCreateAccount(String userId);
}
