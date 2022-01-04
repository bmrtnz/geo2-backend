package fr.microtec.geo2.persistance.entity;

import lombok.*;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class FunctionResult {
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
