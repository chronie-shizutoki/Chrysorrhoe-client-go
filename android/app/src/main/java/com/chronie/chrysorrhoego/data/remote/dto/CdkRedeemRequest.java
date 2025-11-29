package com.chronie.chrysorrhoego.data.remote.dto;

/**
 * CDK兑换请求数据传输对象
 * 用于封装CDK兑换API请求参数
 */
public class CdkRedeemRequest {

    private String cdk;
    private String username;
    private String clientId;
    
    // 默认构造函数
    public CdkRedeemRequest() {
    }
    
    // 构造函数
    public CdkRedeemRequest(String cdk) {
        this.cdk = cdk;
    }
    
    // Getter和Setter方法
    public String getCdk() {
        return cdk;
    }
    
    public void setCdk(String cdk) {
        this.cdk = cdk;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    @Override
    public String toString() {
        return "CdkRedeemRequest{" +
                "cdk='" + cdk + '\'' +
                ", username='" + username + '\'' +
                ", clientId='" + clientId + '\'' +
                '}';
    }
}