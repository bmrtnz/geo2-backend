package fr.microtec.geo2.persistance.repository.function;

import fr.microtec.geo2.persistance.entity.FunctionResult;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.procedure.ParameterRegistration;
import org.hibernate.procedure.internal.ProcedureCallImpl;
import org.hibernate.query.procedure.internal.ProcedureParameterImpl;
import org.springframework.util.StringUtils;

import javax.persistence.ParameterMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class FunctionQueryImpl<R> extends ProcedureCallImpl<R> implements FunctionQuery {
    private final List<String> outputParameters;
    private String outputCursor;
    private final Map<String, Function<Object, ?>> mappers;

    public FunctionQueryImpl(SharedSessionContractImplementor session, String procedureName) {
        super(session, procedureName);
        this.outputParameters = new ArrayList<>();
        this.mappers = new HashMap<>();
    }

    public FunctionQueryImpl(final SharedSessionContractImplementor session, String procedureName, Class... resultClasses) {
        super(session, procedureName, resultClasses);
        this.outputParameters = new ArrayList<>();
        this.mappers = new HashMap<>();
    }

    @Override
    public <T> FunctionQuery attachInput(String name, Class<T> type, Object value) {
        return this.attach(name, type, ParameterMode.IN, value);
    }

    @Override
    public <T> FunctionQuery attachInputOutput(String name, Class<T> type, Object value) {
        return this.attach(name, type, ParameterMode.INOUT, value);
    }

    @Override
    public FunctionQuery attachOutput(String name, Class<?> type) {
        return this.attach(name, type, ParameterMode.OUT, null);
    }

    @Override
    public FunctionQuery attachOutput(String name, Class<?> type, Function<Object, ?> mapper) {
        mappers.put(name, mapper);
        return this.attach(name, type, ParameterMode.OUT, null);
    }

    @Override
    public FunctionQuery attachCursor(String name) {
        return this.attach(name, void.class, ParameterMode.REF_CURSOR, null);
    }

    @Override
    public <T> FunctionQuery attach(String name, Class<T> type, ParameterMode mode, Object value) {
        this.registerStoredProcedureParameter(name, type, mode);

        if (mode.equals(ParameterMode.IN) || mode.equals(ParameterMode.INOUT)) {
            this.setParameter(name, value);
        }

        if (mode.equals(ParameterMode.INOUT) || mode.equals(ParameterMode.OUT)) {
            this.outputParameters.add(name);
        }

        if (mode.equals(ParameterMode.REF_CURSOR)) {
            this.outputCursor = name;
        }

        return this;
    }

    public FunctionResult fetch() {
        this.execute();

        FunctionResult.FunctionResultBuilder builder = FunctionResult.builder()
                .res((Integer) this.getOutputParameterValue("res"))
                .msg((String) this.getOutputParameterValue("msg"));

        this.getOutputParameters()
                .stream()
                .filter(s -> !(s.equals("res") || s.equals("msg")))
                .forEach(s -> {
                    Object output = this.getOutputParameterValue(s);
                    if(this.mappers.containsKey(s))
                        output = this.mappers.get(s).apply(output);
                    builder.withData(s, output);
                });

        if (StringUtils.hasText(this.outputCursor)) {
            try {
                builder.cursorData((List<Object>) this.getResultList());
            } catch (NullPointerException ex) {
                // NPE can pop with none open cursor
            }
        }

        return builder.build();
    }

    @Override
    public List<String> getOutputParameters() {
        return this.outputParameters;
    }

    /**
     * Register own ProcedureParameter for fix compatibility with Oracle Array.
     */
    @Override
    public <T> ParameterRegistration<T> registerParameter(String name, Class<T> type, ParameterMode mode) {
        final ProcedureParameterImpl<T> parameter = new GeoProcedureParameterArrayImpl<T>(
            this,
            name,
            mode,
            type,
            getSession().getFactory().getTypeResolver().heuristicType( type.getName() ),
            getSession().getFactory().getSessionFactoryOptions().isProcedureParameterNullPassingEnabled()
        );

        // Register parameter
        getParameterMetadata().registerParameter(parameter);

        return parameter;
    }

}
