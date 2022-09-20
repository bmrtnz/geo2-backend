package fr.microtec.geo2.persistance.repository.function;

import fr.microtec.geo2.persistance.GeoStringArrayType;
import org.hibernate.procedure.internal.ProcedureCallImpl;
import org.hibernate.procedure.spi.ParameterStrategy;
import org.hibernate.query.procedure.internal.ProcedureParameterImpl;
import org.hibernate.type.ProcedureParameterNamedBinder;
import org.hibernate.type.Type;

import javax.persistence.ParameterMode;
import java.sql.CallableStatement;
import java.sql.SQLException;

public class GeoProcedureParameterArrayImpl<T> extends ProcedureParameterImpl<T> {

    private final ProcedureCallImpl<?> procedureCall;

    public GeoProcedureParameterArrayImpl(ProcedureCallImpl procedureCall, String name, ParameterMode mode, Class<T> javaType, Type hibernateType, boolean initialPassNullsSetting) {
        super(procedureCall, name, mode, javaType, hibernateType, initialPassNullsSetting);
        this.procedureCall = procedureCall;
    }

    public GeoProcedureParameterArrayImpl(ProcedureCallImpl procedureCall, Integer position, ParameterMode mode, Class<T> javaType, Type hibernateType, boolean initialPassNullsSetting) {
        super(procedureCall, position, mode, javaType, hibernateType, initialPassNullsSetting);
        this.procedureCall = procedureCall;
    }

    @Override
    public void prepare(CallableStatement statement, int startIndex) throws SQLException {
        // If GeoStringArrayType parameter type, need override parameter prepare
        if (getParameterType().equals(GeoStringArrayType.class)) {
            int[] sqlTypesToUse = getSqlTypes();
            boolean namedStrategy = this.procedureCall.getParameterStrategy() == ParameterStrategy.NAMED;

            // Register output
            if (getMode() == ParameterMode.INOUT || getMode() == ParameterMode.OUT) {
                if (namedStrategy) {
                    statement.registerOutParameter(getName(), sqlTypesToUse[0], GeoStringArrayType.getOracleTypeName());
                } else {
                    statement.registerOutParameter(startIndex, sqlTypesToUse[0]);
                }
            }

            // Bind input
            if (getMode() == ParameterMode.IN || getMode() == ParameterMode.INOUT) {
                if (namedStrategy) {
                    ((ProcedureParameterNamedBinder) getHibernateType()).nullSafeSet(
                        statement,
                        getBind().getValue(),
                        this.getName(),
                        procedureCall.getSession()
                    );
                } else {
                    getHibernateType().nullSafeSet(statement, getBind().getValue(), startIndex, procedureCall.getSession());
                }
            }
        } else {
            super.prepare(statement, startIndex);
        }
    }
}
