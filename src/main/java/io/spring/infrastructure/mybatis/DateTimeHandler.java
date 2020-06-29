package io.spring.infrastructure.mybatis;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;
import org.joda.time.DateTime;

import java.sql.*;
import java.util.Calendar;
import java.util.TimeZone;

@MappedTypes(DateTime.class)
public class DateTimeHandler implements TypeHandler<DateTime> {

    private  final Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    @Override
    public void setParameter(PreparedStatement ps, int i, DateTime parameter, JdbcType jdbcType) throws SQLException {
        ps.setTimestamp(i, parameter != null ? new Timestamp(parameter.getMillis()) : null, utc);
    }

    @Override
    public DateTime getResult(ResultSet rs, String columnName) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(columnName, utc);
        return timestamp != null ? new DateTime(timestamp.getTime()) : null;
    }

    @Override
    public DateTime getResult(ResultSet rs, int columnIndex) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(columnIndex, utc);
        return timestamp != null ? new DateTime(timestamp.getTime()) : null;
    }

    @Override
    public DateTime getResult(CallableStatement cs, int columnIndex) throws SQLException {
        Timestamp ts = cs.getTimestamp(columnIndex, utc);
        return ts != null ? new DateTime(ts.getTime()) : null;
    }

}
