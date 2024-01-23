package fr.microtec.geo2.persistance.entity.ordres;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeoDocumentNumKey implements Serializable {
    protected String ordreNumero;
    protected String typeDocument;
    protected String anneeCreation;
}
