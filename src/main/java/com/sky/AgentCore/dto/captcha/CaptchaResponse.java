package com.sky.AgentCore.dto.captcha;

import lombok.Data;

@Data
public class CaptchaResponse {
    private String uuid;
    private String imageBase64;

    public CaptchaResponse(String uuid, String imageBase64) {
        this.uuid = uuid;
        this.imageBase64 = imageBase64;
    }
}
