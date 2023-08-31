package com.calcifer.weight.typehandler;

import com.calcifer.weight.entity.enums.ICodeEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * mybatis 枚举类处理器 数据库字段和POJO转换时使用code值
 *
 * @param <E>
 */
public class CodeEnumTypeHandler<E extends Enum<?> & ICodeEnum> extends BaseTypeHandler<ICodeEnum> {
    private Class<E> type;

    public CodeEnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, ICodeEnum iCodeEnum, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i, iCodeEnum.getCode());
    }

    @Override
    public ICodeEnum getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        int i = resultSet.getInt(columnName);
        if (resultSet.wasNull()) {
            return null;
        } else {
            try {
                return ICodeEnum.codeOf(type, i);
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot convert " + i + " to " + type.getSimpleName() + " by code value.", e);
            }
        }
    }

    @Override
    public ICodeEnum getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        int i = resultSet.getInt(columnIndex);
        if (resultSet.wasNull()) {
            return null;
        } else {
            try {
                return ICodeEnum.codeOf(type, i);
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot convert " + i + " to " + type.getSimpleName() + " by code value.", e);
            }
        }

    }

    @Override
    public ICodeEnum getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        int i = callableStatement.getInt(columnIndex);
        if (callableStatement.wasNull()) {
            return null;
        } else {
            try {
                return ICodeEnum.codeOf(type, i);
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot convert " + i + " to " + type.getSimpleName() + " by code value.", e);
            }
        }
    }
}
