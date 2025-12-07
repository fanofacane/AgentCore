package com.sky.AgentCore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.account.AccountDTO;
import com.sky.AgentCore.dto.account.AccountEntity;

public interface AccountAppService extends IService<AccountEntity> {
    AccountDTO getUserAccount(String userId);
}
