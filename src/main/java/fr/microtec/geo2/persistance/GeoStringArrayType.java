package fr.microtec.geo2.persistance;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleConnection;
import oracle.sql.ARRAY;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.ProcedureParameterExtractionAware;
import org.hibernate.type.ProcedureParameterNamedBinder;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.*;
import java.util.Arrays;

/**
 * Hibernate Type for converting Oracle 'P_STR_TAB_TYPE' to String[] in JAVA and vis-versa.
 */
public class GeoStringArrayType implements UserType, ProcedureParameterNamedBinder, ProcedureParameterExtractionAware<String[]> {

    @Override
    public int[] sqlTypes() {
        return new int[]{ Types.ARRAY };
    }

    @Override
    public Class returnedClass() {
        return String[].class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x instanceof String[] && y instanceof String[]) {
            return Arrays.deepEquals((String[])x, (String[])y);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return Arrays.hashCode((String[]) x);
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        Array array = rs.getArray(names[0]);

        return array != null ? array.getArray() : null;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value != null) {
            // Create Oracle array
            ARRAY array = this.convertToClobArray(session, (String []) value);

            // bind array
            st.setArray(index, array);
        } else {
            st.setNull(index, sqlTypes()[0]);
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        String[] a = (String[])value;

        return Arrays.copyOf(a, a.length);
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    @Override
    public boolean canDoSetting() {
        return true;
    }

    @Override
    public void nullSafeSet(CallableStatement statement, Object value, String name, SharedSessionContractImplementor session) throws SQLException {
        OracleCallableStatement stmt = statement.unwrap(OracleCallableStatement.class);

        if (value != null) {
            // Create Oracle array
            ARRAY array = this.convertToClobArray(session, (String []) value);

            // bind array
            stmt.setArray(name, array);
        } else {
            stmt.setNull(name, sqlTypes()[0]);
        }
    }

    private ARRAY convertToClobArray(SharedSessionContractImplementor session, String[] values) throws SQLException {
        OracleConnection connection = session.connection().unwrap(OracleConnection.class);

        Clob[] clobValues = Arrays.stream((String[]) values)
            .map(v -> {
                Clob clob = null;
                try {
                    clob = connection.createClob();
                    clob.setString(1, v);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                return clob;
            })
            .toArray(Clob[]::new);

        // Create Oracle array
        return connection.createARRAY(getOracleTypeName(), clobValues);
    }

    @Override
    public boolean canDoExtraction() {
        return true;
    }

    @Override
    public String[] extract(CallableStatement statement, int startIndex, SharedSessionContractImplementor session) throws SQLException {
        return this.convertToStringArray(statement.getArray(startIndex));
    }

    @Override
    public String[] extract(CallableStatement statement, String[] paramNames, SharedSessionContractImplementor session) throws SQLException {
        return this.convertToStringArray(statement.getArray(paramNames[0]));
    }

    private String[] convertToStringArray(Array values) throws SQLException {
        Clob[] clobValues = (Clob[]) values.getArray();

        if (clobValues == null) return null;

        return Arrays.stream(clobValues)
            .map(Clob::toString)
            .toArray(String[]::new);
    }

    /**
     * Oracle type name.
     */
    public static String getOracleTypeName() {
        return "P_STR_TAB_TYPE";
    }
}
