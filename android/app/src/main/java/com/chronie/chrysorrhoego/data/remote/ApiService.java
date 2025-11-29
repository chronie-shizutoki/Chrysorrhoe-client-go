package com.chronie.chrysorrhoego.data.remote;

import com.chronie.chrysorrhoego.data.remote.dto.TransferResponse;
import com.chronie.chrysorrhoego.data.remote.dto.TransactionHistoryResponse;
import com.chronie.chrysorrhoego.data.remote.dto.CdkRedeemResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    // 根据API文档，转账应该使用/api/transfers/by-username端点
    @FormUrlEncoded
    @POST("/api/transfers/by-username")
    Call<TransferResponse> transfer(
            @Field("fromUsername") String fromUsername,
            @Field("toUsername") String toUsername,
            @Field("amount") String amount,
            @Field("description") String description
    );

    // 根据API文档，获取交易历史应该使用/api/wallets/:walletId/transactions端点
    // 由于需要walletId参数，这里提供两个版本：一个通过路径参数，一个通过查询参数
    @GET("/api/wallets/{walletId}/transactions")
    Call<TransactionHistoryResponse> getWalletTransactionHistory(
            @Path("walletId") String walletId,
            @Query("page") int page,
            @Query("limit") int limit
    );

    // 根据JavaScript版本实现，正确的交易历史API路径应该是带walletId的路径
    @GET("/api/wallets/{walletId}/transactions/detailed")
    Call<TransactionHistoryResponse> getTransactionHistory(
            @Path("walletId") String walletId,
            @Query("page") int page,
            @Query("limit") int limit
    );

    // 根据CDK API文档，CDK兑换应该使用/api/cdks/redeem端点
    @FormUrlEncoded
    @POST("/api/cdks/redeem")
    Call<CdkRedeemResponse> redeemCdk(
            @Field("code") String code,
            @Field("username") String username
    );
    
    // 验证CDK接口
    @FormUrlEncoded
    @POST("/api/cdks/validate")
    Call<CdkRedeemResponse> validateCdk(
            @Field("code") String code
    );
}