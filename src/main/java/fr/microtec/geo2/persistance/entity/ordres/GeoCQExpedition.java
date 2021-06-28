package fr.microtec.geo2.persistance.entity.ordres;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_cqexp")
@Entity
public class GeoCQExpedition extends ModifiedEntity {

  @EmbeddedId
  private GeoCQExpeditionPK id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "orl_ref")
  private GeoOrdreLigne ordreLigne;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cql_ref", insertable = false, updatable = false)
  private GeoCQLigne ligne;

  @Column(name = "ok_type_pal")
  private Integer typePaletteOK;

  @Column(name = "ok_etat_pal")
  private Integer etatPaletteOK;

  @Column(name = "ok_pal_pcf")
  private Integer PCFOK;

  @Column(name = "ok_type_col")
  private Integer typeColisOK;

  @Column(name = "ok_nbr_col")
  private Integer nombreColisOK;

  @Column(name = "ok_fiche_pal")
  private Integer fichePaletteOK;

  @Column(name = "ok_etiq_col")
  private Integer etiquetteColisOK;

  @Column(name = "ok_lis_etiq_c")
  private Integer lisibilitéEtiquetteColisOK;

  @Column(name = "ok_type_bel")
  private Integer typeBoxEndLabelOK;

  @Column(name = "ok_lis_bel")
  private Integer lisibiliteBoxEndLabelOK;

  @Column(name = "ok_type_sac")
  private Integer typeSacOK;

  @Column(name = "ok_variete")
  private Integer varieteOK;

  @Column(name = "ok_nbr_fruit")
  private Integer nombreFruitsOK;

  @Column(name = "ok_type_etiq_sac")
  private Integer typeEtiquetteSacOK;

  @Column(name = "ok_lis_etiq_sac")
  private Integer lisibiliteEtiquetteOK;

  @Column(name = "ok_nbr_uc_col")
  private Integer nombreUCColisOK;

  @Column(name = "ok_homo_col")
  private Integer homogeneiteColisOK;

}

@Embeddable
final class GeoCQExpeditionPK implements Serializable {

  @Column(name = "cql_ref")
  private String id;

}