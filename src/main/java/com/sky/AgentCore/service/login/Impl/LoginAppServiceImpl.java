package com.sky.AgentCore.service.login.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.Exceptions.BusinessException;
import com.sky.AgentCore.dto.login.EmailLoginRequest;
import com.sky.AgentCore.dto.login.LoginRequest;
import com.sky.AgentCore.dto.login.RegisterRequest;
import com.sky.AgentCore.dto.user.UserEntity;
import com.sky.AgentCore.enums.AuthFeatureKey;
import com.sky.AgentCore.mapper.UserMapper;
import com.sky.AgentCore.service.auth.AuthSettingAppService;
import com.sky.AgentCore.service.login.LoginAppService;
import com.sky.AgentCore.utils.EmailService;
import com.sky.AgentCore.utils.JwtUtils;
import com.sky.AgentCore.utils.PasswordUtils;
import com.sky.AgentCore.utils.VerificationCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

import static com.sky.AgentCore.utils.VerificationCode.BUSINESS_TYPE_EMAIL_LOGIN;
import static com.sky.AgentCore.utils.VerificationCode.BUSINESS_TYPE_REGISTER;

@Service
public class LoginAppServiceImpl extends ServiceImpl<UserMapper,UserEntity> implements LoginAppService {
    @Autowired
    private AuthSettingAppService authSettingDomainService;
    @Autowired
    private VerificationCode verificationCode;
    @Autowired
    private EmailService emailService;
    @Override
    public String login(LoginRequest loginRequest) {
        // 检查普通登录是否启用
        if (!authSettingDomainService.isFeatureEnabled(AuthFeatureKey.EMAIL_LOGIN)) throw new BusinessException("邮箱登录已禁用");
        //检查邮箱或手机账号是否存在且密码正确
        UserEntity userEntity = lambdaQuery().eq(UserEntity::getEmail, loginRequest.getAccount()).or().eq(UserEntity::getPhone, loginRequest.getAccount()).one();
        if (userEntity==null || !PasswordUtils.matches(loginRequest.getPassword(),userEntity.getPassword())) throw new BusinessException("账号密码错误");

        return JwtUtils.generateToken(userEntity.getId());
    }

    @Override
    public void register(RegisterRequest registerRequest) {
        if (!authSettingDomainService.isFeatureEnabled(AuthFeatureKey.USER_REGISTER)){
            throw new BusinessException("用户注册已禁用");
        }
    if (StringUtils.hasText(registerRequest.getEmail()) && !StringUtils.hasText(registerRequest.getPhone())){
        if (!StringUtils.hasText(registerRequest.getCode())){
            throw new BusinessException("邮箱注册时必须提供验证码");
        }
        boolean isValid = verificationCode.verifyCode(registerRequest.getEmail(), registerRequest.getCode(),BUSINESS_TYPE_REGISTER);
        if (!isValid) throw new BusinessException("验证码无效或者过期");
    }
        checkAccountExists(registerRequest.getEmail(),registerRequest.getPhone());
        fillRegister(registerRequest);

    }
    /** 发送注册邮箱验证码 */
    @Override
    public void sendEmailVerificationCode(String email, String captchaUuid, String captchaCode, String ip, String businessType) {
        // 检查用户注册是否启用
        if (!authSettingDomainService.isFeatureEnabled(AuthFeatureKey.USER_REGISTER)) {
            throw new BusinessException("用户注册已禁用");
        }

        // 检查邮箱是否已存在
        boolean exists = lambdaQuery().eq(UserEntity::getEmail, email).exists();
        if (exists) throw new BusinessException("账号已存在,不可重复账注册");

        // 生成验证码并发送邮件
        String code = verificationCode.generateCode(email, captchaUuid, captchaCode, ip, businessType);
        emailService.sendVerificationCode(email, code);
    }
    /** 发送邮箱登录验证码 */
    @Override
    public void sendEmailLoginVerificationCode(String email, String captchaUuid, String captchaCode, String ip, String businessType) {
        // 检查用户注册是否启用
        if (!authSettingDomainService.isFeatureEnabled(AuthFeatureKey.EMAIL_LOGIN)) {
            throw new BusinessException("邮箱登录已禁用");
        }

        // 检查邮箱是否已存在
        boolean exists = lambdaQuery().eq(UserEntity::getEmail, email).exists();
        if (!exists) throw new BusinessException("账号不存在");

        // 生成验证码并发送邮件
        String code = verificationCode.generateCode(email, captchaUuid, captchaCode, ip, businessType);
        emailService.sendVerificationCode(email, code);
    }

    @Override
    public void sendResetPasswordCode(String email, String captchaUuid, String captchaCode, String ip) {
        // 检查普通登录是否启用
        if (!authSettingDomainService.isFeatureEnabled(AuthFeatureKey.NORMAL_LOGIN)) {
            throw new BusinessException("普通登录已禁用，无法重置密码");
        }

        // 检查邮箱是否存在，不存在则抛出异常
        UserEntity user = lambdaQuery().eq(UserEntity::getEmail, email).one();
        if (user == null) {
            throw new BusinessException("该邮箱未注册");
        }

        // 生成验证码并发送邮件
        String code = verificationCode.generateCode(email, captchaUuid, captchaCode, ip,
                VerificationCode.BUSINESS_TYPE_RESET_PASSWORD);
        emailService.sendVerificationCode(email, code);
    }

    @Override
    public String emailLogin(EmailLoginRequest emailLoginRequest) {
        // 检查邮箱登录是否启用
        if(!authSettingDomainService.isFeatureEnabled(AuthFeatureKey.EMAIL_LOGIN)) throw new BusinessException("邮箱登录已禁用");
        // 检查邮箱、验证码是否存在
        if(!StringUtils.hasText(emailLoginRequest.getEmail()) || !StringUtils.hasText(emailLoginRequest.getCode())) throw new BusinessException("邮箱登录时必须提供邮箱和验证码");
        // 验证码验证
        boolean isValid = verificationCode.verifyCode(emailLoginRequest.getEmail(), emailLoginRequest.getCode(), BUSINESS_TYPE_EMAIL_LOGIN);
        if (!isValid) throw new BusinessException("验证码无效或者过期");
        UserEntity user = lambdaQuery().eq(UserEntity::getEmail, emailLoginRequest.getEmail()).one();
        if (user==null) throw new BusinessException("用户不存在");
        return JwtUtils.generateToken(user.getId());
    }

    public void fillRegister(RegisterRequest registerRequest) {
        UserEntity userEntity = new UserEntity();
        BeanUtil.copyProperties(registerRequest,userEntity);
        userEntity.setPassword(PasswordUtils.encode(registerRequest.getPassword()));
        userEntity.valid();
        userEntity.setNickname(generateNickname());
        userEntity.setLoginPlatform("normal");
        save(userEntity);
    }
    public void checkAccountExists(String email,String phone) {
        if (lambdaQuery().eq(UserEntity::getEmail, email).or().eq(UserEntity::getPhone, phone).count() > 0) {
            throw new BusinessException("邮箱或手机号已存在");
        }
    }
    /** 随机生成用户昵称
     * @return 用户昵称 */
    private String generateNickname() {
        return "agent-x" + UUID.randomUUID().toString().replace("-", "").substring(0, 6);
    }
}
