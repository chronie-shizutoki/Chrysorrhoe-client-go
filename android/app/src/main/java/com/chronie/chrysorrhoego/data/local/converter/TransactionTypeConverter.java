package com.chronie.chrysorrhoego.data.local.converter;

import androidx.room.TypeConverter;

import com.chronie.chrysorrhoego.model.Transaction;

/**
 * 交易类型转换器，用于Room数据库中枚举类型的转换
 */
public class TransactionTypeConverter {

    /**
     * 将Type枚举转换为String
     * @param type 交易类型枚举
     * @return 枚举名称
     */
    @TypeConverter
    public static String fromType(Transaction.Type type) {
        if (type == null) {
            return null;
        }
        return type.name();
    }

    /**
     * 将String转换为Type枚举
     * @param name 枚举名称
     * @return 交易类型枚举
     */
    @TypeConverter
    public static Transaction.Type toType(String name) {
        if (name == null) {
            return null;
        }
        return Transaction.Type.valueOf(name);
    }

    /**
     * 将Status枚举转换为String
     * @param status 交易状态枚举
     * @return 枚举名称
     */
    @TypeConverter
    public static String fromStatus(Transaction.Status status) {
        if (status == null) {
            return null;
        }
        return status.name();
    }

    /**
     * 将String转换为Status枚举
     * @param name 枚举名称
     * @return 交易状态枚举
     */
    @TypeConverter
    public static Transaction.Status toStatus(String name) {
        if (name == null) {
            return null;
        }
        return Transaction.Status.valueOf(name);
    }

    /**
     * 将Direction枚举转换为String
     * @param direction 交易方向枚举
     * @return 枚举名称
     */
    @TypeConverter
    public static String fromDirection(Transaction.Direction direction) {
        if (direction == null) {
            return null;
        }
        return direction.name();
    }

    /**
     * 将String转换为Direction枚举
     * @param name 枚举名称
     * @return 交易方向枚举
     */
    @TypeConverter
    public static Transaction.Direction toDirection(String name) {
        if (name == null) {
            return null;
        }
        return Transaction.Direction.valueOf(name);
    }
}