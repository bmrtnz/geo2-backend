package fr.microtec.geo2.persistance.entity.ordres;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import fr.microtec.geo2.persistance.converter.BooleanIntegerConverter;
import lombok.Data;

@Data
@Table(name = "geo_packlist_entete")
@Entity
public class GeoPacklistEntete {

    @Id
    @Column(name = "ref_packlist")
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
    @Column(name = "ind_cli_ent", nullable = false)
    private Boolean cliEnt;

    @Column(name = "ind_traite")
    @Convert(converter = BooleanIntegerConverter.class)
    private Boolean traite;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "entete")
    private List<GeoPacklistOrdre> ordres;

}
