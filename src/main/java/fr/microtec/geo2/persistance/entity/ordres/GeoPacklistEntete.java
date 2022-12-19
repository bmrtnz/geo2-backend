package fr.microtec.geo2.persistance.entity.ordres;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import fr.microtec.geo2.persistance.converter.BooleanIntegerConverter;
import fr.microtec.geo2.persistance.entity.common.GeoTypeTiers;
import lombok.Data;

@Data
@Table(name = "geo_packlist_entete")
@Entity
public class GeoPacklistEntete {

    @Id
    @Column(name = "ref_packlist")
    @GeneratedValue(generator = "GeoPacklistGenerator")
    @GenericGenerator(name = "GeoPacklistGenerator", strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator", parameters = {
            @Parameter(name = "sequenceName", value = "seq_pack_list"),
    })
    private Integer id;

    @NotNull
    @Column(name = "date_dep", nullable = false)
    private LocalDateTime depart;

    @NotNull
    @Column(name = "date_liv", nullable = false)
    private LocalDateTime livraison;

    @NotNull
    @Column(name = "date_imp", nullable = false)
    private LocalDateTime impression;

    @NotNull
    @Column(name = "num_po", nullable = false)
    private String numeroPo;

    @NotNull
    @Column(name = "mail", nullable = false)
    private String mail;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ind_cli_ent", nullable = false)
    private GeoTypeTiers typeTier;

    @Column(name = "ind_traite")
    @Convert(converter = BooleanIntegerConverter.class)
    private Boolean traite;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "entete")
    private List<GeoPacklistOrdre> ordres;

}
