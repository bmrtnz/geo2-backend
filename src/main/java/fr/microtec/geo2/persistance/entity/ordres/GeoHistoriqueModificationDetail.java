package fr.microtec.geo2.persistance.entity.ordres;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "GEO_HISTO_MODIF_DETAIL")
@Entity
public class GeoHistoriqueModificationDetail extends ModifiedEntity {

    @Id
    @Column(name = "histo_orx_ref")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orx_ref")
    private GeoOrdreLogistique logistique;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ord_ref")
    private GeoOrdre ordre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orl_ref")
    private GeoOrdreLigne ligne;

    @Column(name = "exp_nb_col_old")
    private Float nombreColisExpediesAvant;

    @Column(name = "exp_nb_col_new")
    private Float nombreColisExpediesApres;

    @Column(name = "exp_nb_pal_old")
    private Float nombrePalettesExpedieesAvant;

    @Column(name = "exp_nb_pal_new")
    private Float nombrePalettesExpedieesApres;

    @Column(name = "exp_pds_brut_old")
    private Float poidsBrutExpedieAvant;

    @Column(name = "exp_pds_brut_new")
    private Float poidsBrutExpedieApres;

    @Column(name = "exp_pds_net_old")
    private Float poidsNetExpedieAvant;

    @Column(name = "exp_pds_net_new")
    private Float poidsNetExpedieApres;

}
