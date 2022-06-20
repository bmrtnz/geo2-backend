package fr.microtec.geo2.persistance.entity;

import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class FunctionResult {
    public final static int RESULT_UNKNOWN = 0;
    public final static int RESULT_OK = 1;
    public final static int RESULT_WARNING = 2;

    @Id
    private Integer res;

    private String msg;

    @Transient
    @Singular("withData")
    private Map<String, Object> data;

    @Transient
    private List<Object> cursorData;

    public <T> List<T> getCursorDataAs(Class<T> clazz) {
        return (List<T>) this.getCursorData();
    }
}
