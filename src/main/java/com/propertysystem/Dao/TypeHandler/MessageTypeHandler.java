package com.propertysystem.Dao.TypeHandler;

import com.propertysystem.Constant.MessageType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MessageTypeHandler extends BaseTypeHandler<MessageType> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, MessageType parameter, JdbcType jdbcType) throws SQLException {
        // 将枚举的序号存储到数据库int字段中
        ps.setInt(i, parameter.ordinal());
    }

    @Override
    public MessageType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        // 从数据库int字段读取并转换为枚举
        Integer value = rs.getObject(columnName, Integer.class);
        return value == null ? null : MessageType.values()[value];
    }

    @Override
    public MessageType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        // 从数据库int字段读取并转换为枚举
        Integer value = rs.getObject(columnIndex, Integer.class);
        return value == null ? null : MessageType.values()[value];
    }

    @Override
    public MessageType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        // 从存储过程int字段读取并转换为枚举
        Integer value = cs.getObject(columnIndex, Integer.class);
        return value == null ? null : MessageType.values()[value];
    }

}
