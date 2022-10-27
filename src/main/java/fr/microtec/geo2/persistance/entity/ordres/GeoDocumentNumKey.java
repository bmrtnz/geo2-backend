package fr.microtec.geo2.persistance.entity.ordres;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GeoDocumentNumKey implements Serializable {
    protected String ordreNumero;
    protected String typeDocument;
    protected String anneeCreation;
}
