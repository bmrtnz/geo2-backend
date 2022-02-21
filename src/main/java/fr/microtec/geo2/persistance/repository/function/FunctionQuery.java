package fr.microtec.geo2.persistance.repository.function;

import fr.microtec.geo2.persistance.entity.FunctionResult;

import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.util.List;
import java.util.function.Function;

public interface FunctionQuery extends StoredProcedureQuery {

    List<String> getOutputParameters();
    <T> FunctionQuery attachInput(String name, Class<T> type, T value);
    <T> FunctionQuery attachInputOutput(String name, Class<T> type, T value);
    FunctionQuery attachOutput(String name, Class<?> type);
    FunctionQuery attachOutput(String name, Class<?> type, Function<Object, ?> mapper);
    FunctionQuery attachCursor(String name);
    <T> FunctionQuery attach(String name, Class<T> type, ParameterMode mode, T value);

    FunctionResult fetch();

}
