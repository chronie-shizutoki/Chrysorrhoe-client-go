package com.chronie.chrysorrhoego.data.remote;

/**
 * CDK兑换请求类，用于API调用的请求体
 */
public class CdkRedeemRequest {
    private String code;

    /**
     * 构造函数
     * @param code CDK码
     */
    public CdkRedeemRequest(String code) {
        this.code = code;
    }

    // Getter and Setter methods
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}