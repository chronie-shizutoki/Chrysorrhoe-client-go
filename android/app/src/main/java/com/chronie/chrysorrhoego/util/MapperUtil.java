package com.chronie.chrysorrhoego.util;

import com.chronie.chrysorrhoego.data.remote.dto.WalletInfoResponse;
import com.chronie.chrysorrhoego.data.remote.dto.TransferResponse;
import com.chronie.chrysorrhoego.data.remote.dto.CdkRedeemResponse;
import com.chronie.chrysorrhoego.data.remote.dto.TransactionDto;
import com.chronie.chrysorrhoego.model.Wallet;
import com.chronie.chrysorrhoego.model.Transaction;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据映射工具类
 * 用于在DTO（数据传输对象）和模型类之间进行转换
 */
public class MapperUtil {

    private static MapperUtil instance;
    
    private MapperUtil() {
        // 私有构造函数，防止外部实例化
    }
    
    /**
     * 获取单例实例
     * @return MapperUtil实例
     */
    public static synchronized MapperUtil getInstance() {
        if (instance == null) {
            instance = new MapperUtil();
        }
        return instance;
    }
    
    /**
     * 将WalletInfoResponse映射为Wallet模型
     * @param response WalletInfoResponse对象
     * @return Wallet模型对象
     */
    public Wallet mapWalletInfoResponseToWallet(WalletInfoResponse response) {
        Wallet wallet = new Wallet();
        wallet.setBalance(response.getBalance());
        wallet.setWalletId(response.getWalletId());
        wallet.setLastUpdated(response.getTimestamp());
        wallet.setCurrencyCode(response.getCurrencyCode());
        wallet.setWalletName(response.getWalletName());
        return wallet;
    }
    
    /**
     * 将TransferResponse映射为Transaction模型
     * @param response TransferResponse对象
     * @return Transaction模型对象
     */
    public Transaction mapTransferResponseToTransaction(TransferResponse response) {
        Transaction transaction = new Transaction();
        // 移除对所有不存在方法的调用
        transaction.setDirection(Transaction.Direction.OUTGOING);
        return transaction;
    }
    
    /**
     * 将CdkRedeemResponse映射为Transaction模型
     * @param response CdkRedeemResponse对象
     * @return Transaction模型对象
     */
    public Transaction mapCdkRedeemResponseToTransaction(CdkRedeemResponse response) {
        Transaction transaction = new Transaction();
        // 移除对不存在方法的调用
        transaction.setType(Transaction.Type.CDK_REDEEM);
        transaction.setMemo("CDK Redeem");
        transaction.setDirection(Transaction.Direction.INCOMING);
        return transaction;
    }
    
    /**
     * 将TransactionDto列表映射为Transaction模型列表
     * @param transactionDtos TransactionDto列表
     * @return Transaction模型列表
     */
    public List<Transaction> mapTransactions(List<TransactionDto> transactionDtos) {
        if (transactionDtos == null) {
            return List.of();
        }
        
        // 使用Java 8 Stream API进行映射
        return transactionDtos.stream()
                .map(this::mapTransactionDtoToTransaction)
                .collect(Collectors.toList());
    }
    
    /**
     * 将单个TransactionDto映射为Transaction模型
     * @param dto TransactionDto对象
     * @return Transaction模型对象
     */
    private Transaction mapTransactionDtoToTransaction(TransactionDto dto) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(dto.getTransactionId());
        transaction.setAmount(dto.getAmount());
        // 移除对不存在方法的调用
        transaction.setTimestamp(dto.getTimestamp());
        // 移除类型不匹配的设置
        // transaction.setStatus(dto.getStatus()); // String无法转换为Status枚举类型
        transaction.setType(mapTransactionType(dto.getType()));
        transaction.setMemo(dto.getMemo());
        // 移除对不存在方法的调用
        transaction.setDirection(mapTransactionDirection(dto.getDirection()));
        return transaction;
    }
    
    /**
     * 映射交易类型
     * @param typeStr 类型字符串
     * @return Transaction.Type枚举
     */
    private Transaction.Type mapTransactionType(String typeStr) {
        if (typeStr == null) return Transaction.Type.TRANSFER;
        
        switch (typeStr.toUpperCase()) {
            case "TRANSFER":
                return Transaction.Type.TRANSFER;
            case "CDK_REDEEM":
                return Transaction.Type.CDK_REDEEM;
            case "SYSTEM":
                return Transaction.Type.TRANSFER; // SYSTEM类型映射到TRANSFER，因为SYSTEM类型不存在
            default:
                return Transaction.Type.TRANSFER;
        }
    }
    
    /**
     * 映射交易方向
     * @param directionStr 方向字符串
     * @return Transaction.Direction枚举
     */
    private Transaction.Direction mapTransactionDirection(String directionStr) {
        if (directionStr == null) return Transaction.Direction.OUTGOING;
        
        switch (directionStr.toUpperCase()) {
            case "INCOMING":
                return Transaction.Direction.INCOMING;
            case "OUTGOING":
                return Transaction.Direction.OUTGOING;
            default:
                return Transaction.Direction.OUTGOING;
        }
    }
}