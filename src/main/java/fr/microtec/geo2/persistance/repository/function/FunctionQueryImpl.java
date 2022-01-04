package fr.microtec.geo2.persistance.repository.function;

import fr.microtec.geo2.persistance.entity.FunctionResult;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.procedure.internal.ProcedureCallImpl;
import org.springframework.util.StringUtils;

import javax.persistence.ParameterMode;
import java.util.ArrayList;
import java.util.List;

public class FunctionQueryImpl<R> extends ProcedureCallImpl<R> implements FunctionQuery {
    private final List<String> outputParameters;
    private String outputCursor;

    public FunctionQueryImpl(SharedSessionContractImplementor session, String procedureName) {
        super(session, procedureName);
        this.outputParameters = new ArrayList<>();
    }

    public FunctionQueryImpl(final SharedSessionContractImplementor session, String procedureName, Class... resultClasses) {
        super(session, procedureName, resultClasses);
        this.outputParameters = new ArrayList<>();
    }

    @Override
    public <T> FunctionQuery attachInput(String name, Class<T> type, T value) {
        return this.attach(name, type, ParameterMode.IN, value);
    }

    @Override
    public <T> FunctionQuery attachInputOutput(String name, Class<T> type, T value) {
        return this.attach(name, type, ParameterMode.INOUT, value);
    }

    @Override
    public FunctionQuery attachOutput(String name, Class<?> type) {
        return this.attach(name, type, ParameterMode.OUT, null);
    }

    @Override
    public FunctionQuery attachCursor(String name) {
        return this.attach(name, void.class, ParameterMode.REF_CURSOR, null);
    }

    @Override
    public <T> FunctionQuery attach(String name, Class<T> type, ParameterMode mode, T value) {
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
                .forEach(s -> builder.withData(s, this.getOutputParameterValue(s)));

        if (StringUtils.hasText(this.outputCursor)) {
            builder.cursorData((List<Object>) this.getResultList());
        }

        return builder.build();
    }

    @Override
    public List<String> getOutputParameters() {
        return this.outputParameters;
    }
}
