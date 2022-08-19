package fr.microtec.geo2.persistance.repository.function;

import fr.microtec.geo2.persistance.GeoStringArrayType;
import org.hibernate.procedure.internal.ProcedureCallImpl;
import org.hibernate.query.procedure.internal.ProcedureParameterImpl;
import org.hibernate.type.Type;

import javax.persistence.ParameterMode;
import java.sql.CallableStatement;
import java.sql.SQLException;

public class GeoProcedureParameterArrayImpl<T> extends ProcedureParameterImpl<T> {

    public GeoProcedureParameterArrayImpl(ProcedureCallImpl procedureCall, String name, ParameterMode mode, Class<T> javaType, Type hibernateType, boolean initialPassNullsSetting) {
        super(procedureCall, name, mode, javaType, hibernateType, initialPassNullsSetting);
    }

    public GeoProcedureParameterArrayImpl(ProcedureCallImpl procedureCall, Integer position, ParameterMode mode, Class<T> javaType, Type hibernateType, boolean initialPassNullsSetting) {
        super(procedureCall, position, mode, javaType, hibernateType, initialPassNullsSetting);
    }

    @Override
    public void prepare(CallableStatement statement, int startIndex) throws SQLException {
        int[] sqlTypesToUse = getSqlTypes();

        if (getParameterType().equals(GeoStringArrayType.class)) {
            statement.registerOutParameter( getName(), sqlTypesToUse[0], "P_STR_TAB_TYPE" );
        } else {
            super.prepare(statement, startIndex);
        }
    }
}
