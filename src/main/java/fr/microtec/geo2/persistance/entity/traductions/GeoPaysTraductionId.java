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
public class GeoPaysTraductionId implements Serializable {

    private String pays;
    private String langue;

}
