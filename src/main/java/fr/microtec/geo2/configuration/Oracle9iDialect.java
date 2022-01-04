package fr.microtec.geo2.configuration;

import org.hibernate.dialect.OracleTypesHelper;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Oracle9iDialect extends org.hibernate.dialect.Oracle9iDialect {

    public Oracle9iDialect() {
        super();
    }

    /**
     * registerResultSetOutParameter and getResultSet is override for enable resultSet (cursor) output from stored procedures.
     * It's work but not defined in Oracle9iDialect in hibernate.
     */

    @Override
    public int registerResultSetOutParameter(CallableStatement statement, String name) throws SQLException {
        statement.registerOutParameter(name, OracleTypesHelper.INSTANCE.getOracleCursorTypeSqlType());
        return 1;
    }

    @Override
    public ResultSet getResultSet(CallableStatement statement, String name) throws SQLException {
        return (ResultSet) statement.getObject(name);
    }

}
