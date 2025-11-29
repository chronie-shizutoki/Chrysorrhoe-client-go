package com.chronie.chrysorrhoego.data.remote;

import com.chronie.chrysorrhoego.data.remote.dto.WalletInfoResponse;
import com.chronie.chrysorrhoego.data.remote.dto.TransferResponse;
import com.chronie.chrysorrhoego.data.remote.dto.TransactionHistoryResponse;
import com.chronie.chrysorrhoego.data.remote.dto.CdkRedeemResponse;
import com.chronie.chrysorrhoego.data.remote.dto.TransactionStatusResponse;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    /**
     * 获取钱包信息
     * @return 钱包信息响应
     */
    @GET("/api/wallets/current")
    WalletInfoResponse getWalletInfo();

    /**
     * 根据API文档，转账应该使用/api/transfers/by-username端点
     */
    @FormUrlEncoded
    @POST("/api/transfers/by-username")
    TransferResponse transfer(
            @Field("fromUsername") String fromUsername,
            @Field("toUsername") String toUsername,
            @Field("amount") String amount,
            @Field("description") String description
    );

    /**
     * 转账方法（使用请求体）
     */
    @POST("/api/transfers")
    TransferResponse transfer(@Body TransferRequest request);

    /**
     * 根据API文档，获取交易历史
     */
    @GET("/api/wallets/{walletId}/transactions")
    TransactionHistoryResponse getWalletTransactionHistory(
            @Path("walletId") String walletId,
            @Query("page") int page,
            @Query("limit") int limit
    );

    /**
     * 获取交易历史（兼容Repository接口）
     */
    @GET("/api/wallets/current/transactions/detailed")
    TransactionHistoryResponse getTransactionHistory(
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );

    /**
     * 根据CDK API文档，CDK兑换应该使用/api/cdks/redeem端点
     */
    @FormUrlEncoded
    @POST("/api/cdks/redeem")
    CdkRedeemResponse redeemCdk(
            @Field("code") String code,
            @Field("username") String username
    );
    
    /**
     * CDK兑换方法（使用请求体）
     */
    @POST("/api/cdks/redeem")
    CdkRedeemResponse redeemCdk(@Body CdkRedeemRequest request);
    
    /**
     * 验证CDK接口
     */
    @FormUrlEncoded
    @POST("/api/cdks/validate")
    CdkRedeemResponse validateCdk(
            @Field("code") String code
    );
    
    /**
     * 验证交易状态
     */
    @GET("/api/transactions/status")
    TransactionStatusResponse getTransactionStatus(
            @Query("transactionId") String transactionId
    );

}