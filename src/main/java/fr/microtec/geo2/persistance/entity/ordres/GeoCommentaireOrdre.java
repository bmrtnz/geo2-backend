package fr.microtec.geo2.persistance.entity.ordres;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import fr.microtec.geo2.persistance.entity.ModifiedEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "geo_comm_ordre")
// Id-class doesn't work with generated value, also comment @IdClass and @Id on
// ordre field.
// @IdClass(GeoCommentaireOrdreKey.class)
@Entity
public class GeoCommentaireOrdre extends ModifiedEntity {

    @Id
    @Column(name = "comm_ord_ref")
    @GeneratedValue(generator = "GeoCommentaireOrdreGenerator")
    @GenericGenerator(name = "GeoCommentaireOrdreGenerator", strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator", parameters = {
            @Parameter(name = "sequenceName", value = "seq_comm_ordre"),
            @Parameter(name = "mask", value = "FM09999999"),
            @Parameter(name = "isSequence", value = "true")
    })
    private String id;

    // @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ord_ref", nullable = false)
    private GeoOrdre ordre;

    @Column(name = "comm_ordre")
    private String commentaires;

}
