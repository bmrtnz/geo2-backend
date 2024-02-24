package fr.microtec.geo2.persistance.entity.traductions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeoVarieteTraductionId implements Serializable {

    private String variete;
    private String langue;

}
